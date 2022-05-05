package com.andriiginting.muvi.detail.di

import com.andriiginting.common_database.MuviDatabase
import com.andriiginting.core_network.MuviDetailService
import com.andriiginting.muvi.detail.data.MuviDetailRepository
import com.andriiginting.muvi.detail.data.MuviDetailRepositoryImpl
import com.andriiginting.muvi.detail.domain.MuviDetailMapper
import com.andriiginting.muvi.detail.domain.MuviDetailMapperImpl
import com.andriiginting.muvi.detail.domain.MuviDetailUseCase
import com.andriiginting.muvi.detail.domain.MuviDetailUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.migration.DisableInstallInCheck

@Module
@InstallIn(ViewModelComponent::class)
class MuviDetailModule {
    @Provides
    fun provideRepository(
        service: MuviDetailService,
        database: MuviDatabase
    ): MuviDetailRepository {
        return MuviDetailRepositoryImpl(service, database)
    }

    @Provides
    fun provideMapper(): MuviDetailMapper {
        return MuviDetailMapperImpl()
    }

    @Provides
    @ViewModelScoped
    fun provideUseCase(
        repository: MuviDetailRepository,
        mapper: MuviDetailMapper
    ): MuviDetailUseCase {
        return MuviDetailUseCaseImpl(repository, mapper)
    }
}