package com.github.cheapmon.balalaika.data.entities.history

import androidx.room.Embedded
import androidx.room.Relation
import com.github.cheapmon.balalaika.data.entities.category.Category

data class HistoryEntryWithCategory(
    @Embedded val historyEntry: HistoryEntry,
    @Relation(parentColumn = "category_id", entityColumn = "id") val category: Category?
)