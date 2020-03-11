package com.github.cheapmon.balalaika.ui.search

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.Category
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

data class SearchRestriction(val category: String?, val restriction: String?) : Serializable

class SearchItemFragment : Fragment() {
    private val args: SearchItemFragmentArgs by navArgs()

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
        activity?.search_input?.setText(args.searchText ?: "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.fragment_search_items, container, false)
        val list = root.findViewById<RecyclerView>(R.id.list)
        val header = root.findViewById<ConstraintLayout>(R.id.header)
        setupList(list)
        setupHeader(header)
        return root
    }

    private fun setupList(list: RecyclerView) {
        val viewModel = ViewModelProvider(this).get(SearchItemViewModel::class.java)
        val navController = findNavController()
        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = SearchItemRecyclerViewAdapter(arrayListOf(), viewModel, navController)
            activity?.search_input?.addTextChangedListener((adapter as SearchItemRecyclerViewAdapter).watcher)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                viewModel.restriction = args.restriction
                viewModel.refresh(args.restriction, "")
            }
        }
    }

    private fun setupHeader(header: ConstraintLayout) {
        val res = args.restriction
        if(res?.category != null && res.restriction != null) {
            header.visibility = View.VISIBLE
            header.findViewById<TextView>(R.id.category_name).text = res.category
            header.findViewById<TextView>(R.id.category_restriction).text = res.restriction
            header.findViewById<TextView>(R.id.cancel_button).setOnClickListener {
                header.visibility = View.GONE
                val model = ViewModelProvider(this).get(SearchItemViewModel::class.java)
                findNavController().navigate(SearchItemFragmentDirections.actionNavSearchToSelf(model.searchText, null))
                /*viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        model.restriction = null
                        model.refresh(null, model.searchText)
                    }
                }*/
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }
}