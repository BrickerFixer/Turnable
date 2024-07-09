package com.brickerfixer.turnable

import android.app.Application
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.room.Room.databaseBuilder

class App : Application() {
    var database: TrackDatabase? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        instance = this
        database = databaseBuilder(applicationContext, TrackDatabase::class.java, "trackdatabase")
            .allowMainThreadQueries()
            .build()
        serviceIntent = Intent(this, ExoplayerService::class.java)
        startService(serviceIntent)
    }

    companion object {
        var instance: App? = null
        var serviceIntent: Intent? = null
    }
}