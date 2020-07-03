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

import com.github.cheapmon.balalaika.core.resources.AndroidResourceProvider
import com.github.cheapmon.balalaika.core.resources.ResourceProvider
import com.github.cheapmon.balalaika.core.storage.PreferenceStorage
import com.github.cheapmon.balalaika.core.storage.Storage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

/**
 * Import utility dependency injection module
 *
 * This module injects all classes associated with database import and utility operations.
 */
@Module
@InstallIn(ActivityComponent::class)
abstract class ImportModule {
    /** @suppress */
    @ActivityScoped
    @Binds
    abstract fun provideResourceProvider(provider: AndroidResourceProvider): ResourceProvider

    /** @suppress */
    @ActivityScoped
    @Binds
    abstract fun provideStorage(preferenceStorage: PreferenceStorage): Storage
}
