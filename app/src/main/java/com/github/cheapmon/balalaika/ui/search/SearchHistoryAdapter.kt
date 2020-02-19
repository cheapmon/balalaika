package com.github.cheapmon.balalaika.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.SearchHistoryEntry
import kotlinx.android.synthetic.main.fragment_search_history_entry.view.*

class SearchHistoryAdapter(private val values: ArrayList<Pair<SearchHistoryEntry, () -> Unit>> = arrayListOf())
    : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_search_history_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.container.setOnClickListener { item.second() }
        holder.queryView.text = item.first.query
        holder.dateView.text = item.first.date.toString()
    }

    override fun getItemCount(): Int = values.size

    fun submitList(list: List<Pair<SearchHistoryEntry, () -> Unit>>) {
        values.clear()
        values.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val container: View = view.container
        val queryView: TextView = view.query
        val dateView: TextView = view.date

        override fun toString(): String {
            return queryView.text.toString()
        }
    }
}