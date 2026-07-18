// ============================================================================
// Supabase Edge Function: validate-visit
// Deploy: supabase functions deploy validate-visit --no-verify-jwt=false
//
// Endpoint de validación de visita. Recibe GPS + foto en base64, valida
// geofencing, llama a Google Cloud Vision (label detection) para confirmar
// que la foto corresponde al hito, y si todo pasa: sube la foto a Storage,
// registra la visita, otorga XP/insignia y devuelve la narrativa localizada.
//
// Requiere variables de entorno (supabase secrets set ...):
//   SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY, GOOGLE_VISION_API_KEY
// ============================================================================

import { serve } from "https://deno.land/std@0.220.0/http/server.ts";
import { createClient, SupabaseClient } from "https://esm.sh/@supabase/supabase-js@2.45.0";

type LangCode = "es" | "en" | "fr" | "ru";
const SUPPORTED_LANGS: LangCode[] = ["es", "en", "fr", "ru"];

interface ValidateVisitRequest {
  site_id: string;
  lat: number;
  lng: number;
  photo_base64: string; // sin el prefijo "data:image/jpeg;base64,"
  lang?: LangCode;
}

interface VisionLabel {
  description: string;
  score: number;
}

const EARTH_RADIUS_M = 6371000;

function haversineDistanceMeters(
  lat1: number,
  lng1: number,
  lat2: number,
  lng2: number,
): number {
  const toRad = (deg: number) => (deg * Math.PI) / 180;
  const dLat = toRad(lat2 - lat1);
  const dLng = toRad(lng2 - lng1);
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLng / 2) ** 2;
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return EARTH_RADIUS_M * c;
}

function badRequest(message: string): Response {
  return new Response(JSON.stringify({ error: message }), {
    status: 400,
    headers: { "Content-Type": "application/json" },
  });
}

function parseRequestBody(body: unknown): ValidateVisitRequest {
  if (typeof body !== "object" || body === null) {
    throw new Error("Cuerpo de la solicitud inválido");
  }
  const b = body as Record<string, unknown>;
  if (typeof b.site_id !== "string" || b.site_id.length === 0) {
    throw new Error("site_id es requerido");
  }
  if (typeof b.lat !== "number" || typeof b.lng !== "number") {
    throw new Error("lat y lng deben ser numéricos");
  }
  if (Math.abs(b.lat) > 90 || Math.abs(b.lng) > 180) {
    throw new Error("Coordenadas GPS fuera de rango");
  }
  if (typeof b.photo_base64 !== "string" || b.photo_base64.length < 100) {
    throw new Error("photo_base64 es requerido y debe ser una imagen válida");
  }
  const lang = SUPPORTED_LANGS.includes(b.lang as LangCode)
    ? (b.lang as LangCode)
    : "es";
  return {
    site_id: b.site_id,
    lat: b.lat,
    lng: b.lng,
    photo_base64: b.photo_base64,
    lang,
  };
}

// --- Google Cloud Vision: label detection -----------------------------------
// Sentinel para QA local: nadie configura su GOOGLE_VISION_API_KEY real con
// este valor por accidente, así que es seguro usarlo como interruptor de
// modo mock sin arriesgar que quede prendido en producción. Permite probar
// el camino de éxito completo (geofencing -> Vision -> Storage -> XP ->
// narrativa localizada) sin gastar cuota de Google ni depender de que una
// foto real reciba una etiqueta con buena confianza.
const MOCK_VISION_SENTINEL = "mock-local-testing";

const MOCK_VISION_LABELS: VisionLabel[] = [
  { description: "fortress", score: 0.93 },
  { description: "wall", score: 0.87 },
  { description: "stone wall", score: 0.81 },
  { description: "architecture", score: 0.76 },
];

async function callVisionApi(
  photoBase64: string,
  apiKey: string,
): Promise<VisionLabel[]> {
  if (apiKey === MOCK_VISION_SENTINEL) {
    return MOCK_VISION_LABELS;
  }

  const res = await fetch(
    `https://vision.googleapis.com/v1/images:annotate?key=${apiKey}`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        requests: [
          {
            image: { content: photoBase64 },
            features: [{ type: "LABEL_DETECTION", maxResults: 15 }],
          },
        ],
      }),
    },
  );

  if (!res.ok) {
    throw new Error(`Vision API respondió ${res.status}: ${await res.text()}`);
  }

  const json = await res.json();
  const annotations = json?.responses?.[0]?.labelAnnotations ?? [];
  return annotations.map((a: { description: string; score: number }) => ({
    description: a.description.toLowerCase(),
    score: a.score,
  }));
}

function scoreVisionMatch(
  detected: VisionLabel[],
  expectedLabels: string[],
  minConfidence: number,
): { passed: boolean; confidence: number } {
  if (expectedLabels.length === 0) {
    // Sin referencia configurada: acepta si hay al menos una etiqueta con
    // confianza razonable (fallback conservador, no bloquea el MVP).
    const best = detected[0]?.score ?? 0;
    return { passed: best >= 0.5, confidence: best };
  }

  const expectedSet = expectedLabels.map((l) => l.toLowerCase());
  let bestMatch = 0;
  for (const label of detected) {
    for (const expected of expectedSet) {
      if (label.description.includes(expected) || expected.includes(label.description)) {
        bestMatch = Math.max(bestMatch, label.score);
      }
    }
  }
  return { passed: bestMatch >= minConfidence, confidence: bestMatch };
}

// --- Handler ------------------------------------------------------------
serve(async (req: Request) => {
  if (req.method !== "POST") {
    return badRequest("Método no permitido, use POST");
  }

  const authHeader = req.headers.get("Authorization");
  if (!authHeader) {
    return new Response(JSON.stringify({ error: "No autenticado" }), {
      status: 401,
      headers: { "Content-Type": "application/json" },
    });
  }

  const supabaseUrl = Deno.env.get("SUPABASE_URL")!;
  const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
  const visionApiKey = Deno.env.get("GOOGLE_VISION_API_KEY")!;

  // Cliente con el JWT del usuario, solo para resolver quién llama.
  const userClient: SupabaseClient = createClient(supabaseUrl, serviceRoleKey, {
    global: { headers: { Authorization: authHeader } },
  });
  const { data: userData, error: authError } = await userClient.auth.getUser();
  if (authError || !userData.user) {
    return new Response(JSON.stringify({ error: "Token inválido" }), {
      status: 401,
      headers: { "Content-Type": "application/json" },
    });
  }
  const userId = userData.user.id;

  // Cliente admin (service_role) para las escrituras del logro/XP.
  const admin: SupabaseClient = createClient(supabaseUrl, serviceRoleKey);

  let payload: ValidateVisitRequest;
  try {
    payload = parseRequestBody(await req.json());
  } catch (err) {
    return badRequest((err as Error).message);
  }

  const { data: site, error: siteError } = await admin
    .from("historical_sites")
    .select("*")
    .eq("id", payload.site_id)
    .eq("is_active", true)
    .single();

  if (siteError || !site) {
    return badRequest("Sitio histórico no encontrado o inactivo");
  }

  // 1. Verificar si ya fue visitado (idempotencia: no duplicar XP).
  const { data: existingVisit } = await admin
    .from("user_visits")
    .select("id")
    .eq("user_id", userId)
    .eq("site_id", payload.site_id)
    .maybeSingle();

  if (existingVisit) {
    return new Response(
      JSON.stringify({ error: "Este hito ya fue desbloqueado previamente", code: "ALREADY_VISITED" }),
      { status: 409, headers: { "Content-Type": "application/json" } },
    );
  }

  // 2. Geofencing: distancia real vs radio permitido del sitio.
  const distanceM = haversineDistanceMeters(payload.lat, payload.lng, site.lat, site.lng);
  if (distanceM > site.geofence_radius_m) {
    return new Response(
      JSON.stringify({
        error: `Estás a ${Math.round(distanceM)}m del hito. Acércate a menos de ${site.geofence_radius_m}m.`,
        code: "OUT_OF_RANGE",
        distance_m: Math.round(distanceM),
      }),
      { status: 422, headers: { "Content-Type": "application/json" } },
    );
  }

  // 3. Validación visual con IA.
  const visionRef = site.vision_reference as {
    expected_labels?: string[];
    min_confidence?: number;
  } ?? {};

  let detectedLabels: VisionLabel[];
  try {
    detectedLabels = await callVisionApi(payload.photo_base64, visionApiKey);
  } catch (err) {
    console.error("Error llamando a Vision API:", err);
    return new Response(
      JSON.stringify({ error: "No se pudo analizar la imagen, intenta de nuevo", code: "VISION_UNAVAILABLE" }),
      { status: 502, headers: { "Content-Type": "application/json" } },
    );
  }

  const { passed, confidence } = scoreVisionMatch(
    detectedLabels,
    visionRef.expected_labels ?? [],
    visionRef.min_confidence ?? 0.55,
  );

  if (!passed) {
    return new Response(
      JSON.stringify({
        error: "La foto no coincide con el monumento esperado. Intenta con otro ángulo.",
        code: "PHOTO_MISMATCH",
        confidence,
        detected_labels: detectedLabels.slice(0, 5).map((l) => l.description),
      }),
      { status: 422, headers: { "Content-Type": "application/json" } },
    );
  }

  // 4. Subir la foto a Storage (bucket "visit-photos").
  const photoBytes = Uint8Array.from(atob(payload.photo_base64), (c) => c.charCodeAt(0));
  const photoPath = `${userId}/${site.id}-${Date.now()}.jpg`;
  const { error: uploadError } = await admin.storage
    .from("visit-photos")
    .upload(photoPath, photoBytes, { contentType: "image/jpeg", upsert: false });

  if (uploadError) {
    console.error("Error subiendo foto:", uploadError);
    return new Response(
      JSON.stringify({ error: "No se pudo guardar la foto" }),
      { status: 500, headers: { "Content-Type": "application/json" } },
    );
  }
  const { data: publicUrlData } = admin.storage.from("visit-photos").getPublicUrl(photoPath);

  // 5. Registrar la visita.
  const { error: insertError } = await admin.from("user_visits").insert({
    user_id: userId,
    site_id: site.id,
    captured_lat: payload.lat,
    captured_lng: payload.lng,
    distance_m: distanceM,
    photo_url: publicUrlData.publicUrl,
    vision_confidence: confidence,
    vision_labels: detectedLabels.slice(0, 10),
    validation_passed: true,
    xp_awarded: site.xp_reward,
  });

  if (insertError) {
    console.error("Error insertando visita:", insertError);
    return new Response(
      JSON.stringify({ error: "No se pudo registrar la visita" }),
      { status: 500, headers: { "Content-Type": "application/json" } },
    );
  }

  // 6. Otorgar XP acumulado (lee-modifica-escribe protegido por unique en user_visits,
  //    que ya impide doble conteo del mismo sitio).
  const { data: profile } = await admin
    .from("profiles")
    .select("xp")
    .eq("id", userId)
    .single();

  const newXp = (profile?.xp ?? 0) + site.xp_reward;
  await admin
    .from("profiles")
    .update({ xp: newXp, last_visit_at: new Date().toISOString() })
    .eq("id", userId);

  // 7. Otorgar insignia asociada al sitio, si tiene una y el usuario no la tiene.
  let newBadge: Record<string, unknown> | null = null;
  if (site.badge_id) {
    const { data: alreadyHasBadge } = await admin
      .from("user_badges")
      .select("badge_id")
      .eq("user_id", userId)
      .eq("badge_id", site.badge_id)
      .maybeSingle();

    if (!alreadyHasBadge) {
      await admin.from("user_badges").insert({
        user_id: userId,
        badge_id: site.badge_id,
        source_site_id: site.id,
      });
      const { data: badgeData } = await admin
        .from("badges")
        .select("*")
        .eq("id", site.badge_id)
        .single();
      newBadge = badgeData;
    }
  }

  // 8. Construir payload de respuesta localizado.
  const lang = payload.lang!;
  const responseBody = {
    success: true,
    site: {
      id: site.id,
      title: site[`title_${lang}`],
      narrative: site[`narrative_${lang}`],
    },
    xp_awarded: site.xp_reward,
    total_xp: newXp,
    photo_url: publicUrlData.publicUrl,
    badge: newBadge
      ? {
          id: newBadge.id,
          code: newBadge.code,
          icon_url: newBadge.icon_url,
          rarity: newBadge.rarity,
          name: newBadge[`name_${lang}`],
          description: newBadge[`description_${lang}`],
        }
      : null,
  };

  return new Response(JSON.stringify(responseBody), {
    status: 200,
    headers: { "Content-Type": "application/json" },
  });
});
