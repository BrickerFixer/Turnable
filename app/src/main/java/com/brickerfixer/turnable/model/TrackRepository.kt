package com.brickerfixer.turnable.model

import javax.inject.Inject

class TrackRepository @Inject constructor(private val trackDao: TrackDao) {
    fun addNewItemToDB(mediaItemUri: String?) {
        val tr = Track(null, mediaItemUri)
        trackDao.insert(tr)
    }

    fun getAll(): List<Track> {
        return trackDao.getAll()
    }
}