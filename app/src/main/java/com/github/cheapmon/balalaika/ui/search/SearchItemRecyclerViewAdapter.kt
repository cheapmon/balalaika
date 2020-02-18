package com.github.cheapmon.balalaika.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import kotlinx.android.synthetic.main.fragment_search_item.view.*

class SearchItemRecyclerViewAdapter(
        private val values: List<String>)
    : RecyclerView.Adapter<SearchItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contentView.text = values[position]
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val contentView: TextView = view.content

        override fun toString(): String {
            return "${contentView.text}"
        }
    }
}