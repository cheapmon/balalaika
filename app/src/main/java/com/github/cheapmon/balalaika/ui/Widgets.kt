package com.github.cheapmon.balalaika.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.db.LemmaProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class Widget {
    abstract fun create(group: ViewGroup, property: LemmaProperty): View
    fun inflate(group: ViewGroup, id: Int): View {
        return LayoutInflater.from(group.context).inflate(id, group, false)
    }

    companion object {
        lateinit var categories: Map<String, Category>

        suspend fun get(group: ViewGroup, property: LemmaProperty): View {
            if (!this::categories.isInitialized) {
                categories = withContext(Dispatchers.Default) {
                    mapOf(*BalalaikaDatabase.connect().categoryDao().getAll().map { it.id to it }.toTypedArray())
                }
            }
            val widget: Widget = when (categories[property.categoryId]?.widget) {
                "title" -> TitleWidget
                "plain" -> PlainWidget
                else -> PlainWidget
            }
            return widget.create(group, property)
        }
    }
}

object TitleWidget : Widget() {
    override fun create(group: ViewGroup, property: LemmaProperty): View {
        val view = super.inflate(group, R.layout.lexeme_widget_title)
        view.findViewById<TextView>(R.id.title).text = property.value
        return view
    }
}

object PlainWidget : Widget() {
    override fun create(group: ViewGroup, property: LemmaProperty): View {
        val view = super.inflate(group, R.layout.lexeme_widget_plain)
        view.findViewById<TextView>(R.id.name).text = categories[property.categoryId]?.name
        view.findViewById<TextView>(R.id.value).text = property.value
        return view
    }
}
