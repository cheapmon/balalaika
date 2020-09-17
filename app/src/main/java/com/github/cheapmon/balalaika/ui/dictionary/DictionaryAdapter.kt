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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryItemBinding
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetActionListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetFactory
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetMenuListener
import com.github.cheapmon.balalaika.util.setIconById
import com.google.android.material.button.MaterialButton

/** Paging Adapter for [DictionaryFragment] */
class DictionaryAdapter(
    private val listener: Listener,
    private val menuListener: WidgetMenuListener,
    private val audioActionListener: WidgetActionListener<Property.Audio>,
    private val referenceActionListener: WidgetActionListener<Property.Reference>,
    private val urlActionListener: WidgetActionListener<Property.Url>,
    private val wordnetActionListener: WidgetActionListener<Property.Wordnet>
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
        fun bind(item: DictionaryEntry) {
            with(binding) {
                entry = item
                entryProperties.visibility = View.VISIBLE
                entryCollapseButton.setOnClickListener {
                    if (entryProperties.visibility == View.GONE) {
                        entryProperties.visibility = View.VISIBLE
                        entryCollapseButton.setIconById(R.drawable.ic_arrow_up)
                    } else {
                        entryProperties.visibility = View.GONE
                        entryCollapseButton.setIconById(R.drawable.ic_arrow_down)
                    }
                }
                entryBaseButton.setOnClickListener {
                    listener.onClickBaseButton(entry)
                }
                isBookmark = item.bookmark != null
                updateBookmarkButton(entryBookmarkButton)
                entryBookmarkButton.setOnClickListener {
                    listener.onClickBookmarkButton(entry, isBookmark)
                    isBookmark = !isBookmark
                    updateBookmarkButton(entryBookmarkButton)
                }
                entryProperties.visibility = View.VISIBLE
                entryProperties.removeAllViews()
                val factory = WidgetFactory(
                    entryProperties,
                    true,
                    menuListener,
                    audioActionListener,
                    referenceActionListener,
                    urlActionListener,
                    wordnetActionListener
                )
                entry.properties.forEach { (category, properties) ->
                    val widget = factory.get(category, properties)
                    entryProperties.addView(widget.create())
                }
            }
        }

        private fun updateBookmarkButton(button: MaterialButton) {
            if (isBookmark) {
                button.setIconById(R.drawable.ic_bookmark)
            } else {
                button.setIconById(R.drawable.ic_bookmark_border)
            }
        }
    }

    private object DictionaryDiff : DiffUtil.ItemCallback<DictionaryEntry>() {
        override fun areItemsTheSame(oldItem: DictionaryEntry, newItem: DictionaryEntry): Boolean {
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
        /** Callback for whenever a bookmark button is clicked */
        fun onClickBookmarkButton(entry: DictionaryEntry, isBookmark: Boolean)

        /** Callback for whenever a base button is clicked */
        fun onClickBaseButton(entry: DictionaryEntry)
    }
}
