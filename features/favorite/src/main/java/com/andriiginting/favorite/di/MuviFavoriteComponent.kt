package com.andriiginting.favorite.di

import com.andriiginting.common_di.FeatureScope
import com.andriiginting.common_di.MuviAppComponent
import com.andriiginting.favorite.presentation.FavoriteActivity
import dagger.Component
import dagger.hilt.migration.DisableInstallInCheck

@FeatureScope
@Component(
    dependencies = [MuviAppComponent::class],
    modules = [MuviFavoriteModule::class]
)
@DisableInstallInCheck
interface MuviFavoriteComponent {
    fun inject(activity: FavoriteActivity)
}