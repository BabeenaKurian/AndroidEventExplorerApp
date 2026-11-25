package com.babeena.myapplication.data.remote

import com.google.gson.JsonElement

data class EventDto(
    val id: JsonElement? = null,
    val title: String? = null,
    val location: String? = null,
    val dateTime: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val latitude: JsonElement? = null,
    val longitude: JsonElement? = null
)
