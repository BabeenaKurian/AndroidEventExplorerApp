package com.babeena.myapplication.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babeena.myapplication.domain.model.ApiResult
import com.babeena.myapplication.domain.model.Event
import com.babeena.myapplication.domain.repository.BookMarkRepository
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
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val locationHelper: LocationHelper,
    private val bookmarkRepository: BookMarkRepository
) : ViewModel() {
    private val _eventUiState = MutableStateFlow(EventUiState())
    val eventUiState: StateFlow<EventUiState> = _eventUiState.asStateFlow()
    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation.asStateFlow()
    private val _bookmarkedEventIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedEventIds: StateFlow<Set<String>> = _bookmarkedEventIds.asStateFlow()
    fun getAllEvents() {
        viewModelScope.launch {
            _eventUiState.value = _eventUiState.value.copy(
                isLoading = true, errorMessage = null
            )
            try {
                when (val result = eventRepository.getAllEvents()) {
                    is ApiResult.Loading -> {
                        _eventUiState.value = _eventUiState.value.copy(
                            isLoading = true, errorMessage = null
                        )
                    }
                    is ApiResult.Success -> {
                        _eventUiState.value = _eventUiState.value.copy(
                            isLoading = false, data = result.data, errorMessage = null
                        )
                    }
                    is ApiResult.Failure -> {
                        _eventUiState.value = _eventUiState.value.copy(
                            isLoading = false, errorMessage = result.errorMessage
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _eventUiState.value = _eventUiState.value.copy(
                    isLoading = false, errorMessage = e.message ?: LOAD_EVENTS_ERROR
                )
            }
        }
    }

    fun fetchUserLocation() {
        viewModelScope.launch {
            try {
                _userLocation.value = locationHelper.getCurrentLocation()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _userLocation.value = null
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.getAllBookMarkedItems().collect { bookmark ->
                _bookmarkedEventIds.value = bookmark.map { it.id }.toSet()
            }
        }
    }

    fun toggleBookmark(event: Event) {
        if (event.id in _bookmarkedEventIds.value) {
            removeBookmark(event.id)
        } else {
            addBookmark(event)
        }
    }

    fun addBookmark(event: Event) {
        viewModelScope.launch {
            bookmarkRepository.addBookMarkedItem(event)
        }
    }

    fun removeBookmark(eventId: String) {
        viewModelScope.launch {
            bookmarkRepository.removeBookMarkedEvent(eventId)
        }
    }

    init {
        getAllEvents()
        observeBookmarks()
    }

    companion object {
        private const val LOAD_EVENTS_ERROR = "Unable to load events"
    }
}