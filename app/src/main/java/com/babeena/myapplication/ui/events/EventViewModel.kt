package com.babeena.myapplication.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babeena.myapplication.data.remote.ApiResult
import com.babeena.myapplication.domain.repository.EventRepository
import com.babeena.myapplication.utils.LocationHelper
import com.babeena.myapplication.utils.UserLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class EventViewModel@Inject constructor(
    private val eventRepository: EventRepository,
    private val locationHelper: LocationHelper,

    ) :ViewModel(){
    private val _eventUiState = MutableStateFlow(EventUiState())

    var eventUiState: StateFlow<EventUiState> = _eventUiState

    private val _userLocation =
        MutableStateFlow<UserLocation?>(null)

    val userLocation = _userLocation.asStateFlow()


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

    fun fetchUserLocation() {
        viewModelScope.launch {
            _userLocation.value =
                locationHelper.getCurrentLocation()
        }
    }

    init {
        getAllEvents()
    }


}