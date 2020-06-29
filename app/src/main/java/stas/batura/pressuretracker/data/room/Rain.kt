package stas.batura.pressuretracker.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rain_table")
data class Rain (

        var isStarted: Boolean = false,

        var isEnded: Boolean = false,

        var time: Long = 0L,

        @PrimaryKey(autoGenerate = true)
        var _id: Long = 0
)