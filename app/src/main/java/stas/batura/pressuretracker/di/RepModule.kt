package stas.batura.pressuretracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.Repository

@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepos(repository: Repository): IRep

}