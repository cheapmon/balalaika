package com.github.cheapmon.balalaika.data.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "search_history",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ],
    indices = [Index(value = ["category_id"])]
)
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val historyEntryId: Long = 0,
    @ColumnInfo(name = "category_id") val categoryId: Long?,
    @ColumnInfo(name = "restriction") val restriction: String,
    @ColumnInfo(name = "query") val query: String
)

data class HistoryEntryWithCategory(
    @Embedded val historyEntry: HistoryEntry,
    @Relation(parentColumn = "category_id", entityColumn = "id") val category: Category?
)

@Dao
interface HistoryEntryDao {
    @Query("SELECT * FROM search_history")
    fun getAll(): Flow<List<HistoryEntry>>

    @Transaction
    @Query("SELECT * FROM search_history")
    fun getAllWithCategory(): Flow<List<HistoryEntryWithCategory>>

    @Query("SELECT COUNT(*) FROM search_history")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg historyEntries: HistoryEntry)
}