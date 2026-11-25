package com.babeena.myapplication.domain.model

data class Event(
    val id: String,
    val title: String,
    val location: String?,
    val dateTime: String?,
    val description: String?,
    val imageUrl: String?,
    val latitude: Double?,
    val longitude: Double?
)
