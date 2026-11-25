package com.babeena.myapplication.ui.events

import com.babeena.myapplication.domain.model.Event

data class EventUiState(
    val data: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
