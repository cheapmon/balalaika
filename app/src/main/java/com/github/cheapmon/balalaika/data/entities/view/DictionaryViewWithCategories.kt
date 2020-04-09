package com.github.cheapmon.balalaika.data.entities.view

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.cheapmon.balalaika.data.entities.category.Category

data class DictionaryViewWithCategories(
    @Embedded val dictionaryView: DictionaryView,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            DictionaryViewToCategory::class,
            parentColumn = "dictionary_view_id",
            entityColumn = "category_id"
        )
    )
    val categories: List<Category>
)