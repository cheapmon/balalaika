package com.github.cheapmon.balalaika.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.data.entities.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.Lexeme
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import com.github.cheapmon.balalaika.databinding.FragmentSearchItemBinding
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.Widgets
import com.github.cheapmon.balalaika.util.highlight

class SearchAdapter(
    private val listener: Listener
) : ListAdapter<DictionaryEntry, SearchAdapter.ViewHolder>(SearchDiff) {
    private var searchText: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentSearchItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dictionaryEntry = getItem(position)
        with(holder.binding) {
            entryTitle.text = dictionaryEntry.lexeme.form.highlight(searchText, root.context)
            root.setOnClickListener { listener.onClickItem(dictionaryEntry.lexeme) }
            entryProperties.removeAllViews()
            dictionaryEntry.properties
                .filter { it.property.value.contains(searchText) }
                .also { entryProperties.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE }
                .groupBy { it.category }
                .toSortedMap(Comparator { o1, o2 -> o1.sequence.compareTo(o2.sequence) })
                .forEach { (category, properties) ->
                    val widget = Widgets.get(
                        entryProperties,
                        WListener(),
                        category,
                        properties,
                        false,
                        searchText
                    )
                    entryProperties.addView(widget.create())
                }
        }
    }

    fun submitSearchText(text: String) {
        searchText = text
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: FragmentSearchItemBinding) : RecyclerView.ViewHolder(binding.root)

    object SearchDiff : DiffUtil.ItemCallback<DictionaryEntry>() {
        override fun areItemsTheSame(
            oldItem: DictionaryEntry,
            newItem: DictionaryEntry
        ): Boolean {
            return oldItem.lexeme.lexemeId == newItem.lexeme.lexemeId
        }

        override fun areContentsTheSame(
            oldItem: DictionaryEntry,
            newItem: DictionaryEntry
        ): Boolean {
            return oldItem == newItem
        }
    }

    private class WListener : WidgetListener {
        override fun onClickAudioButton(resId: Int) = Unit
        override fun onClickSearchButton(query: String, restriction: SearchRestriction) = Unit
        override fun onClickScrollButton(externalId: String) = Unit
        override fun onClickLinkButton(link: String) = Unit
    }

    interface Listener {
        fun onClickItem(lexeme: Lexeme)
    }
}