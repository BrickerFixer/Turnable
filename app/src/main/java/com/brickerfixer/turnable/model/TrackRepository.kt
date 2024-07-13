package com.brickerfixer.turnable.model

class TrackRepository(private val trackDao: TrackDao) {
    fun addNewItemToDB(mediaItemUri: String?) {
        val tr = Track(null, mediaItemUri)
        trackDao.insert(tr)
    }

    fun getAll(): List<Track> {
        return trackDao.getAll()
    }
}