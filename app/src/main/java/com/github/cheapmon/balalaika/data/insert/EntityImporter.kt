package com.github.cheapmon.balalaika.data.insert

import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.property.Property
import com.github.cheapmon.balalaika.data.entities.view.DictionaryView
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewToCategory

interface EntityImporter {
    fun readCategories(): List<Category>
    fun readLexemes(): List<Lexeme>
    fun readProperties(): List<Property>
    fun readDictionaryViews(): List<DictionaryView>
    fun readDictionaryViewToCategories(): List<DictionaryViewToCategory>
}