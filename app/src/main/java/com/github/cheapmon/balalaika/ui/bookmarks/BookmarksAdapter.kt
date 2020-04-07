package com.github.cheapmon.balalaika.ui.bookmarks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.data.entities.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentBookmarksItemBinding

class BookmarksAdapter(
    private val listener: Listener
) : ListAdapter<Lexeme, BookmarksAdapter.ViewHolder>(BookmarksDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentBookmarksItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        with(holder.binding) {
            lexeme = entry
            bookmarksItemDeleteButton.setOnClickListener { listener.onClickDeleteButton(entry) }
            bookmarksItemRedoButton.setOnClickListener { listener.onClickRedoButton(entry) }
        }
    }

    class ViewHolder(
        val binding: FragmentBookmarksItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    object BookmarksDiff : DiffUtil.ItemCallback<Lexeme>() {
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

    interface Listener {
        fun onClickDeleteButton(lexeme: Lexeme)
        fun onClickRedoButton(lexeme: Lexeme)
    }
}

