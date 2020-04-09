package com.github.cheapmon.balalaika.data.entities.lexeme

import com.github.cheapmon.balalaika.data.entities.property.PropertyWithRelations

data class _DictionaryEntry(
    val lexeme: Lexeme,
    val base: Lexeme?,
    val properties: List<PropertyWithRelations>
)