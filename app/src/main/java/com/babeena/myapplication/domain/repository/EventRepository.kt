package com.babeena.myapplication.domain.repository

import com.babeena.myapplication.data.remote.ApiResult
import com.babeena.myapplication.data.remote.EventDetailsResult

interface EventRepository {
    suspend fun getAllEvents(): ApiResult

    suspend fun getEventDetails(id: String): EventDetailsResult


}