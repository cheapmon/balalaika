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
        items.addAll(BalalaikaDatabase.instance.fullFormDao().getAllLike("%$searchText%"))
        // Then, find all matching properties
        items.addAll(BalalaikaDatabase.instance.lexemePropertyDao().findPropertiesLike("%$searchText%"))
    }
}