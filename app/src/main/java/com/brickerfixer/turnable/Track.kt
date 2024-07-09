package com.brickerfixer.turnable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "trackuri") val trackuri: String?
)
