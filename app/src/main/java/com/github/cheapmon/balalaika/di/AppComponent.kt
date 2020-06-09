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

import android.content.Context
import com.github.cheapmon.balalaika.MainActivity
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksFragment
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryFragment
import com.github.cheapmon.balalaika.ui.history.HistoryFragment
import com.github.cheapmon.balalaika.ui.preferences.PreferencesFragment
import com.github.cheapmon.balalaika.ui.preferences.SourcesFragment
import com.github.cheapmon.balalaika.ui.search.SearchFragment
import dagger.BindsInstance
import dagger.Component

/**
 * Application dependency injection component
 *
 * This component injects the [main activity][MainActivity] and all of its fragments.
 */
@ActivityScope
@Component(modules = [DatabaseModule::class, ViewModelModule::class, ImportModule::class])
interface AppComponent {
    /** @suppress */
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    /** @suppress */
    fun inject(activity: MainActivity)

    /** @suppress */
    fun inject(fragment: BookmarksFragment)

    /** @suppress */
    fun inject(fragment: DictionaryFragment)

    /** @suppress */
    fun inject(fragment: HistoryFragment)

    /** @suppress */
    fun inject(fragment: PreferencesFragment)

    /** @suppress */
    fun inject(fragment: SourcesFragment)

    /** @suppress */
    fun inject(fragment: SearchFragment)
}