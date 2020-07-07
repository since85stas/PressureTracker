package stas.batura.pressuretracker.di

import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.Room
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.batura.stat.batchat.repository.room.PressureDao
import ru.batura.stat.batchat.repository.room.PressureDatabase
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RoomModule {

    @Provides
    fun providePressureDao(database: PressureDatabase): PressureDao {
        return database.pressureDatabaseDao
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext appContext: Context): PressureDatabase {
        return PressureDatabase.getInstance(appContext)
    }


}

@Module
@InstallIn(ApplicationComponent::class)
class SensorModule {

    @Provides
    @Singleton
    fun provideSensorManager(@ApplicationContext appContext: Context): SensorManager {
        val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager
    }

    @Provides
    @Singleton
    fun provideLocationManager(@ApplicationContext appContext: Context): LocationManager {
        val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager
    }
}