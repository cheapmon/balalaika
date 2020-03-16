package com.github.cheapmon.balalaika.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Category::class, Lexeme::class, Property::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DB : RoomDatabase() {
    abstract fun categories(): CategoryDao
    abstract fun lexemes(): LexemeDao
    abstract fun properties(): PropertyDao
}
