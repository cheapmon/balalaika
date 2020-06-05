/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.di

import com.github.cheapmon.balalaika.data.config.ConfigLoader
import com.github.cheapmon.balalaika.data.config.YamlConfigLoader
import com.github.cheapmon.balalaika.data.insert.CsvEntityImporter
import com.github.cheapmon.balalaika.data.insert.EntityImporter
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