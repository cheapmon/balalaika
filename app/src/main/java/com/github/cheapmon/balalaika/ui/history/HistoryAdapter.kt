package com.github.cheapmon.balalaika.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import com.github.cheapmon.balalaika.databinding.FragmentHistoryItemBinding

class HistoryAdapter(
    private val listener: Listener
) : ListAdapter<HistoryEntryWithRestriction, HistoryAdapter.ViewHolder>(HistoryDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentHistoryItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

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
            historyItemRedoButton.setOnClickListener { listener.onClickRedoButton(entry) }
        }
    }

    class ViewHolder(
        val binding: FragmentHistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    object HistoryDiff : DiffUtil.ItemCallback<HistoryEntryWithRestriction>() {
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

    interface Listener {
        fun onClickDeleteButton(historyEntry: HistoryEntryWithRestriction)
        fun onClickRedoButton(historyEntry: HistoryEntryWithRestriction)
    }
}

