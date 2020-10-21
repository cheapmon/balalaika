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

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.components.DictionaryEntryCard
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property

/** Paging Adapter for [DictionaryFragment] */
class DictionaryAdapter(
    private val onClickBase: (DictionaryEntry) -> Unit,
    private val onBookmark: (DictionaryEntry) -> Unit,
    private val onClickProperty: (DataCategory, Property, String) -> Unit
) : PagingDataAdapter<DictionaryEntry, DictionaryAdapter.ViewHolder>(DictionaryDiff) {
    /** Create view */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ComposeView(parent.context))
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
    inner class ViewHolder(private val view: ComposeView) : RecyclerView.ViewHolder(view) {
        /** Bind single entry to this view holder */
        fun bind(item: DictionaryEntry) {
            view.setContent {
                DictionaryEntryCard(
                    dictionaryEntry = item,
                    onClickBase = onClickBase,
                    onBookmark = onBookmark,
                    onClickProperty = onClickProperty
                )
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
}
