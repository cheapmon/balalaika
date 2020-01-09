package com.github.cheapmon.balalaika.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.FullForm
import com.github.cheapmon.balalaika.db.LexemeProperty

data class PropertyLine(val widget: String, val fullForm: FullForm, val category: String, val properties: List<LexemeProperty>)
data class DictionaryEntry(val fullForm: FullForm, val lines: List<PropertyLine>)

class HomeViewModel() : ViewModel() {
    val lexemes: LiveData<PagedList<DictionaryEntry>>

    init {
        val factory = BalalaikaDatabase.instance.fullFormDao().getAll().map { lexeme ->
            val lines = BalalaikaDatabase.instance.lexemePropertyDao()
                    .findByFullForm(lexeme.id)
                    .groupBy { it.categoryId }
                    .map { (categoryId, properties) ->
                        val category = BalalaikaDatabase.instance.categoryDao().findById(categoryId)
                        val widget = category?.widget ?: "plain"
                        val categoryName = category?.name ?: ""
                        PropertyLine(widget, lexeme, categoryName, properties)
                    }
            DictionaryEntry(lexeme, lines)
        }
        lexemes = LivePagedListBuilder(factory, 10).build()
    }
}