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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.Application
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.entry.GroupedEntry
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentSearchBinding
import com.github.cheapmon.balalaika.util.grouped
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFragment : Fragment(), SearchAdapter.Listener {
    @Inject
    lateinit var viewModelFactory: SearchViewModelFactory
    lateinit var viewModel: SearchViewModel

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
        searchAdapter = SearchAdapter(this)
        with(binding) {
            recyclerView = searchList.apply {
                layoutManager = LinearLayoutManager(this@SearchFragment.context)
                adapter = searchAdapter
                setHasFixedSize(true)
            }
            searchInput.addTextChangedListener(Watcher())
            searchRestriction.setOnCloseIconClickListener {
                viewModel.clearRestriction()
                searchRestriction.visibility = View.GONE
            }
            inProgress = true
        }
        handleArgs()
        bindUi()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as Application).appComponent.inject(this)
        val model by viewModels<SearchViewModel> { viewModelFactory }
        viewModel = model
    }

    private fun handleArgs() {
        val query = args.query
        val restriction = args.restriction
        if (query != null) {
            viewModel.setQuery(query)
        }
        if (restriction != null) viewModel.setRestriction(restriction)
    }

    private fun bindUi() {
        viewModel.entries.observe(viewLifecycleOwner, Observer { list ->
            searchAdapter.submitList(list)
            if (list.isEmpty()) {
                binding.searchEmptyIcon.visibility = View.VISIBLE
                binding.searchEmptyText.visibility = View.VISIBLE
            } else {
                binding.searchEmptyIcon.visibility = View.GONE
                binding.searchEmptyText.visibility = View.GONE
            }
        })
        viewModel.query.observe(viewLifecycleOwner, Observer {
            searchAdapter.submitSearchText(it)
            binding.query = it
        })
        viewModel.restriction.observe(viewLifecycleOwner, Observer {
            when (it) {
                is SearchRestriction.None -> {
                    binding.restriction = ""
                    binding.searchRestriction.visibility = View.GONE
                }
                is SearchRestriction.Some -> {
                    binding.restriction = getString(
                        R.string.search_restriction,
                        it.category.name, it.restriction
                    )
                    binding.searchRestriction.visibility = View.VISIBLE
                }
            }
        })
        viewModel.inProgress.observe(viewLifecycleOwner, Observer {
            binding.inProgress = it
        })
    }

    override fun onClickItem(lexeme: Lexeme) {
        viewModel.addToHistory()
        val directions = SearchFragmentDirections.actionNavSearchToNavHome(lexeme.externalId)
        findNavController().navigate(directions)
    }

    override fun onLoadLexeme(lexeme: Lexeme, block: (entry: GroupedEntry?) -> Unit) {
        lifecycleScope.launch {
            val entry = viewModel.getDictionaryEntriesFor(lexeme.lexemeId).grouped()
            block(entry)
        }
    }

    inner class Watcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString().length >= 2) viewModel.setQuery(s.toString().trim())
        }
    }
}