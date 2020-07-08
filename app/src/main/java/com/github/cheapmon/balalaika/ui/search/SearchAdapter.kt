/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.databinding.FragmentSearchItemBinding
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.Widgets
import com.github.cheapmon.balalaika.util.highlight

/** Adapter for [SearchFragment] */
class SearchAdapter(
    private val listener: Listener
) : PagingDataAdapter<DictionaryEntry, SearchAdapter.ViewHolder>(SearchDiff) {
    private var searchText: String = ""

    /** Create view */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentSearchItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    /** Bind item and add listeners */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position) ?: return
        with(holder.binding) {
            entryTitle.text =
                entry.lexemeWithBase.lexeme.form.highlight(searchText, root.context)
            val props = entry.properties.filter { it.property.value.contains(searchText) }
            if (props.isEmpty()) {
                entryProperties.visibility = View.GONE
            } else {
                entryProperties.visibility = View.VISIBLE
            }
            root.setOnClickListener { listener.onClickItem(entry.lexemeWithBase.lexeme) }
            entryProperties.removeAllViews()
            props
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

    /** Update search query */
    fun submitSearchText(text: String) {
        searchText = text
        notifyDataSetChanged()
    }

    /** @suppress */
    class ViewHolder(val binding: FragmentSearchItemBinding) : RecyclerView.ViewHolder(binding.root)

    /** @suppress */
    private object SearchDiff : DiffUtil.ItemCallback<DictionaryEntry>() {
        override fun areItemsTheSame(
            oldItem: DictionaryEntry,
            newItem: DictionaryEntry
        ): Boolean {
            return oldItem.lexemeWithBase.lexeme.id == newItem.lexemeWithBase.lexeme.id
        }

        override fun areContentsTheSame(
            oldItem: DictionaryEntry,
            newItem: DictionaryEntry
        ): Boolean {
            return oldItem == newItem
        }
    }

    /** Dummy widget listener that ignores all actions */
    private class WListener : WidgetListener {
        override fun onClickAudioButton(resId: Int) = Unit
        override fun onClickSearchButton(query: String, restriction: SearchRestriction) = Unit
        override fun onClickScrollButton(externalId: String) = Unit
        override fun onClickLinkButton(link: String) = Unit
    }

    /** Component that handles actions from this adapter */
    interface Listener {
        /** Callback for whenever a [lexeme][Lexeme] is clicked */
        fun onClickItem(lexeme: Lexeme)
    }
}
