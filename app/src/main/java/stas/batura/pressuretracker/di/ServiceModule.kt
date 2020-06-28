package stas.batura.pressuretracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import stas.batura.pressuretracker.data.IRep
import stas.batura.pressuretracker.data.Repository

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceModule {

    @Binds
    abstract fun bindReposserv(repository: Repository): IRep

}