package com.github.cheapmon.balalaika.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.data.entities.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentSearchItemBinding

class SearchAdapter(
    private val listener: SearchAdapterListener
) : ListAdapter<Lexeme, SearchAdapter.ViewHolder>(SearchDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentSearchItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lexeme = getItem(position)
        with(holder.binding) {
            name = lexeme.form
            root.setOnClickListener { listener.onClickItem(lexeme) }
        }
    }

    class ViewHolder(val binding: FragmentSearchItemBinding) : RecyclerView.ViewHolder(binding.root)

    object SearchDiff : DiffUtil.ItemCallback<Lexeme>() {
        override fun areItemsTheSame(oldItem: Lexeme, newItem: Lexeme): Boolean {
            return oldItem.lexemeId == newItem.lexemeId
        }

        override fun areContentsTheSame(oldItem: Lexeme, newItem: Lexeme): Boolean {
            return oldItem == newItem
        }
    }

    interface SearchAdapterListener {
        fun onClickItem(lexeme: Lexeme)
    }
}