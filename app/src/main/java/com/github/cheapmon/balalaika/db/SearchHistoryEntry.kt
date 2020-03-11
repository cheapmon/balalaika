package com.github.cheapmon.balalaika.db

import androidx.room.*
import java.util.*

@Entity(tableName = "search_history")
data class SearchHistoryEntry(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "query") val query: String,
        @ColumnInfo(name = "category_id") val categoryId: String?,
        @ColumnInfo(name = "value") val value: String?,
        @ColumnInfo(name = "date") val date: Date?
)

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history")
    fun getAll(): List<SearchHistoryEntry>

    @Query("SELECT count(*) FROM search_history")
    fun count(): Int

    @Insert
    fun insert(entry: SearchHistoryEntry)
}