package stas.batura.pressuretracker.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pressure_table")
data class Pressure (

        var pressure: Float = 0.0F,

        var time: Long = 0,

        var rainPower: Int = 0,

        @PrimaryKey(autoGenerate = true)
        var pressureId: Long = 0L


)

{
}