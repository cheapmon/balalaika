package com.github.cheapmon.balalaika.di

import android.content.Context
import androidx.room.Room
import com.github.cheapmon.balalaika.data.AppDatabase
import com.github.cheapmon.balalaika.util.Constants
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    @ActivityScope
    @Provides
    fun provideDatabase(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "balalaika").build()

    @ActivityScope
    @Provides
    fun provideCategories(appDatabase: AppDatabase) = appDatabase.categories()

    @ActivityScope
    @Provides
    fun provideLexemes(appDatabase: AppDatabase) = appDatabase.lexemes()

    @ActivityScope
    @Provides
    fun provideProperties(appDatabase: AppDatabase) = appDatabase.properties()

    @ActivityScope
    @Provides
    fun provideDictionaryViews(appDatabase: AppDatabase) = appDatabase.dictionaryViews()

    @ActivityScope
    @Provides
    fun provideHistoryEntries(appDatabase: AppDatabase) = appDatabase.historyEntries()
}