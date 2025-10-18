package com.example.evntly.data.repository

import com.example.evntly.domain.model.Event
import com.example.evntly.data.local.EventDao
import kotlinx.coroutines.flow.Flow

/**
 * Wraps the DAO,
 * exposes simple functions.
 * Keeps ViewModel clean
 */

class EventRepository(private val eventDao: EventDao) {
    suspend fun addEvent(event: Event) {
        eventDao.insertEvent(event)
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }

    fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents()
    }
}