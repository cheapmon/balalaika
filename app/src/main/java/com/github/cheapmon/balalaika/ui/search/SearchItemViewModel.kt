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
        val forms = BalalaikaDatabase.instance.fullFormDao().getAllLike("%$searchText%")
        val props = BalalaikaDatabase.instance.lexemePropertyDao().findPropertiesLike("%$searchText%")
        (forms + props).distinct().chunked(100).forEach {
            items.addAll(BalalaikaDatabase.instance.fullFormDao().getAllById(it))
        }
    }
}