package com.github.cheapmon.balalaika.di

import com.github.cheapmon.balalaika.util.AndroidResourceLoader
import com.github.cheapmon.balalaika.util.ResourceLoader
import dagger.Binds
import dagger.Module

@Module
abstract class ImportModule {
    @Binds
    abstract fun provideResourceLoader(androidResourceLoader: AndroidResourceLoader): ResourceLoader
}