package com.github.cheapmon.balalaika.data.entities.lexeme

import androidx.room.Embedded
import androidx.room.Relation
import com.github.cheapmon.balalaika.data.entities.property.Property

data class LexemeWithRelations(
    @Embedded val lexeme: Lexeme,
    @Relation(parentColumn = "base_id", entityColumn = "id") val base: Lexeme?,
    @Relation(parentColumn = "id", entityColumn = "lexeme_id") val properties: List<Property>
)