package com.github.cheapmon.balalaika.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.Category
import com.github.cheapmon.balalaika.data.entities.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentSearchBinding
import com.github.cheapmon.balalaika.util.InjectorUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.io.Serializable

@FlowPreview
@ExperimentalCoroutinesApi
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels {
        InjectorUtil.provideSearchViewModelFactory(requireContext())
    }

    private val args: SearchFragmentArgs by navArgs()

    private lateinit var binding: FragmentSearchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        searchAdapter = SearchAdapter(Listener())
        recyclerView = binding.searchList.apply {
            layoutManager = LinearLayoutManager(this@SearchFragment.context)
            adapter = searchAdapter
            setHasFixedSize(true)
        }
        binding.searchInput.addTextChangedListener(Watcher())
        binding.searchRestriction.setOnCloseIconClickListener {
            viewModel.clearRestriction()
            binding.searchRestriction.visibility = View.GONE
        }
        handleArgs()
        submitData()
        return binding.root
    }

    private fun handleArgs() {
        if (args.query == null) {
            if (viewModel.getQuery() == null) {
                viewModel.setQuery("")
                binding.query = ""
            } else {
                binding.query = viewModel.getQuery()
            }
        } else {
            binding.query = args.query
        }
        val restriction = args.restriction
        if (restriction != null) {
            viewModel.setRestriction(restriction.category.categoryId, restriction.restriction)
            binding.restriction = getString(
                R.string.search_restriction,
                restriction.category.name, restriction.restriction
            )
            binding.searchRestriction.visibility = View.VISIBLE
        } else {
            viewModel.clearRestriction()
            binding.searchRestriction.visibility = View.GONE
        }
    }

    private fun submitData() {
        viewModel.lexemes.observe(viewLifecycleOwner, Observer {
            searchAdapter.submitList(it)
            binding.inProgress = false
            if (it.isEmpty()) {
                binding.searchEmptyIcon.visibility = View.VISIBLE
                binding.searchEmptyText.visibility = View.VISIBLE
            } else {
                binding.searchEmptyIcon.visibility = View.GONE
                binding.searchEmptyText.visibility = View.GONE
            }
        })
    }

    inner class Listener : SearchAdapter.SearchAdapterListener {
        override fun onClickItem(lexeme: Lexeme) {
            saveSearch()
            Snackbar.make(binding.root, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
        }

        private fun saveSearch() {
            val query = viewModel.getQuery() ?: return
            val restriction = viewModel.getRestriction()
            if (restriction != null) {
                viewModel.addToHistory(query, restriction.first, restriction.second ?: "")
            } else {
                viewModel.addToHistory(query)
            }
        }
    }

    inner class Watcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.inProgress = true
            viewModel.setQuery(s.toString().trim())
        }
    }

    data class Restriction(
        val category: Category,
        val restriction: String
    ) : Serializable
}