package stas.batura.pressuretracker.di

import android.content.Context
import androidx.room.Room
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
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