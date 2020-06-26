package ru.batura.stat.batchat.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import stas.batura.pressuretracker.ui.data.room.Pressure

@Dao
interface PressureDao {

    @Insert
    suspend fun insertPressure(pressure: Pressure)

    @Query("SELECT * FROM pressure_table ")
    fun getMessages(): LiveData<List<Pressure>>


}