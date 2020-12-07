package com.github.cheapmon.balalaika.data.di

import com.github.cheapmon.balalaika.data.mappers.DictionaryEntryEntityToDictionaryEntry
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntryEntityToDictionaryEntryMapper
import com.github.cheapmon.balalaika.data.mappers.Mapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/** [Mapper] dependency injection module */
@Module
@InstallIn(ApplicationComponent::class)
internal class MappersModule {
    /** @suppress */
    @Provides
    fun providesEntityToEntryMapper(
        toDictionaryEntry: DictionaryEntryEntityToDictionaryEntry
    ): DictionaryEntryEntityToDictionaryEntryMapper = toDictionaryEntry
}
