package com.babeena.myapplication.data.remote

import com.babeena.myapplication.domain.model.Event

sealed interface ApiResult {
    data class Success(
        val data: List<Event>?
    ) : ApiResult

    data class Failure(
        val errorMessage: String
    ) : ApiResult
}