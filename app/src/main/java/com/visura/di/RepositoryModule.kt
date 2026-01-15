package com.visura.di

import com.visura.data.repositories.network.authentication.DefaultFireBaseClientRepository
import com.visura.data.repositories.network.authentication.DefaultGoogleClientRepository
import com.visura.data.repositories.network.location.DefaultLocationRepository
import com.visura.domain.repositories.authentication.FireBaseClientRepository
import com.visura.domain.repositories.authentication.GoogleClientRepository
import com.visura.domain.repositories.location.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun FirebaseRepository(defaultFireBaseClientRepository: DefaultFireBaseClientRepository): FireBaseClientRepository

    @Binds
    abstract fun GoogleRepository(defaultGoogleClientRepository: DefaultGoogleClientRepository): GoogleClientRepository

    @Binds
    abstract fun LocationRepository(defaultLocationRepository: DefaultLocationRepository): LocationRepository
}