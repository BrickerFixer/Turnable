package com.brickerfixer.turnable.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks")
    fun getAll(): List<Track>
    //TODO: try to do something so it won't duplicate entries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(track: Track)
    @Delete
    fun delete(track: Track)
    @Query("DELETE FROM tracks")
    fun deleteAll()
}