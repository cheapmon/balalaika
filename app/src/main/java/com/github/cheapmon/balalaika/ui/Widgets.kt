package com.github.cheapmon.balalaika.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.db.LemmaProperty
import com.github.cheapmon.balalaika.db.Lexeme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Widget {
    abstract fun create(group: ViewGroup, lexeme: Lexeme, property: LemmaProperty): View
    fun inflate(group: ViewGroup, id: Int): View {
        return LayoutInflater.from(group.context).inflate(id, group, false)
    }

    companion object {
        lateinit var categories: Map<String, Category>

        suspend fun get(group: ViewGroup, lexeme: Lexeme, property: LemmaProperty): View {
            if (!this::categories.isInitialized) {
                categories = withContext(Dispatchers.Default) {
                    mapOf(*BalalaikaDatabase.instance.categoryDao().getAll().map { it.id to it }.toTypedArray())
                }
            }
            val widget: Widget = when (categories[property.categoryId]?.widget) {
                "lemma" -> LemmaWidget
                "plain" -> PlainWidget
                else -> PlainWidget
            }
            return widget.create(group, lexeme, property)
        }
    }
}

object LemmaWidget : Widget() {
    override fun create(group: ViewGroup, lexeme: Lexeme, property: LemmaProperty): View {
        val view = super.inflate(group, R.layout.lexeme_widget_title)
        view.findViewById<TextView>(R.id.title).text = lexeme.lexeme
        view.findViewById<TextView>(R.id.lemma).text = property.value
        return view
    }
}

object PlainWidget : Widget() {
    override fun create(group: ViewGroup, lexeme: Lexeme, property: LemmaProperty): View {
        val view = super.inflate(group, R.layout.lexeme_widget_plain)
        view.findViewById<TextView>(R.id.name).text = categories[property.categoryId]?.name
        view.findViewById<TextView>(R.id.value).text = property.value
        return view
    }
}
