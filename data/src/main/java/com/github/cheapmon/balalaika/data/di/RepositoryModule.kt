package com.github.cheapmon.balalaika.data.di

import com.github.cheapmon.balalaika.data.repositories.ConfigRepository
import com.github.cheapmon.balalaika.data.repositories.DefaultConfigRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/** Repository dependency injection module */
@Module
@InstallIn(ApplicationComponent::class)
internal class RepositoryModule {
    /** @suppress */
    @Provides
    fun providesConfigRepository(
        configRepository: DefaultConfigRepository
    ): ConfigRepository = configRepository
}
