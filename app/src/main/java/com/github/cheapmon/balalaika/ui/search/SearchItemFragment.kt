package com.github.cheapmon.balalaika.ui.search

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import kotlinx.android.synthetic.main.app_bar_main.*

class SearchItemFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSearchInput()
    }

    override fun onResume() {
        super.onResume()
        showSearchInput()
    }

    private fun showSearchInput() {
        activity?.search_input?.visibility = View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_items, container, false)
        setHasOptionsMenu(true)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = SearchItemRecyclerViewAdapter(listOf("A", "B", "C"))
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }
}