package com.github.cheapmon.balalaika.ui.search

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.FullForm
import kotlinx.android.synthetic.main.fragment_search_item.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchItemRecyclerViewAdapter(
        private val values: ArrayList<FullForm>,
        private val viewModel: SearchItemViewModel,
        private val navController: NavController
) : RecyclerView.Adapter<SearchItemRecyclerViewAdapter.ViewHolder>(), CoroutineScope {
    var job: Job = Job()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contentView.text = values[position].fullForm
        holder.view.setOnClickListener {
            val direction = SearchItemFragmentDirections.actionSearchItemFragmentToNavHome2(values[position].id)
            navController.navigate(direction)
        }
    }

    override fun getItemCount(): Int = values.size

    val watcher = object : TextWatcher {
        private var searchFor = ""
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val text = s.toString().trim()
            if (text == searchFor || text.isEmpty() || text.length < 3) return
            searchFor = text
            job.cancel()
            job = launch {
                delay(300)
                if (text != searchFor) return@launch
                withContext(Dispatchers.IO) {
                    viewModel.searchText = text
                }
                values.clear()
                values.addAll(viewModel.items)
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val contentView: TextView = view.content

        override fun toString(): String {
            return "${contentView.text}"
        }
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main
}