package co.exploracolombia.presentation.reviews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.SiteReview
import co.exploracolombia.presentation.theme.RutaColors

/**
 * Reseñas de la comunidad para un hito: lectura pública real (cualquier
 * usuario ve las de todos), escritura solo de la propia — ver la migración
 * `site_reviews` y `SupabaseReviewsApi`. No es una lista simulada en el
 * dispositivo: cada reseña viaja al backend.
 */
@Composable
fun ReviewsSection(viewModel: ReviewsViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Reseñas de la comunidad",
            color = RutaColors.JungleGreenDark,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(10.dp))

        when (val state = uiState) {
            is ReviewsUiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            is ReviewsUiState.Error -> Text(state.message, color = RutaColors.StoneGrey, style = MaterialTheme.typography.bodyMedium)
            is ReviewsUiState.Loaded -> {
                if (state.reviews.isEmpty()) {
                    Text(
                        "Todavía nadie ha dejado una reseña — sé la primera persona en contar cómo fue tu visita.",
                        color = RutaColors.StoneGrey,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    val average = state.reviews.map { it.rating }.average()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StarRow(rating = Math.round(average).toInt(), size = 18.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "%.1f · %d reseña%s".format(average, state.reviews.size, if (state.reviews.size == 1) "" else "s"),
                            color = RutaColors.StoneGrey,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    state.reviews.forEach { review ->
                        ReviewRow(review)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        ReviewComposer(isSubmitting = isSubmitting, onSubmit = viewModel::submitReview)
    }
}

@Composable
private fun ReviewRow(review: SiteReview) {
    Surface(
        color = RutaColors.JungleGreen.copy(alpha = 0.06f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(review.authorName, color = RutaColors.JungleGreenDark, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                StarRow(rating = review.rating, size = 14.dp)
            }
            if (!review.reviewText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(review.reviewText, color = RutaColors.StoneGrey, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ReviewComposer(isSubmitting: Boolean, onSubmit: (Int, String?) -> Unit) {
    var rating by remember { mutableIntStateOf(0) }
    var text by remember { mutableStateOf("") }

    Column {
        Text("Deja tu puntuación", color = RutaColors.JungleGreenDark, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(6.dp))
        StarPicker(rating = rating, onRatingChange = { rating = it })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { if (it.length <= 500) text = it },
            placeholder = { Text("Cuenta cómo fue tu visita (opcional)") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSubmit(rating, text) },
            enabled = rating > 0 && !isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Publicar reseña")
            }
        }
    }
}

@Composable
private fun StarPicker(rating: Int, onRatingChange: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = "$i estrellas",
                tint = RutaColors.Gold,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onRatingChange(i) }
                    .padding(2.dp),
            )
        }
    }
}

@Composable
private fun StarRow(rating: Int, size: androidx.compose.ui.unit.Dp) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = RutaColors.Gold,
                modifier = Modifier.size(size),
            )
        }
    }
}
