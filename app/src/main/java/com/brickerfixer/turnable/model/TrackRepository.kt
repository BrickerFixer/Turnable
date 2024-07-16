package com.brickerfixer.turnable.model

import javax.inject.Inject

class TrackRepository @Inject constructor(private val trackDao: TrackDao) {
    fun addNewItemToDB(mediaItemUri: String?) {
        val tr = Track(null, mediaItemUri)
        if (trackDao.uniquenessCheck(tr.uri)?.isEmpty() == true){
            trackDao.insert(tr)
        }
    }

    fun getAll(): List<Track> {
        return trackDao.getAll()
    }
}