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
package com.github.cheapmon.balalaika.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryItemBinding
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.Widgets

/** Paging Adapter for [DictionaryFragment] */
class DictionaryAdapter(
    private val listener: Listener,
    private val widgetListener: WidgetListener
) : PagingDataAdapter<DictionaryEntry, DictionaryAdapter.ViewHolder>(DictionaryDiff) {
    /** Create view */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentDictionaryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    /**
     * Bind item and add listeners
     *
     * - Collapse entry
     * - Show bookmark state
     * - Group entries and pass to widgets
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position) ?: return
        holder.bind(entry)
    }

    /** @suppress */
    inner class ViewHolder(private val binding: FragmentDictionaryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isBookmark: Boolean = false

        /** Bind single entry to this view holder */
        fun bind(entry: DictionaryEntry) {
            with(binding) {
                lexeme = entry.lexemeWithBase.lexeme
                base = entry.lexemeWithBase.base
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
                isBookmark = entry.lexemeWithBase.lexeme.isBookmark
                updateBookmarkButton(entryBookmarkButton)
                entryBookmarkButton.setOnClickListener {
                    listener.onClickBookmarkButton(entry, isBookmark)
                    isBookmark = !isBookmark
                    updateBookmarkButton(entryBookmarkButton)
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

        private fun updateBookmarkButton(button: ImageButton) {
            if (isBookmark) {
                button.setImageResource(R.drawable.ic_bookmark)
            } else {
                button.setImageResource(R.drawable.ic_bookmark_border)
            }
        }
    }

    private object DictionaryDiff : DiffUtil.ItemCallback<DictionaryEntry>() {
        override fun areItemsTheSame(oldItem: DictionaryEntry, newItem: DictionaryEntry): Boolean {
            return oldItem.lexemeWithBase.lexeme.id == newItem.lexemeWithBase.lexeme.id
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
        /** Callback for whenever a bookmark button is clicked */
        fun onClickBookmarkButton(entry: DictionaryEntry, isBookmark: Boolean)

        /** Callback for whenever a base button is clicked */
        fun onClickBaseButton(entry: DictionaryEntry)
    }
}
