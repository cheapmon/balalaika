package com.github.cheapmon.balalaika.data.di

import com.github.cheapmon.balalaika.data.mappers.DictionaryEntryEntityToDictionaryEntry
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntryEntityToDictionaryEntryMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
internal class MappersModule {
    /** @suppress */
    @Provides
    fun providesEntityToEntryMapper(
        toDictionaryEntry: DictionaryEntryEntityToDictionaryEntry
    ): DictionaryEntryEntityToDictionaryEntryMapper = toDictionaryEntry
}
