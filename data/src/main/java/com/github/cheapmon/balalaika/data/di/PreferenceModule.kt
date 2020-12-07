package com.github.cheapmon.balalaika.data.di

import com.github.cheapmon.balalaika.data.prefs.DataStoreStorage
import com.github.cheapmon.balalaika.data.prefs.PreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/** [Preference][PreferenceStorage] dependency injection module */
@Module
@InstallIn(ApplicationComponent::class)
internal class PreferenceModule {
    /** @suppress */
    @Provides
    fun providePreferenceStorage(dataStoreStorage: DataStoreStorage): PreferenceStorage =
        dataStoreStorage
}
