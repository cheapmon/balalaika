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
package com.github.cheapmon.balalaika.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.databinding.FragmentHistoryItemBinding

/** Adapter for [HistoryFragment] */
class HistoryAdapter(
    private val listener: Listener
) : ListAdapter<HistoryEntryWithRestriction, HistoryAdapter.ViewHolder>(HistoryDiff) {
    /** Create view */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentHistoryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    /** Bind item and add listeners */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        with(holder.binding) {
            title = entry.historyEntry.query
            restriction = when (entry.restriction) {
                is SearchRestriction.None -> holder.itemView.context.getString(R.string.no_restriction)
                is SearchRestriction.Some -> root.resources.getString(
                    R.string.search_restriction,
                    entry.restriction.category.name, entry.restriction.restriction
                )
            }
            historyItemDeleteButton.setOnClickListener { listener.onClickDeleteButton(entry) }
            root.setOnClickListener { listener.onClickRedoButton(entry) }
        }
    }

    /** @suppress */
    class ViewHolder(
        val binding: FragmentHistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    /** @suppress */
    private object HistoryDiff : DiffUtil.ItemCallback<HistoryEntryWithRestriction>() {
        override fun areContentsTheSame(
            oldItem: HistoryEntryWithRestriction,
            newItem: HistoryEntryWithRestriction
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: HistoryEntryWithRestriction,
            newItem: HistoryEntryWithRestriction
        ): Boolean {
            return oldItem.historyEntry.historyEntryId == newItem.historyEntry.historyEntryId
        }
    }

    /** Component that handles actions from this adapter */
    interface Listener {
        /** Callback for whenever deletion of a [history entry][HistoryEntry] is requested */
        fun onClickDeleteButton(historyEntry: HistoryEntryWithRestriction)

        /** Callback for whenever a [history entry][HistoryEntry] is clicked */
        fun onClickRedoButton(historyEntry: HistoryEntryWithRestriction)
    }
}

