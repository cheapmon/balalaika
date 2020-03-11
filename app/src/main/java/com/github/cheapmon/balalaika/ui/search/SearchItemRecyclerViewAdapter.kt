package com.github.cheapmon.balalaika.ui.search

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.switchMap
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.FullForm
import com.github.cheapmon.balalaika.db.SearchHistoryEntry
import kotlinx.android.synthetic.main.fragment_search_item.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class SearchItemRecyclerViewAdapter(
        private val values: ArrayList<FullForm>,
        private val viewModel: SearchItemViewModel,
        private val navController: NavController
) : RecyclerView.Adapter<SearchItemRecyclerViewAdapter.ViewHolder>(), CoroutineScope {
    private var job: Job = Job()
    private var searchText: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contentView.text = values[position].fullForm
        holder.view.setOnClickListener {
            navigateHome(values[position].id, viewModel.restriction?.category, viewModel.restriction?.restriction)
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
                viewModel.searchText = text
                searchText = text
                values.clear()
                viewModel.forms.switchMap { BalalaikaDatabase.instance.fullFormDao().getAllById(it) }.observeForever {
                    if (it.size == 1) {
                        navigateHome(it.first().id, viewModel.restriction?.category, viewModel.restriction?.restriction)
                    }
                    values.addAll(it)
                    notifyDataSetChanged()
                }
                viewModel.props.switchMap { BalalaikaDatabase.instance.fullFormDao().getAllById(it) }.observeForever {
                    values.addAll(it.filter { form -> values.find { f -> f.id == form.id } == null })
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun navigateHome(fullFormId: String, category: String? = null, restriction: String? = null) {
        launch {
            withContext(Dispatchers.IO) {
                val id = if (category != null) {
                    BalalaikaDatabase.instance.categoryDao().findByName(category).value?.id
                } else null
                val entry = SearchHistoryEntry(id = 0, query = searchText, date = Date(),
                        categoryId = id, value = restriction)
                BalalaikaDatabase.instance.searchHistoryDao().insert(entry)
            }
        }
        val direction = SearchItemFragmentDirections.actionSearchToHome(fullFormId)
        navController.navigate(direction)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val contentView: TextView = view.content

        override fun toString(): String {
            return "${contentView.text}"
        }
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main
}