package com.github.cheapmon.balalaika.ui.search

import androidx.lifecycle.*
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.FullForm

class SearchItemViewModel : ViewModel() {
    var searchText = ""
        set(text) {
            if (field != text) {
                field = text
                refresh(restriction, field)
            }
        }
    var restriction: SearchRestriction? = null
    lateinit var forms: LiveData<List<String>>
    lateinit var props: LiveData<List<String>>

    fun refresh(res: SearchRestriction?, searchText: String) {
        forms = if (res == null) {
            BalalaikaDatabase.instance.fullFormDao().getAllLike("%$searchText%")
        } else {
            val c = res.category
            val v = res.restriction
            if (c != null && v != null) {
                BalalaikaDatabase.instance.categoryDao().findByName(c).switchMap {
                    val id = it?.id
                    if (id != null) {
                        BalalaikaDatabase.instance.fullFormDao().getAllLikeRestricted(
                                text = "%$searchText%",
                                category = id,
                                restriction = res.restriction
                        )
                    } else {
                        BalalaikaDatabase.instance.fullFormDao().getAllLike("%$searchText%")
                    }
                }
            } else {
                BalalaikaDatabase.instance.fullFormDao().getAllLike("%$searchText%")
            }
        }
        props = if(res == null) {
            BalalaikaDatabase.instance.lexemePropertyDao().findPropertiesLike("%$searchText%")
        } else {
            val c = res.category
            val r = res.restriction
            if(c != null && r != null) {
                BalalaikaDatabase.instance.categoryDao().findByName(c).switchMap {
                    val id = it?.id
                    if(id != null) {
                        BalalaikaDatabase.instance.lexemePropertyDao().findPropertiesLikeRestricted("%$searchText%", id, r)
                    } else {
                        BalalaikaDatabase.instance.lexemePropertyDao().findPropertiesLike("%$searchText%")
                    }
                }
            } else {
                BalalaikaDatabase.instance.lexemePropertyDao().findPropertiesLike("%$searchText%")
            }
        }
    }
}