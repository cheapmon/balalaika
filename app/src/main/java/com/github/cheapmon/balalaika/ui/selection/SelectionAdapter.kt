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
package com.github.cheapmon.balalaika.ui.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.databinding.FragmentSelectionItemBinding

/** Adapter for [SelectionListFragment] */
class SelectionAdapter(
    private val listener: Listener
) : ListAdapter<Dictionary, SelectionAdapter.ViewHolder>(Diff) {
    /** Create view */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentSelectionItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    /** Bind item and add listeners */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            dictionary = item
            root.setOnClickListener { listener.onClickDictionary(item) }
        }
    }

    /** @suppress */
    class ViewHolder(
        val binding: FragmentSelectionItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    /** @suppress */
    object Diff : DiffUtil.ItemCallback<Dictionary>() {
        override fun areContentsTheSame(
            oldItem: Dictionary,
            newItem: Dictionary
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: Dictionary,
            newItem: Dictionary
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /** Component that handles actions from this adapter */
    interface Listener {
        /** Callback for whenever a dictionary is clicked */
        fun onClickDictionary(dictionary: Dictionary)
    }
}
