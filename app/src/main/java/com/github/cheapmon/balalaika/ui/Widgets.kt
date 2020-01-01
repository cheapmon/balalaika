package com.github.cheapmon.balalaika.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.LemmaProperty
import com.github.cheapmon.balalaika.db.Lexeme
import com.github.cheapmon.balalaika.ui.home.DictionaryEntry
import com.github.cheapmon.balalaika.ui.home.PropertyLine

abstract class Widget {
    abstract fun create(group: ViewGroup, line: PropertyLine): View
    fun inflate(group: ViewGroup, id: Int): View {
        return LayoutInflater.from(group.context).inflate(id, group, false)
    }

    companion object {
        const val PLAIN = "plain"
        const val LEMMA = "lemma"

        fun get(group: ViewGroup, line: PropertyLine): View {
            val widgetClass = when (line.widget) {
                LEMMA -> LemmaWidget
                PLAIN -> PlainWidget
                else -> PlainWidget
            }
            return widgetClass.create(group, line)
        }
    }
}

object LemmaWidget : Widget() {
    override fun create(group: ViewGroup, line: PropertyLine): View {
        val view = super.inflate(group, R.layout.lexeme_widget_title)
        view.findViewById<TextView>(R.id.title).text = line.lexeme.lexeme
        view.findViewById<TextView>(R.id.lemma).text = line.property.value
        return view
    }
}

object PlainWidget : Widget() {
    override fun create(group: ViewGroup, line: PropertyLine): View {
        val view = super.inflate(group, R.layout.lexeme_widget_plain)
        view.findViewById<TextView>(R.id.name).text = line.category
        view.findViewById<TextView>(R.id.value).text = line.property.value
        return view
    }
}
