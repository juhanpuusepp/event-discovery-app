package com.example.evntly.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room type converters
 * Maps Date <-> Long so Room can persist date field.
 */
class Converters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(time: Long): Date {
        return Date(time)
    }
}