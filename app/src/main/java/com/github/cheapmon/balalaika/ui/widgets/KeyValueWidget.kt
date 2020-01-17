package com.github.cheapmon.balalaika.ui.widgets

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.ui.home.HomeFragment
import com.github.cheapmon.balalaika.ui.home.PropertyLine
import kotlinx.coroutines.CoroutineScope

object KeyValueWidget : Widget() {
    override fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): View {
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