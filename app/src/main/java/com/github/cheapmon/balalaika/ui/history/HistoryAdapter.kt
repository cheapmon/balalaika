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
import com.github.cheapmon.balalaika.databinding.FragmentHistoryItemBinding
import com.github.cheapmon.balalaika.model.HistoryItem

/** Adapter for [HistoryFragment] */
class HistoryAdapter(
    private val listener: Listener
) : ListAdapter<HistoryItem, HistoryAdapter.ViewHolder>(HistoryDiff) {
    /** Create view */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentHistoryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    /** Bind item and add listeners */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            historyItem = item
            listener = this@HistoryAdapter.listener
        }
    }

    /** @suppress */
    class ViewHolder(
        val binding: FragmentHistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    /** @suppress */
    private object HistoryDiff : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean =
            oldItem == newItem
    }

    /** Component that handles actions from this adapter */
    interface Listener {
        /** Callback for whenever deletion of a [history item][HistoryItem] is requested */
        fun onClickDeleteButton(historyItem: HistoryItem)

        /** Callback for whenever a [history item][HistoryItem] is clicked */
        fun onClickRedoButton(historyItem: HistoryItem)
    }
}
