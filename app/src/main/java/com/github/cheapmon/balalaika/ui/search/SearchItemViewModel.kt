package com.github.cheapmon.balalaika.ui.search

import androidx.lifecycle.ViewModel
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.FullForm

class SearchItemViewModel : ViewModel() {
    val items: ArrayList<FullForm> = arrayListOf()
    var searchText = ""
        set(text) {
            if (field != text) {
                field = text
                refresh()
            }
        }

    private fun refresh() {
        items.clear()
        // Find any matching full form first
        val forms = BalalaikaDatabase.instance.fullFormDao().getAllLike("%$searchText%")
        // Then, find all matching properties
        val props = BalalaikaDatabase.instance.lexemePropertyDao().findPropertiesLike("%$searchText%")
        // Get forms from ids
        (forms + props).distinct().chunked(100).forEach {
            items.addAll(BalalaikaDatabase.instance.fullFormDao().getAllById(it))
        }
    }
}