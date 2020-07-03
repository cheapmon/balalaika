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

import com.github.cheapmon.balalaika.data.selection.DictionaryProvider
import com.github.cheapmon.balalaika.data.selection.ResourcesDictionaryProvider
import com.github.cheapmon.balalaika.data.selection.ServerDictionaryProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
@InstallIn(ActivityComponent::class)
class DictionaryModule {
    @ActivityScoped
    @Provides
    @IntoMap
    @StringKey("SERVER")
    fun provideServerProvider(provider: ServerDictionaryProvider): DictionaryProvider =
        provider

    @ActivityScoped
    @Provides
    @IntoMap
    @StringKey("RESOURCES")
    fun provideResourcesProvider(provider: ResourcesDictionaryProvider): DictionaryProvider =
        provider
}
