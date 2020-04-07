package com.github.cheapmon.balalaika.data.insert

import com.github.cheapmon.balalaika.data.entities.*

interface EntityImporter {
    fun readCategories(): List<Category>
    fun readLexemes(): List<Lexeme>
    fun readProperties(): List<Property>
    fun readDictionaryViews(): List<DictionaryView>
    fun readDictionaryViewToCategories(): List<DictionaryViewToCategory>
}