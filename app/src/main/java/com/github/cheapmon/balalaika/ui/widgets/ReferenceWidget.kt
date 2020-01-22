package com.github.cheapmon.balalaika.ui.widgets

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentManager
import com.github.cheapmon.balalaika.PropertyLine
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.ui.home.DictionaryDialog
import com.github.cheapmon.balalaika.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReferenceWidget(
        private val group: ViewGroup,
        private val line: PropertyLine,
        private val scope: CoroutineScope,
        private val adapter: HomeFragment.HomeAdapter
) : Widget {
    override fun createView(): View {
        val widgetView = WidgetHelper.inflate(group, R.layout.lexeme_widget_reference)
        widgetView.findViewById<TextView>(R.id.category).text = line.category
        val container = widgetView.findViewById<LinearLayoutCompat>(R.id.container)
        line.properties.forEach { property ->
            val value = property.value?.split(Regex(";;;"))?.first()
            val link = property.value?.split(Regex(";;;"))?.getOrNull(1)
            val view = WidgetHelper.inflate(container, R.layout.lexeme_widget_reference_value)
            if (link != null) {
                view.findViewById<TextView>(R.id.value_with_link).text = value
                view.findViewById<ImageButton>(R.id.link_btn).setOnClickListener {
                    scrollToReference(link)
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

    override fun createContextMenu(fragmentManager: FragmentManager?): DictionaryDialog? {
        TODO("not implemented")
    }

    private fun scrollToReference(link: String) {
        scope.launch {
            val pos = withContext(Dispatchers.IO) {
                val form = BalalaikaDatabase.instance.fullFormDao().getById(link)?.fullForm ?: ""
                BalalaikaDatabase.instance.fullFormDao().getPositionOf(form)
            }
            adapter.recyclerView?.scrollToPosition(pos)
        }
    }
}

object ReferenceWidgetBuilder : WidgetBuilder {
    override fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): Widget {
        return ReferenceWidget(group, line, scope, adapter)
    }
}