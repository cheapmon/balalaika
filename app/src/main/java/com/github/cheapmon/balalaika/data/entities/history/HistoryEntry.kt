package com.github.cheapmon.balalaika.data.entities.history

import androidx.room.*
import com.github.cheapmon.balalaika.data.entities.category.Category

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
