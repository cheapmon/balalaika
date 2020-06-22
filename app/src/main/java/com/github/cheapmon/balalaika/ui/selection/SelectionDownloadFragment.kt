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
package com.github.cheapmon.balalaika.ui.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSelectionListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Fragment for dictionary download
 *
 * The user may:
 * - See all available remote dictionaries
 * - Navigate to a dictionary's detail view
 * - Add a dictionary to the library
 */
@AndroidEntryPoint
class SelectionDownloadFragment : Fragment(), SelectionAdapter.Listener,
    SwipeRefreshLayout.OnRefreshListener {
    private val viewModel: SelectionViewModel by viewModels()

    private lateinit var binding: FragmentSelectionListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectionLayoutManager: LinearLayoutManager
    private lateinit var selectionAdapter: SelectionAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var job: Job? = null

    /** Create view */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selection_list, container, false)
        selectionLayoutManager = LinearLayoutManager(context)
        selectionAdapter = SelectionAdapter(this)
        swipeRefreshLayout = binding.selectionRefresh.apply {
            setOnRefreshListener(this@SelectionDownloadFragment)
        }
        recyclerView = binding.selectionList.apply {
            layoutManager = selectionLayoutManager
            adapter = selectionAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, selectionLayoutManager.orientation))
        }
        binding.selectionRetry.setOnClickListener { bindUi() }
        bindUi()
        return binding.root
    }

    /** Bind data */
    private fun bindUi(forceRefresh: Boolean = false) {
        swipeRefreshLayout.isRefreshing = true
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.getRemoteDictionaries(forceRefresh)
                .observe(viewLifecycleOwner, Observer { response ->
                    binding.pending = response is DictionaryService.RemoteResponse.Pending
                    binding.empty = response is DictionaryService.RemoteResponse.Failed
                    when (response) {
                        is DictionaryService.RemoteResponse.Failed -> {
                            swipeRefreshLayout.isRefreshing = false
                            selectionAdapter.submitList(listOf())
                            Toast.makeText(
                                context,
                                context?.getString(R.string.selection_loading_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is DictionaryService.RemoteResponse.Success -> {
                            swipeRefreshLayout.isRefreshing = false
                            selectionAdapter.submitList(ArrayList(response.dictionaries))
                            binding.empty = response.dictionaries.isEmpty()
                            Toast.makeText(
                                context,
                                context?.getString(
                                    R.string.selection_loading_success,
                                    response.dictionaries.count()
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }
    }

    /** Show dictionary in detail view */
    override fun onClickDictionary(dictionary: Dictionary) {
        val directions =
            SelectionFragmentDirections.actionNavSelectionToNavSelectionDetail(dictionary, true)
        findNavController().navigate(directions)
    }

    /** Refresh dictionary list from remote */
    override fun onRefresh() {
        bindUi(forceRefresh = true)
    }
}
