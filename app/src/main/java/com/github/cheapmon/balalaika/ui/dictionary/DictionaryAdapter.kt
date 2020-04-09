package com.github.cheapmon.balalaika.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.entry.GroupedEntry
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryItemBinding
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.Widgets

class DictionaryAdapter(
    private val listener: Listener,
    private val widgetListener: WidgetListener
) : PagedListAdapter<Lexeme, DictionaryAdapter.ViewHolder>(DictionaryDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentDictionaryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lex = getItem(position) ?: return
        listener.onLoadLexeme(lex) { entry ->
            with(holder.binding) {
                if(entry == null) {
                    lexeme = lex
                    base = null
                    entryProperties.visibility = View.GONE
                    return@with
                }
                lexeme = entry.lexeme
                base = entry.base
                entryProperties.visibility = View.VISIBLE
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
                    .forEach { (category, properties) ->
                        val widget =
                            Widgets.get(entryProperties, widgetListener, category, properties)
                        entryProperties.addView(widget.create())
                    }
            }
        }
    }

    class ViewHolder(val binding: FragmentDictionaryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    object DictionaryDiff : DiffUtil.ItemCallback<Lexeme>() {
        override fun areItemsTheSame(oldItem: Lexeme, newItem: Lexeme): Boolean {
            return oldItem.lexemeId == newItem.lexemeId
        }

        override fun areContentsTheSame(
            oldItem: Lexeme,
            newItem: Lexeme
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onClickBookmarkButton(entry: GroupedEntry)
        fun onClickBaseButton(entry: GroupedEntry)
        fun onLoadLexeme(lexeme: Lexeme, block: (entry: GroupedEntry?) -> Unit)
    }
}