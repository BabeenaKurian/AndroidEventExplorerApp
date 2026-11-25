package com.babeena.myapplication.domain.repository

import com.babeena.myapplication.data.remote.ApiResult

interface EventRepository {
    suspend fun getAllEvents(): ApiResult

}