package com.github.cheapmon.balalaika.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.HistoryEntryWithCategory
import com.github.cheapmon.balalaika.databinding.FragmentHistoryItemBinding

class HistoryAdapter(
    private val listener: HistoryAdapterListener
) : ListAdapter<HistoryEntryWithCategory, HistoryAdapter.ViewHolder>(HistoryDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentHistoryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        holder.binding.title = entry.historyEntry.query
        if (entry.category != null) {
            holder.binding.restriction = "${entry.category.name}: ${entry.historyEntry.restriction}"
        } else {
            holder.binding.restriction = holder.itemView.context.getString(R.string.no_restriction)
        }
        holder.binding.historyItemDeleteButton.setOnClickListener {
            listener.onClickDeleteButton(entry)
        }
        holder.binding.historyItemRedoButton.setOnClickListener {
            listener.onClickRedoButton(entry)
        }
    }

    class ViewHolder(
        val binding: FragmentHistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    object HistoryDiff : DiffUtil.ItemCallback<HistoryEntryWithCategory>() {
        override fun areContentsTheSame(
            oldItem: HistoryEntryWithCategory,
            newItem: HistoryEntryWithCategory
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: HistoryEntryWithCategory,
            newItem: HistoryEntryWithCategory
        ): Boolean {
            return oldItem.historyEntry.historyEntryId == newItem.historyEntry.historyEntryId
        }
    }

    interface HistoryAdapterListener {
        fun onClickDeleteButton(historyEntry: HistoryEntryWithCategory)
        fun onClickRedoButton(historyEntry: HistoryEntryWithCategory)
    }
}

