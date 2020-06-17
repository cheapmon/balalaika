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

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
class SearchFragment : Fragment(), SearchAdapter.Listener {
    private val viewModel: SearchViewModel by viewModels()

    private val args: SearchFragmentArgs by navArgs()

    private lateinit var binding: FragmentSearchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter

    /** Prepare view and load data */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        searchAdapter = SearchAdapter(this)
        with(binding) {
            recyclerView = searchList.apply {
                layoutManager = LinearLayoutManager(this@SearchFragment.context)
                adapter = searchAdapter
                setHasFixedSize(true)
            }
            searchInput.addTextChangedListener(Watcher())
            searchRestriction.setOnCloseIconClickListener {
                viewModel.setRestriction(SearchRestriction.None)
            }
        }
        indicateProgress()
        handleArgs()
        bindUi()
        return binding.root
    }

    private fun indicateProgress() {
        lifecycleScope.launch {
            searchAdapter.loadStateFlow.combine(viewModel.importFlow) { loadState, done ->
                (loadState.refresh is LoadState.Loading) || !done
            }.collect { inProgress -> binding.inProgress = inProgress }
        }
    }

    private fun bindUi() {
        lifecycleScope.launch {
            launch {
                viewModel.dictionary.collectLatest { data -> searchAdapter.submitData(data) }
            }
            launch {
                viewModel.query.collectLatest { query ->
                    binding.query = query
                    searchAdapter.submitSearchText(query)
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
                    }
                }
            }
        }
        /*if (itemCount == 0) {
            binding.searchEmptyIcon.visibility = View.VISIBLE
            binding.searchEmptyText.visibility = View.VISIBLE
        } else {
            binding.searchEmptyIcon.visibility = View.GONE
            binding.searchEmptyText.visibility = View.GONE
        }*/
    }

    /** Process fragment arguments */
    private fun handleArgs() {
        args.query?.let { viewModel.setQuery(it) }
        args.restriction?.let { viewModel.setRestriction(it) }
    }


    /** Show entry in dictionary */
    override fun onClickItem(lexeme: Lexeme) {
        viewModel.addToHistory()
        val directions = SearchFragmentDirections.actionNavSearchToNavHome(lexeme.externalId)
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