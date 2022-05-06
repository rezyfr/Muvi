package com.andriiginting.favorite.di

import com.andriiginting.common_database.MuviDatabase
import com.andriiginting.favorite.data.MuviFavoriteRepository
import com.andriiginting.favorite.data.MuviFavoriteRepositoryImpl
import com.andriiginting.favorite.domain.MuviFavoriteUseCase
import com.andriiginting.favorite.domain.MuviFavoriteUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class MuviFavoriteModule {

    @Provides
    fun provideRepository(db: MuviDatabase): MuviFavoriteRepository {
        return MuviFavoriteRepositoryImpl(db)
    }

    @Provides
    @ViewModelScoped
    fun provideUseCase(repo: MuviFavoriteRepository): MuviFavoriteUseCase {
        return MuviFavoriteUseCaseImpl(repo)
    }
}