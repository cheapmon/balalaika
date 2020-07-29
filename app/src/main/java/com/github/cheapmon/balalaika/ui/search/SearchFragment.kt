/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.github.cheapmon.balalaika.MainViewModel
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSearchBinding
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.ui.RecyclerViewFragment
import com.github.cheapmon.balalaika.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment for querying the database
 *
 * The user can:
 * - Search for dictionary entries using a query
 * - Apply restriction to the search query
 * - Show an entry in the dictionary
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchFragment :
    RecyclerViewFragment<SearchViewModel, FragmentSearchBinding, SearchAdapter>(
        SearchViewModel::class,
        R.layout.fragment_search,
        false
    ), SearchAdapter.Listener, SearchView.OnQueryTextListener {
    private val activityViewModel: MainViewModel by activityViewModels()

    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel.toggleSearch()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        activityViewModel.addToHistory()
        activityViewModel.toggleSearch()
        super.onDestroyView()
    }

    override fun onCreateBinding(binding: FragmentSearchBinding) {
        super.onCreateBinding(binding)
        binding.searchRestriction.setOnCloseIconClickListener {
            viewModel.setRestriction(SearchRestriction.None)
        }

        searchView = (activity as? AppCompatActivity)?.findViewById(R.id.main_search)
        searchView?.setOnQueryTextListener(this)
    }

    override fun createRecyclerView(binding: FragmentSearchBinding) =
        binding.searchList

    override fun createRecyclerViewAdapter() =
        SearchAdapter(this)

    override fun observeData(
        binding: FragmentSearchBinding,
        owner: LifecycleOwner,
        adapter: SearchAdapter
    ) {
        lifecycleScope.launch {
            launch {
                adapter.loadStateFlow.collect { loadState ->
                    binding.inProgress = loadState.refresh is LoadState.Loading
                }
            }
            launch {
                viewModel.dictionary.collectLatest { data -> adapter.submitData(data) }
            }
            launch {
                viewModel.query.collectLatest { query ->
                    searchView?.setQuery(query, false)
                    adapter.submitSearchText(query)
                }
            }
            launch {
                viewModel.restriction.collectLatest { restriction ->
                    when (restriction) {
                        is SearchRestriction.None -> {
                            binding.restriction = ""
                            binding.searchRestriction.visibility = View.GONE
                        }
                        is SearchRestriction.Some -> {
                            binding.restriction = getString(
                                R.string.search_restriction,
                                restriction.category.name, restriction.restriction
                            )
                            binding.searchRestriction.visibility = View.VISIBLE
                        }
                    }.exhaustive
                }
            }
            launch {
                viewModel.currentDictionary.collectLatest { binding.empty = it == null }
            }
        }
    }

    /** Show entry in dictionary */
    override fun onClickItem(lexeme: Lexeme) {
        activityViewModel.addToHistory()
        val directions = SearchFragmentDirections.actionNavSearchToNavHome(lexeme.id)
        findNavController().navigate(directions)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        hideKeyboard()
        activityViewModel.addToHistory()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val query = newText.toString().trim()
        if (query.length >= 2) viewModel.setQuery(query)
        return true
    }

    private fun hideKeyboard() {
        val view = requireView()
        (view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
