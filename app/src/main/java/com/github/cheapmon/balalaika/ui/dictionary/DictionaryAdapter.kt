package com.github.cheapmon.balalaika.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.DictionaryEntry
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryItemBinding
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.Widgets

class DictionaryAdapter(
    private val listener: Listener,
    private val widgetListener: WidgetListener
) : ListAdapter<DictionaryEntry, DictionaryAdapter.ViewHolder>(DictionaryDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentDictionaryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dictionaryEntry = getItem(position)
        with(holder.binding) {
            lexeme = dictionaryEntry.lexeme
            base = dictionaryEntry.base
            entryCollapseButton.setOnClickListener {
                if (entryProperties.visibility == View.GONE) {
                    entryProperties.visibility = View.VISIBLE
                    entryCollapseButton.setImageResource(R.drawable.ic_arrow_up)
                } else {
                    entryProperties.visibility = View.GONE
                    entryCollapseButton.setImageResource(R.drawable.ic_arrow_down)
                }
            }
            entryBaseButton.setOnClickListener {
                listener.onClickBaseButton(dictionaryEntry)
            }
            if (dictionaryEntry.lexeme.isBookmark) {
                entryBookmarkButton.setImageResource(R.drawable.ic_bookmark)
            } else {
                entryBookmarkButton.setImageResource(R.drawable.ic_bookmark_border)
            }
            entryBookmarkButton.setOnClickListener {
                listener.onClickBookmarkButton(dictionaryEntry)
            }
            entryProperties.visibility = View.VISIBLE
            entryProperties.removeAllViews()
            dictionaryEntry.properties.groupBy { it.category }
                .toSortedMap(Comparator { o1, o2 -> o1.sequence.compareTo(o2.sequence) })
                .forEach { (category, properties) ->
                    val widget = Widgets.get(entryProperties, widgetListener, category, properties)
                    entryProperties.addView(widget.create())
                }
        }
    }

    class ViewHolder(val binding: FragmentDictionaryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    object DictionaryDiff : DiffUtil.ItemCallback<DictionaryEntry>() {
        override fun areItemsTheSame(oldItem: DictionaryEntry, newItem: DictionaryEntry): Boolean {
            return oldItem.lexeme.lexemeId == newItem.lexeme.lexemeId
        }

        override fun areContentsTheSame(
            oldItem: DictionaryEntry,
            newItem: DictionaryEntry
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onClickBookmarkButton(dictionaryEntry: DictionaryEntry)
        fun onClickBaseButton(dictionaryEntry: DictionaryEntry)
    }
}