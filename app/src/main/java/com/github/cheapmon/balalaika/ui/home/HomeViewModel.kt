package com.github.cheapmon.balalaika.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.LemmaProperty
import com.github.cheapmon.balalaika.db.Lexeme

data class PropertyLine(val widget: String, val lexeme: Lexeme, val category: String, val property: LemmaProperty)
data class DictionaryEntry(val lexeme: Lexeme, val lines: List<PropertyLine>)

class HomeViewModel() : ViewModel() {
    val lexemes: LiveData<PagedList<DictionaryEntry>>

    init {
        val factory = BalalaikaDatabase.instance.lexemeDao().getAll().map { lexeme ->
            val properties = BalalaikaDatabase.instance.lemmaPropertyDao().findByLexeme(lexeme.lexeme).map { property ->
                val category = BalalaikaDatabase.instance.categoryDao().findById(property.categoryId)
                val widget = category?.widget ?: "plain"
                val categoryName = category?.name ?: ""
                PropertyLine(widget, lexeme, categoryName, property)
            }
            DictionaryEntry(lexeme, properties)
        }
        lexemes = LivePagedListBuilder(factory, 10).build()
    }
}