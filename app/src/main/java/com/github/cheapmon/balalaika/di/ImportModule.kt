package com.github.cheapmon.balalaika.di

import com.github.cheapmon.balalaika.data.config.ConfigLoader
import com.github.cheapmon.balalaika.data.config.YamlConfigLoader
import com.github.cheapmon.balalaika.data.insert.*
import com.github.cheapmon.balalaika.data.resources.AndroidResourceLoader
import com.github.cheapmon.balalaika.data.resources.ResourceLoader
import com.github.cheapmon.balalaika.data.storage.PreferenceStorage
import com.github.cheapmon.balalaika.data.storage.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class ImportModule {
    @ActivityScope
    @Binds
    abstract fun provideResourceLoader(androidResourceLoader: AndroidResourceLoader): ResourceLoader

    @ActivityScope
    @Binds
    abstract fun provideImporter(csvImporter: CsvEntityImporter): EntityImporter

    @ActivityScope
    @Binds
    abstract fun provideConfigLoader(yamlConfigLoader: YamlConfigLoader): ConfigLoader

    @ActivityScope
    @Binds
    abstract fun provideStorage(preferenceStorage: PreferenceStorage): Storage
}