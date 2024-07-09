package com.brickerfixer.turnable

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {
    @Query("SELECT * FROM Track")
    fun getAll(): List<Track>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: Track)
    @Delete
    fun delete(track: Track)
    @Query("DELETE FROM Track")
    fun deleteAll()
}