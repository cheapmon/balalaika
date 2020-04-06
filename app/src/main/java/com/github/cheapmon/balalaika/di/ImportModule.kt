package com.github.cheapmon.balalaika.di

import com.github.cheapmon.balalaika.data.import.AndroidResourceLoader
import com.github.cheapmon.balalaika.data.import.ResourceLoader
import dagger.Binds
import dagger.Module

@Module
abstract class ImportModule {
    @Binds
    abstract fun provideResourceLoader(androidResourceLoader: AndroidResourceLoader): ResourceLoader
}