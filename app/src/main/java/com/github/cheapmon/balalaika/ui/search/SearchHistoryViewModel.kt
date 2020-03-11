package com.github.cheapmon.balalaika.ui.search

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.SearchHistoryEntry

class SearchHistoryViewModel : ViewModel() {
    val entries: List<Pair<SearchHistoryEntry, () -> Unit>>
    lateinit var navController: NavController

    init {
        entries = BalalaikaDatabase.instance.searchHistoryDao().getAll().map {
            it to {
                val restriction = if(it.categoryId != null && it.value != null) {
                    SearchRestriction(BalalaikaDatabase.instance.categoryDao().findById(it.categoryId)?.name, it.value)
                } else {
                    null
                }
                navController.navigate(SearchHistoryFragmentDirections.actionNavHistoryToNavSearch(it.query, restriction))
            }
        }
    }
}