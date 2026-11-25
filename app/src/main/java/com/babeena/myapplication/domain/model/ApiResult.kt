package com.babeena.myapplication.domain.model

sealed interface ApiResult {
    data object Loading : ApiResult
    data class Success(val data: List<Event>) : ApiResult
    data class Failure(val errorMessage: String) : ApiResult
}