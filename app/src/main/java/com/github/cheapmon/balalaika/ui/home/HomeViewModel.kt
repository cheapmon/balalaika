package com.github.cheapmon.balalaika.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.cheapmon.balalaika.DictionaryEntry
import com.github.cheapmon.balalaika.PropertyLine
import com.github.cheapmon.balalaika.db.BalalaikaDatabase

class HomeViewModel : ViewModel() {
    val lexemes: LiveData<PagedList<DictionaryEntry>>
    private var viewId: String = "all"

    fun setView(viewId: String) {
        this.viewId = viewId
    }

    init {
        val factory = BalalaikaDatabase.instance.fullFormDao().getAll().map { lexeme ->
            val categories = BalalaikaDatabase.instance.dictionaryViewDao().findCategoryIdsForView(viewId)
            val lines = BalalaikaDatabase.instance.lexemePropertyDao()
                    .findByFullForm(lexeme.id, categories)
                    .groupBy { it.categoryId }
                    .toList()
                    .sortedBy { BalalaikaDatabase.instance.categoryDao().findById(it.first)?.sequence }
                    .map { (categoryId, properties) ->
                        val category = BalalaikaDatabase.instance.categoryDao().findById(categoryId)
                        val widget = category?.widget ?: "plain"
                        val categoryName = category?.name ?: ""
                        PropertyLine(widget, lexeme, categoryName, properties.filter { it.value != null })
                    }
            DictionaryEntry(lexeme, lines)
        }
        lexemes = LivePagedListBuilder(factory, 10).build()
    }
}