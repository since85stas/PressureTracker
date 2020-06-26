package stas.batura.pressuretracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import ru.batura.stat.batchat.repository.room.PressureDao
import stas.batura.pressuretracker.ui.data.IRep
import stas.batura.pressuretracker.ui.data.Repository

@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModule {

    @Binds abstract fun bindRepos(repository: Repository): IRep

}