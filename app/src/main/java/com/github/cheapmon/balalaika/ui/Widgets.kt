package com.github.cheapmon.balalaika.ui

import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat.startActivity
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.ui.home.PropertyLine
import kotlinx.coroutines.CoroutineScope


abstract class Widget {
    abstract fun create(scope: CoroutineScope, group: ViewGroup, line: PropertyLine): View
    fun inflate(group: ViewGroup, id: Int): View {
        return LayoutInflater.from(group.context).inflate(id, group, false)
    }

    companion object {
        private val WIDGET_CODES = mapOf(
                "plain" to PlainWidget,
                "key_value" to KeyValueWidget,
                "lexeme" to LexemeWidget,
                "text_url" to TextUrlWidget
        )

        fun get(scope: CoroutineScope, group: ViewGroup, line: PropertyLine): View {
            val widgetClass: Widget = WIDGET_CODES.getOrElse(line.widget) { KeyValueWidget }
            return widgetClass.create(scope, group, line)
        }
    }
}

object LexemeWidget : Widget() {
    override fun create(scope: CoroutineScope, group: ViewGroup, line: PropertyLine): View {
        val container = LinearLayoutCompat(group.context).apply {
            orientation = LinearLayoutCompat.VERTICAL
            layoutParams = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT
            )
        }
        line.properties.forEach { property ->
            val view = super.inflate(group, R.layout.lexeme_widget_title)
            view.findViewById<TextView>(R.id.title).text = line.fullForm.fullForm
            if (line.fullForm.fullForm != property.value) {
                view.findViewById<TextView>(R.id.lexeme).text = property.value
            } else {
                view.findViewById<TextView>(R.id.lexeme).text = ""
            }
            container.addView(view)
        }
        return container
    }
}

object PlainWidget : Widget() {
    override fun create(scope: CoroutineScope, group: ViewGroup, line: PropertyLine): View {
        val container = LinearLayoutCompat(group.context).apply {
            orientation = LinearLayoutCompat.VERTICAL
            layoutParams = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT
            )
        }
        line.properties.forEach { property ->
            val view = super.inflate(group, R.layout.lexeme_widget_plain).apply {
                this.findViewById<TextView>(R.id.value).text = property.value
            }
            container.addView(view)
        }
        return container
    }
}

object KeyValueWidget : Widget() {
    override fun create(scope: CoroutineScope, group: ViewGroup, line: PropertyLine): View {
        val widgetView = super.inflate(group, R.layout.lexeme_widget_key_value)
        widgetView.findViewById<TextView>(R.id.name).text = line.category
        val container = widgetView.findViewById<LinearLayoutCompat>(R.id.container)
        line.properties.forEach { property ->
            container.addView(TextView(container.context).apply {
                text = property.value
                gravity = Gravity.END
            })
        }
        return widgetView
    }
}

object TextUrlWidget : Widget() {
    override fun create(scope: CoroutineScope, group: ViewGroup, line: PropertyLine): View {
        val widgetView = super.inflate(group, R.layout.lexeme_widget_text_url)
        widgetView.findViewById<TextView>(R.id.category).text = line.category
        val container = widgetView.findViewById<LinearLayoutCompat>(R.id.container)
        line.properties.forEach { property ->
            val value = property.value?.split(Regex(";;;"))?.first()
            val link = property.value?.split(Regex(";;;"))?.getOrNull(1)
            val view = super.inflate(container, R.layout.lexeme_widget_text_url_value)
            if (link != null) {
                view.findViewById<TextView>(R.id.value_with_link).text = value
                view.findViewById<ImageButton>(R.id.link_btn).setOnClickListener {
                    startActivity(view.context, Intent(Intent.ACTION_VIEW, Uri.parse(link)), null)
                }
            } else {
                view.findViewById<TextView>(R.id.value).apply {
                    text = value
                    visibility = View.VISIBLE
                }
                view.findViewById<TextView>(R.id.value_with_link).visibility = View.GONE
                view.findViewById<ImageButton>(R.id.link_btn).visibility = View.GONE
            }
            container.addView(view)
        }
        return widgetView
    }
}
