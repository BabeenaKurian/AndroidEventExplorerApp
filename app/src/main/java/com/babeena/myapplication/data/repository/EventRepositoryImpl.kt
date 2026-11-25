package com.babeena.myapplication.data.repository

import com.babeena.myapplication.data.local.EventDao
import com.babeena.myapplication.data.local.EventEntity
import com.babeena.myapplication.data.remote.ApiResult
import com.babeena.myapplication.data.remote.EventApiService
import com.babeena.myapplication.data.remote.EventDetailsResult
import com.babeena.myapplication.data.remote.EventDto
import com.babeena.myapplication.domain.model.Event
import com.babeena.myapplication.domain.repository.EventRepository
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.compareTo
import kotlin.coroutines.cancellation.CancellationException

class EventRepositoryImpl @Inject constructor(
    private val eventApiService: EventApiService,
    val eventDao: EventDao,): EventRepository {

    override suspend fun getAllEvents(): ApiResult {

        return try {
            val response = eventApiService.getAllEvents() ?: return ApiResult.Success(null)

            val events = response.mapNotNull { dto ->
                dto?.toEventOrNull()
            }
            eventDao.replaceAllEvents(
                events.map { it.toEntity() }
            )
            ApiResult.Success(events)

        } catch (e: Exception) {
            val cachedEvents = eventDao.getAllEvents().first()
            if (cachedEvents.isNotEmpty()) {
                ApiResult.Success(cachedEvents.map { it.toEvent() })
            } else {
                ApiResult.Failure(e.message ?: "Unable to load data...")
            }
        }
    }

    override suspend fun getEventDetails(id: String): EventDetailsResult {

        return try {
            val eventDto = eventApiService.getEventDetails(id)
            val event = eventDto?.toEventOrNull()
            if (event != null) {
                EventDetailsResult.Success(event)
            } else {
                EventDetailsResult.Failure("Event details not available")
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            EventDetailsResult.Failure(
                e.message ?: "Unable to get event details"
            )
        }
    }

    private fun EventDto.toEventOrNull(): Event? {
        val eventId = id.toSafeString() ?: return null
        val eventTitle = title
            ?.takeIf { it.isNotBlank() }
            ?: return null

        return Event(
            id = eventId,
            title = eventTitle,
            location = location,
            dateTime = dateTime,
            imageUrl = imageUrl,
            latitude = latitude.toSafeDouble(),
            longitude = longitude.toSafeDouble(),
            description = description
        )
    }

    private fun Event.toEntity() =
        EventEntity(
            id = id,
            title = title,
            location = location,
            dateTime = dateTime,
            imageUrl = imageUrl,
            latitude = latitude,
            longitude = longitude,
            description = description
        )

    private fun EventEntity.toEvent() = Event(
        id = id,
        title = title,
        location = location,
        dateTime = dateTime,
        imageUrl = imageUrl,
        latitude = latitude,
        longitude = longitude,
        description = description
    )

    private fun JsonElement?.toSafeString(): String? {
        if (this == null || isJsonNull) return null

        return runCatching {
            if (isJsonPrimitive) {
                asString.takeIf { it.isNotBlank() }
            } else {
                null
            }
        }.getOrNull()
    }

    private fun JsonElement?.toSafeDouble(): Double? {
        if (this == null || isJsonNull) return null

        return runCatching {
            if (isJsonPrimitive) {
                asString.toDoubleOrNull()
            } else {
                null
            }
        }.getOrNull()
    }
}