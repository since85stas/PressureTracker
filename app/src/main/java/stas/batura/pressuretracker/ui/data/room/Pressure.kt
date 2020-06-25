package stas.batura.pressuretracker.ui.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pressure_table")
data class Pressure (

        var pressure: Double = 0.0,

        var time: Long = 0,

        @PrimaryKey(autoGenerate = true)
        var pressureId: Long = 0L
)

{
}