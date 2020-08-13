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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.cheapmon.balalaika.MainViewModel
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.selection.DictionaryInfo
import com.github.cheapmon.balalaika.databinding.FragmentSelectionListBinding
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.ui.RecyclerViewFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** Fragment for displaying available dictionaries for multiple sources */
@AndroidEntryPoint
class SelectionListFragment(
    private val data: Flow<List<DictionaryInfo>>
) : RecyclerViewFragment<SelectionViewModel, FragmentSelectionListBinding, SelectionAdapter>(
    SelectionViewModel::class,
    R.layout.fragment_selection_list,
    false
), SelectionAdapter.Listener {
    private val activityViewModel: MainViewModel by viewModels()

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    /** Notify about options menu */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /** @suppress */
    override fun onCreateBinding(binding: FragmentSelectionListBinding) {
        super.onCreateBinding(binding)
        swipeRefreshLayout = binding.selectionRefresh.apply {
            setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                viewModel.refresh()
            }
        }
    }

    /** @suppress */
    override fun createRecyclerView(binding: FragmentSelectionListBinding) =
        binding.selectionList

    /** @suppress */
    override fun createRecyclerViewAdapter() =
        SelectionAdapter(this)

    /** @suppress */
    override fun observeData(
        binding: FragmentSelectionListBinding,
        owner: LifecycleOwner,
        adapter: SelectionAdapter
    ) {
        lifecycleScope.launch {
            data.collect { list ->
                swipeRefreshLayout.isRefreshing = false
                adapter.submitList(list.map { it.toDictionary() })
                binding.isEmpty = list.isEmpty()
            }
        }
    }

    /** Show dictionary in detail view */
    override fun onClickDictionary(dictionary: Dictionary) {
        val directions = SelectionFragmentDirections.actionNavSelectionToNavSelectionDetail(
            dictionary.id
        )
        findNavController().navigate(directions)
    }

    /** Activate a dictionary */
    override fun onClickActivate(dictionary: Dictionary) {
        activityViewModel.activate(dictionary)
    }
}
