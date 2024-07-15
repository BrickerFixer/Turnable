package com.brickerfixer.turnable.model

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationCompenent (i.e. everywhere in the application)
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(app, TrackDatabase::class.java, "trackdatabase")
        .build() // The reason we can construct a database for the repo

    @Singleton
    @Provides
    fun provideDao(db: TrackDatabase) =
        db.trackDao() // The reason we can implement a Dao for the database
}