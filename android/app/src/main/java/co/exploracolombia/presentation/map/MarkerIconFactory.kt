package co.exploracolombia.presentation.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.core.graphics.drawable.toDrawable
import co.exploracolombia.domain.model.BadgeRarity

/**
 * osmdroid dibuja el mapa con la Canvas de android.graphics clásica (View),
 * no con la de Compose — por eso los marcadores no pueden ser un
 * @Composable normal. En vez de un ícono suelto con la etiqueta apareciendo
 * solo al tocar (comportamiento por defecto de InfoWindow en osmdroid), se
 * "hornea" el pin + el nombre del sitio en un único Bitmap, así el texto
 * siempre está visible sin necesidad de interacción, tal como se pidió.
 */
object MarkerIconFactory {

    fun createLabeledPin(
        context: Context,
        label: String,
        rarity: BadgeRarity,
        locked: Boolean,
    ): android.graphics.drawable.Drawable {
        val density = context.resources.displayMetrics.density
        val pinSize = (40 * density)
        val labelPaddingH = (10 * density)
        val labelPaddingV = (5 * density)
        val gap = (4 * density)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#211C13")
            textSize = 13 * density
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        val textWidth = textPaint.measureText(label)
        val labelWidth = textWidth + labelPaddingH * 2
        val labelHeight = (13 * density) + labelPaddingV * 2

        val totalWidth = maxOf(pinSize, labelWidth).toInt() + (4 * density).toInt()
        val totalHeight = (pinSize + gap + labelHeight).toInt()

        val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = totalWidth / 2f

        drawPinDrop(canvas, centerX, pinSize, rarity, locked)

        val labelTop = pinSize + gap
        val labelRect = RectF(
            centerX - labelWidth / 2f,
            labelTop,
            centerX + labelWidth / 2f,
            labelTop + labelHeight,
        )
        val chipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(if (locked) "#E1D7BE" else "#FBF7EE")
        }
        canvas.drawRoundRect(labelRect, 8 * density, 8 * density, chipPaint)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#A97B25")
            style = Paint.Style.STROKE
            strokeWidth = 1.2f * density
        }
        canvas.drawRoundRect(labelRect, 8 * density, 8 * density, borderPaint)

        val textBaseline = labelRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(label, centerX, textBaseline, textPaint)

        return bitmap.toDrawable(context.resources)
    }

    private fun drawPinDrop(canvas: Canvas, centerX: Float, pinSize: Float, rarity: BadgeRarity, locked: Boolean) {
        val fillColor = if (locked) Color.parseColor("#8A8375") else rarityColor(rarity)
        val radius = pinSize * 0.32f
        val tipY = pinSize * 0.98f
        val bulbCenterY = pinSize * 0.36f

        val path = Path().apply {
            moveTo(centerX, tipY)
            lineTo(centerX - radius * 0.85f, bulbCenterY + radius * 0.55f)
            addCircle(centerX, bulbCenterY, radius, Path.Direction.CW)
            lineTo(centerX + radius * 0.85f, bulbCenterY + radius * 0.55f)
            close()
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fillColor }
        canvas.drawPath(path, fillPaint)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4A3610")
            style = Paint.Style.STROKE
            strokeWidth = pinSize * 0.035f
        }
        canvas.drawPath(path, strokePaint)

        val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (locked) Color.parseColor("#5B5344") else Color.WHITE
        }
        canvas.drawCircle(centerX, bulbCenterY, radius * 0.4f, corePaint)
    }

    /** Punto azul simple para "tu ubicación" — deliberadamente distinto de los pines dorados de los hitos. */
    fun createUserDot(context: Context): android.graphics.drawable.Drawable {
        val density = context.resources.displayMetrics.density
        val size = (22 * density).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val center = size / 2f

        val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#402F80ED") }
        canvas.drawCircle(center, center, center, haloPaint)

        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#2F80ED") }
        canvas.drawCircle(center, center, center * 0.5f, dotPaint)

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 2f * density
        }
        canvas.drawCircle(center, center, center * 0.5f, borderPaint)

        return bitmap.toDrawable(context.resources)
    }

    private fun rarityColor(rarity: BadgeRarity): Int = when (rarity) {
        BadgeRarity.LEGENDARY -> Color.parseColor("#B8860B")
        BadgeRarity.EPIC -> Color.parseColor("#7B2FF7")
        BadgeRarity.RARE -> Color.parseColor("#2F80ED")
        BadgeRarity.COMMON -> Color.parseColor("#3A7D5C")
    }
}
