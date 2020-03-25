package com.github.cheapmon.balalaika.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.DictionaryEntry
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryItemBinding

class DictionaryAdapter(
    private val listener: DictionaryAdapterListener
) : ListAdapter<DictionaryEntry, DictionaryAdapter.ViewHolder>(DictionaryDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentDictionaryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dictionaryEntry = getItem(position)
        holder.binding.lexeme = dictionaryEntry.lexeme
        holder.binding.base = dictionaryEntry.base
        holder.binding.entryCollapseButton.setOnClickListener {
            if (holder.binding.entryProperties.visibility == View.GONE) {
                holder.binding.entryProperties.visibility = View.VISIBLE
                holder.binding.entryCollapseButton.setImageResource(R.drawable.ic_arrow_up)
            } else {
                holder.binding.entryProperties.visibility = View.GONE
                holder.binding.entryCollapseButton.setImageResource(R.drawable.ic_arrow_down)
            }
        }
        holder.binding.entryBookmarkButton.setOnClickListener {
            listener.onClickBookmarkButton(dictionaryEntry)
        }
        // TODO: Insert props
    }

    class ViewHolder(val binding: FragmentDictionaryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    object DictionaryDiff : DiffUtil.ItemCallback<DictionaryEntry>() {
        override fun areItemsTheSame(oldItem: DictionaryEntry, newItem: DictionaryEntry): Boolean {
            return oldItem.lexeme.lexemeId == newItem.lexeme.lexemeId
        }

        override fun areContentsTheSame(
            oldItem: DictionaryEntry,
            newItem: DictionaryEntry
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface DictionaryAdapterListener {
        fun onClickBookmarkButton(dictionaryEntry: DictionaryEntry)
    }
}