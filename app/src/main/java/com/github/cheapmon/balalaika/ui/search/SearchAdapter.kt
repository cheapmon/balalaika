package com.github.cheapmon.balalaika.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.data.entities.entry.GroupedEntry
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentSearchItemBinding
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.Widgets
import com.github.cheapmon.balalaika.util.highlight

class SearchAdapter(
    private val listener: Listener
) : PagedListAdapter<Lexeme, SearchAdapter.ViewHolder>(SearchDiff) {
    private var searchText: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentSearchItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lex = getItem(position) ?: return
        listener.onLoadLexeme(lex) { entry ->
            with(holder.binding) {
                if (entry == null || entry.properties.isEmpty()) {
                    entryTitle.text = lex.form.highlight(searchText, root.context)
                    entryProperties.visibility = View.GONE
                    return@with
                }
                entryTitle.text = entry.lexeme.form.highlight(searchText, root.context)
                entryProperties.visibility = View.VISIBLE
                root.setOnClickListener { listener.onClickItem(entry.lexeme) }
                entryProperties.removeAllViews()
                entry.properties
                    .filter { it.property.value.contains(searchText) }
                    .groupBy { it.category }
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
    }

    fun submitSearchText(text: String) {
        searchText = text
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: FragmentSearchItemBinding) : RecyclerView.ViewHolder(binding.root)

    object SearchDiff : DiffUtil.ItemCallback<Lexeme>() {
        override fun areItemsTheSame(
            oldItem: Lexeme,
            newItem: Lexeme
        ): Boolean {
            return oldItem.lexemeId == newItem.lexemeId
        }

        override fun areContentsTheSame(
            oldItem: Lexeme,
            newItem: Lexeme
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
        fun onLoadLexeme(lexeme: Lexeme, block: (GroupedEntry?) -> Unit)
    }
}