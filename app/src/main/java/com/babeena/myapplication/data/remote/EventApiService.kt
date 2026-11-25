package com.babeena.myapplication.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface EventApiService {
    @GET("/events")
    suspend fun getAllEvents(): List<EventDto?>?

    @GET("/events/{id}")
    suspend fun getEventDetails(@Path("id") id: String): EventDto?
}