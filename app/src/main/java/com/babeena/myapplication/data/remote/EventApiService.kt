package com.babeena.myapplication.data.remote

import retrofit2.http.GET

interface EventApiService {

    @GET("/events")
    suspend fun getAllEvents(): List<EventDto?>?

}