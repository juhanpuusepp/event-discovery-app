package com.example.evntly.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity (domain model)
 */

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: Date,
    val price: Double,
    val description: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?
)