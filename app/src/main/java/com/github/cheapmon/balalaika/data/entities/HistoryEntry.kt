package com.github.cheapmon.balalaika.data.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.io.Serializable

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
    @ColumnInfo(name = "category_id") val categoryId: Long? = null,
    @ColumnInfo(name = "restriction") val restriction: String? = null,
    @ColumnInfo(name = "query") val query: String
)

data class HistoryEntryWithCategory(
    @Embedded val historyEntry: HistoryEntry,
    @Relation(parentColumn = "category_id", entityColumn = "id") val category: Category?
)

sealed class SearchRestriction : Serializable {
    object None : SearchRestriction()
    data class Some(
        val category: Category,
        val restriction: String
    ) : SearchRestriction()
}

data class HistoryEntryWithRestriction(
    @Embedded val historyEntry: HistoryEntry,
    @Ignore val restriction: SearchRestriction
)

@Dao
interface HistoryEntryDao {
    @Query("SELECT * FROM search_history")
    fun getAll(): Flow<List<HistoryEntry>>

    @Transaction
    @Query("SELECT * FROM search_history")
    fun getAllWithCategory(): Flow<List<HistoryEntryWithCategory>>

    @Query("DELETE FROM search_history")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM search_history")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg historyEntries: HistoryEntry)

    @Query("DELETE FROM search_history WHERE `query` = (:query)")
    suspend fun removeSimilar(query: String)

    @Delete
    suspend fun removeAll(vararg historyEntries: HistoryEntry)
}
