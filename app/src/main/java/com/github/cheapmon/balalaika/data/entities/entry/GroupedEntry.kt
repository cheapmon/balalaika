package com.github.cheapmon.balalaika.data.entities.entry

import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithRelations

data class GroupedEntry(
    val lexeme: Lexeme,
    val base: Lexeme?,
    val properties: List<PropertyWithRelations>
)
