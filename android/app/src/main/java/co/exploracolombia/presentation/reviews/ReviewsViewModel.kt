package co.exploracolombia.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.exploracolombia.domain.model.SiteReview
import co.exploracolombia.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ReviewsUiState {
    data object Loading : ReviewsUiState
    data class Loaded(val reviews: List<SiteReview>) : ReviewsUiState
    data class Error(val message: String) : ReviewsUiState
}

class ReviewsViewModel(
    private val siteId: String,
    private val repository: ReviewRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewsUiState>(ReviewsUiState.Loading)
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = ReviewsUiState.Loading
            repository.fetchReviews(siteId)
                .onSuccess { reviews -> _uiState.value = ReviewsUiState.Loaded(reviews) }
                .onFailure {
                    _uiState.value = ReviewsUiState.Error(
                        "No se pudieron cargar las reseñas. Revisa tu conexión.",
                    )
                }
        }
    }

    fun submitReview(rating: Int, reviewText: String?) {
        if (_isSubmitting.value) return
        viewModelScope.launch {
            _isSubmitting.update { true }
            repository.submitReview(siteId, rating, reviewText?.takeIf { it.isNotBlank() })
                .onSuccess { refresh() }
            _isSubmitting.update { false }
        }
    }
}
