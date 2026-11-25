package com.babeena.myapplication.data.remote

import com.babeena.myapplication.domain.model.Event

sealed interface EventDetailsResult {
    data object Loading : EventDetailsResult
    data class Success(val data: Event) : EventDetailsResult
    data class Failure(val errorMessage: String) : EventDetailsResult
}