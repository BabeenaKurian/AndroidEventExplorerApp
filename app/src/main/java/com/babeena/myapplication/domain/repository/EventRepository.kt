package com.babeena.myapplication.domain.repository

import com.babeena.myapplication.domain.model.ApiResult
import com.babeena.myapplication.domain.model.EventDetailsResult

interface EventRepository {
    suspend fun getAllEvents(): ApiResult
    suspend fun getEventDetails(id: String): EventDetailsResult
    suspend fun syncEvents()


}