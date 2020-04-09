package com.github.cheapmon.balalaika.data.entities.history

import com.github.cheapmon.balalaika.data.entities.category.Category
import java.io.Serializable

sealed class SearchRestriction : Serializable {
    object None : SearchRestriction()
    data class Some(
        val category: Category,
        val restriction: String
    ) : SearchRestriction()
}