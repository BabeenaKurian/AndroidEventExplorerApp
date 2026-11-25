package com.babeena.myapplication.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babeena.myapplication.data.remote.ApiResult
import com.babeena.myapplication.domain.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class EventViewModel@Inject constructor(
    private val eventRepository: EventRepository
) :ViewModel(){
    private val _eventUiState = MutableStateFlow(EventUiState())

    var eventUiState: StateFlow<EventUiState> = _eventUiState

    fun getAllEvents() {
        viewModelScope.launch {
            _eventUiState.value = _eventUiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            try {
                when (val result = eventRepository.getAllEvents()) {
                    is ApiResult.Success -> {
                        _eventUiState.value = _eventUiState.value.copy(
                            isLoading = false,
                            data = result.data.orEmpty(),
                            errorMessage = null
                        )
                    }
                    is ApiResult.Failure -> {
                        _eventUiState.value = _eventUiState.value.copy(
                            isLoading = false,
                            errorMessage = result.errorMessage
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _eventUiState.value = _eventUiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unable to load events"
                )
            }
        }
    }


}