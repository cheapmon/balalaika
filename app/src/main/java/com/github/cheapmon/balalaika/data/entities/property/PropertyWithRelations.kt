package com.github.cheapmon.balalaika.data.entities.property

import androidx.room.Embedded
import androidx.room.Relation
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme

data class PropertyWithRelations(
    @Embedded val property: Property,
    @Relation(parentColumn = "category_id", entityColumn = "id") val category: Category,
    @Relation(parentColumn = "lexeme_id", entityColumn = "id") val lexeme: Lexeme
)