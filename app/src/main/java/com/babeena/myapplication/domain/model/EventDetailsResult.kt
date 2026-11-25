package com.babeena.myapplication.domain.model

sealed interface EventDetailsResult {
    data object Loading : EventDetailsResult
    data class Success(val data: Event) : EventDetailsResult
    data class Failure(val errorMessage: String) : EventDetailsResult
}