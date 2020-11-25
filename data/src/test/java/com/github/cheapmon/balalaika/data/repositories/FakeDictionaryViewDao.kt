package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FakeDictionaryViewDao : DictionaryViewDao {
    private val views = mutableListOf<DictionaryViewWithCategories>()

    override fun getAll(dictionaryId: String): Flow<List<DictionaryViewWithCategories>> {
        return flow { emit(views.filter { it.dictionaryView.dictionaryId == dictionaryId }) }
    }

    override suspend fun insertViews(dictionaryViews: List<DictionaryViewEntity>) {
        for (view in dictionaryViews) {
            val withCategory = DictionaryViewWithCategories(
                dictionaryView = view,
                categories = listOf()
            )
            views.add(withCategory)
        }
    }

    override suspend fun insertRelation(dictionaryViewToCategory: List<DictionaryViewToCategory>) {
        for (relation in dictionaryViewToCategory) {
            val idx = views.indexOfFirst { it.dictionaryView.id == relation.id }
            views[idx] = views[idx].copy(
                categories = views[idx].categories.toMutableList().apply {
                    add(
                        CategoryEntity(
                            id = relation.categoryId,
                            dictionaryId = relation.dictionaryId,
                            name = "",
                            widget = WidgetType.PLAIN,
                            iconName = "",
                            sequence = 0,
                            hidden = false,
                            sortable = false
                        )
                    )
                }
            )
        }
    }

    override suspend fun removeViews(dictionaryId: String) {
        views.removeAll { it.dictionaryView.dictionaryId == dictionaryId }
    }

    override suspend fun removeRelations(dictionaryId: String) {}

    override suspend fun findById(dictionaryId: String, id: String): DictionaryViewWithCategories? {
        return views.find { it.dictionaryView.dictionaryId == dictionaryId && it.dictionaryView.id == id }
    }

    internal fun clear() = views.clear()
}