package com.example.evntly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.evntly.domain.model.Event

/**
 * Declares the Room DB with the Event entity and registers converters.
 */

@Database(entities = [Event::class], version = 2)
@TypeConverters(Converters::class)
abstract class EventDatabase : RoomDatabase() {
    abstract fun getEventDao(): EventDao
}