package com.github.cheapmon.balalaika.data.entities.view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.github.cheapmon.balalaika.data.entities.category.Category

@Entity(
    tableName = "dictionary_view_to_category",
    primaryKeys = ["dictionary_view_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = DictionaryView::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_view_id"]
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ],
    indices = [Index(value = ["dictionary_view_id"]), Index(
        value = ["category_id"]
    )]
)
data class DictionaryViewToCategory(
    @ColumnInfo(name = "dictionary_view_id") val dictionaryViewId: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long
)