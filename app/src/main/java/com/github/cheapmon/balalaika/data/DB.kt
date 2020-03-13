package com.github.cheapmon.balalaika.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Category::class], version = 1)
@TypeConverters(Converters::class)
abstract class DB : RoomDatabase() {
    abstract fun categories(): CategoryDao
}
