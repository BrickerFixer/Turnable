package com.brickerfixer.turnable.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Track::class], version = 1)
abstract class TrackDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}