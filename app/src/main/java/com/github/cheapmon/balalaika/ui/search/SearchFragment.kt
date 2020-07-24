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

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
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
    ), SearchAdapter.Listener {

    override fun onCreateBinding(binding: FragmentSearchBinding) {
        super.onCreateBinding(binding)
        binding.searchInput.addTextChangedListener(Watcher())
        binding.searchRestriction.setOnCloseIconClickListener {
            viewModel.setRestriction(SearchRestriction.None)
        }
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
                    binding.query = query
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
        viewModel.addToHistory()
        val directions = SearchFragmentDirections.actionNavSearchToNavHome(lexeme.id)
        findNavController().navigate(directions)
    }

    /** @suppress */
    inner class Watcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val query = s.toString().trim()
            if (query.length >= 2) viewModel.setQuery(query)
        }
    }
}
