package com.github.cheapmon.balalaika.db

import androidx.room.*

@Entity(foreignKeys = [
    ForeignKey(entity = Word::class, parentColumns = ["id"], childColumns = ["word_id"]),
    ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])
])
data class WordInfo(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "word_id") val wordId: Int,
        @ColumnInfo(name = "category_id") val categoryId: Int,
        @ColumnInfo(name = "value") val value: String
)

@Dao
interface WordInfoDao {
    @Query("SELECT * FROM wordinfo")
    fun getAll(): List<WordInfo>

    @Query("SELECT * FROM wordinfo WHERE word_id = (:word)")
    fun getInfos(word: Int): List<WordInfo>

    @Query("SELECT count(*) FROM wordinfo")
    fun count(): Int

    @Insert
    fun insertAll(vararg categories: WordInfo)
}