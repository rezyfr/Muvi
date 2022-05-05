package com.andriiginting.muvi.home.di

import com.andriiginting.core_network.MuviHomeService
import com.andriiginting.muvi.home.data.MuviHomeRepository
import com.andriiginting.muvi.home.data.MuviHomeRepositoryImpl
import com.andriiginting.muvi.home.domain.MuviHomeUseCase
import com.andriiginting.muvi.home.domain.MuviHomeUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class MuviHomeModule {

    @Provides
    fun provideRepository(service: MuviHomeService): MuviHomeRepository{
        return MuviHomeRepositoryImpl(service)
    }

    @Provides
    @ViewModelScoped
    fun provideUseCase(repository: MuviHomeRepository): MuviHomeUseCase {
        return MuviHomeUseCaseImpl(repository)
    }
}