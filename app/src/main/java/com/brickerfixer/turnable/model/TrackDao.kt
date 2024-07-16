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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(track: Track)
    @Delete
    fun delete(track: Track)
    @Query("DELETE FROM tracks")
    fun deleteAll()
    @Query("SELECT * from tracks WHERE uri = :uri")
    fun uniquenessCheck(uri: String?): List<Track?>?
}