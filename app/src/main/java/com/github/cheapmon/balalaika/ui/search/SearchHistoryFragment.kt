package com.github.cheapmon.balalaika.ui.search

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchHistoryFragment : Fragment(), CoroutineScope {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_history, container, false)
        val viewAdapter = SearchHistoryAdapter()
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = viewAdapter
            }
        }
        launch {
            val viewModel = ViewModelProvider(this@SearchHistoryFragment)
                    .get(SearchHistoryViewModel::class.java)
            viewModel.navController = findNavController()
            viewAdapter.submitList(viewModel.entries)
        }
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override val coroutineContext: CoroutineContext = Dispatchers.IO
}