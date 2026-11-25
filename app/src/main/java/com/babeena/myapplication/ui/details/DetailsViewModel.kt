package com.babeena.myapplication.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babeena.myapplication.data.remote.EventDetailsResult
import com.babeena.myapplication.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class DetailsViewModel @Inject constructor(private val eventRepository: EventRepository) :
    ViewModel() {
    private val _detailsUiState = MutableStateFlow(DetailsUiState())

    val detailsUiState: StateFlow<DetailsUiState> = _detailsUiState

    fun getEventDetails(eventId: String) {
        viewModelScope.launch {
            _detailsUiState.value = _detailsUiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                when (val result = eventRepository.getEventDetails(eventId)) {

                    is EventDetailsResult.Success -> {
                        _detailsUiState.value = _detailsUiState.value.copy(
                            isLoading = false,
                            event = result.data
                        )
                    }

                    is EventDetailsResult.Failure -> {
                        _detailsUiState.value = _detailsUiState.value.copy(
                            isLoading = false,
                            errorMessage = result.errorMessage
                        )
                    }
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detailsUiState.value = _detailsUiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unable to load event details"
                )
            }
        }
    }
}