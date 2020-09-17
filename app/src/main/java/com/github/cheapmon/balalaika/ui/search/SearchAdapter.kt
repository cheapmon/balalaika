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
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.databinding.FragmentSearchItemBinding
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetFactory
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
                entry.representation.highlight(searchText, root.context)
            root.setOnClickListener { listener.onClickItem(entry) }
            entryProperties.removeAllViews()
            val factory = WidgetFactory(entryProperties)
            entry.properties
                .forEach { (category, properties) ->
                    val widget = factory.get(category, properties)
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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DictionaryEntry,
            newItem: DictionaryEntry
        ): Boolean {
            return oldItem == newItem
        }
    }

    /** Component that handles actions from this adapter */
    interface Listener {
        /** Callback for whenever a [dictionary entry][DictionaryEntry] is clicked */
        fun onClickItem(dictionaryEntry: DictionaryEntry)
    }
}
