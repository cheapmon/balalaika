package com.github.cheapmon.balalaika.util

import com.github.cheapmon.balalaika.data.entities.DictionaryEntry

typealias ComparatorMap = HashMap<String, Comparator<DictionaryEntry>>

object ComparatorUtil {
    const val DEFAULT_KEY = "Default"
    val comparators: ComparatorMap

    init {
        val defaultComparator: Comparator<DictionaryEntry> = Comparator { d1, d2 ->
            d1.lexeme.form.compareTo(d2.lexeme.form)
        }
        comparators = hashMapOf(DEFAULT_KEY to defaultComparator)
    }

    fun addPropertyComparator(name: String, categoryId: Long) {
        comparators[name] = Comparator { d1, d2 ->
            val prop1 = d1.properties.find { prop -> prop.category.categoryId == categoryId }
            val prop2 = d2.properties.find { prop -> prop.category.categoryId == categoryId }
            if (prop1 != null && prop2 != null) prop1.property.value.compareTo(prop2.property.value)
            else d1.lexeme.form.compareTo(d2.lexeme.form)
        }
    }
}