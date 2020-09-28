package com.github.cheapmon.balalaika.data.di

import com.github.cheapmon.balalaika.data.prefs.DataStoreStorage
import com.github.cheapmon.balalaika.data.prefs.PreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
internal class PreferenceModule {
    @Provides
    fun providePreferenceStorage(dataStoreStorage: DataStoreStorage): PreferenceStorage =
        dataStoreStorage
}
