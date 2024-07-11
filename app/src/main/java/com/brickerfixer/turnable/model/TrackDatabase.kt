package com.brickerfixer.turnable.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [Track::class])
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}