package com.github.cheapmon.balalaika.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.entry.GroupedEntry
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryItemBinding
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.Widgets

class DictionaryAdapter(
    private val listener: Listener,
    private val widgetListener: WidgetListener
) : PagedListAdapter<GroupedEntry, DictionaryAdapter.ViewHolder>(DictionaryDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentDictionaryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position) ?: return
        with(holder.binding) {
            lexeme = entry.lexeme
            base = entry.base
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
                listener.onClickBaseButton(entry)
            }
            if (entry.lexeme.isBookmark) {
                entryBookmarkButton.setImageResource(R.drawable.ic_bookmark)
            } else {
                entryBookmarkButton.setImageResource(R.drawable.ic_bookmark_border)
            }
            entryBookmarkButton.setOnClickListener {
                listener.onClickBookmarkButton(entry)
            }
            entryProperties.visibility = View.VISIBLE
            entryProperties.removeAllViews()
            entry.properties.groupBy { it.category }
                .toSortedMap(Comparator { o1, o2 -> o1.sequence.compareTo(o2.sequence) })
                .forEach { (category, properties) ->
                    val widget = Widgets.get(entryProperties, widgetListener, category, properties)
                    entryProperties.addView(widget.create())
                }
        }
    }

    class ViewHolder(val binding: FragmentDictionaryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    object DictionaryDiff : DiffUtil.ItemCallback<GroupedEntry>() {
        override fun areItemsTheSame(oldItem: GroupedEntry, newItem: GroupedEntry): Boolean {
            return oldItem.lexeme.lexemeId == newItem.lexeme.lexemeId
        }

        override fun areContentsTheSame(
            oldItem: GroupedEntry,
            newItem: GroupedEntry
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onClickBookmarkButton(dictionaryEntry: GroupedEntry)
        fun onClickBaseButton(dictionaryEntry: GroupedEntry)
    }
}