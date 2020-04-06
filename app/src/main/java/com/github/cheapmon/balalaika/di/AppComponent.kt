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

@ActivityScope
@Component(modules = [DatabaseModule::class, ViewModelModule::class, ImportModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: MainActivity)
    fun inject(fragment: BookmarksFragment)
    fun inject(fragment: DictionaryFragment)
    fun inject(fragment: HistoryFragment)
    fun inject(fragment: PreferencesFragment)
    fun inject(fragment: SourcesFragment)
    fun inject(fragment: SearchFragment)
}