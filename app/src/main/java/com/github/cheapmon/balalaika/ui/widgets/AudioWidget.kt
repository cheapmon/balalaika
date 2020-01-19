package com.github.cheapmon.balalaika.ui.widgets

import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.ui.home.HomeFragment
import com.github.cheapmon.balalaika.ui.home.PropertyLine
import kotlinx.coroutines.CoroutineScope

object AudioWidget : Widget() {
    override fun create(
            adapter: HomeFragment.HomeAdapter,
            scope: CoroutineScope,
            group: ViewGroup,
            line: PropertyLine
    ): View {
        val widgetView = super.inflate(group, R.layout.lexeme_widget_audio)
        widgetView.findViewById<TextView>(R.id.category).text = line.category
        val container = widgetView.findViewById<LinearLayoutCompat>(R.id.container)
        line.properties.forEach { property ->
            val value = property.value?.split(Regex(";;;"))?.first()
            val link = property.value?.split(Regex(";;;"))?.getOrNull(1)
            val view = super.inflate(container, R.layout.lexeme_widget_audio_value)
            if (link != null) {
                view.findViewById<TextView>(R.id.value_with_link).text = value
                view.findViewById<ImageButton>(R.id.link_btn).setOnClickListener {
                    val mediaPlayer = MediaPlayer.create(
                            group.context,
                            group.resources.getIdentifier(link, "raw", "com.github.cheapmon.balalaika")
                    )
                    mediaPlayer.start()
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