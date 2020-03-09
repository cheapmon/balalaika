package com.github.cheapmon.balalaika.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github.cheapmon.balalaika.DictionaryEntry
import com.github.cheapmon.balalaika.PropertyLine
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.FullForm

class HomeViewModel : ViewModel() {
    lateinit var lexemes: LiveData<PagedList<DictionaryEntry>>
    private var viewId: String = "all"
    private var categoryId: String = "default"

    fun setView(viewId: String) {
        this.viewId = viewId
    }

    fun setCategory(categoryId: String) {
        this.categoryId = categoryId
        init()
    }

    private fun init() {
        val source: DataSource.Factory<Int, FullForm> = if(categoryId == "default") {
            BalalaikaDatabase.instance.fullFormDao().getAll()
        } else {
            BalalaikaDatabase.instance.fullFormDao().getAllOrderedBy(categoryId)
        }
        val factory = source.map { lexeme ->
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