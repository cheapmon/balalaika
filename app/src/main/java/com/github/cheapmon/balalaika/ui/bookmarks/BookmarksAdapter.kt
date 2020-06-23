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
package com.github.cheapmon.balalaika.ui.bookmarks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.databinding.FragmentBookmarksItemBinding
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme

/** Adapter for [BookmarksFragment] */
class BookmarksAdapter(
    private val listener: Listener
) : ListAdapter<Lexeme, BookmarksAdapter.ViewHolder>(BookmarksDiff) {
    /** Create view */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentBookmarksItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    /** Bind item and add listeners */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        with(holder.binding) {
            lexeme = entry
            bookmarksItemDeleteButton.setOnClickListener { listener.onClickDeleteButton(entry) }
            root.setOnClickListener { listener.onClickRedoButton(entry) }
        }
    }

    /** @suppress */
    class ViewHolder(
        val binding: FragmentBookmarksItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private object BookmarksDiff : DiffUtil.ItemCallback<Lexeme>() {
        override fun areContentsTheSame(
            oldItem: Lexeme,
            newItem: Lexeme
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: Lexeme,
            newItem: Lexeme
        ): Boolean {
            return oldItem.lexemeId == newItem.lexemeId
        }
    }

    /** Component that handles actions from this adapter */
    interface Listener {
        /** Callback for whenever deletion of a [lexeme][Lexeme] is requested */
        fun onClickDeleteButton(lexeme: Lexeme)

        /** Callback for whenever a [lexeme][Lexeme] is clicked */
        fun onClickRedoButton(lexeme: Lexeme)
    }
}
