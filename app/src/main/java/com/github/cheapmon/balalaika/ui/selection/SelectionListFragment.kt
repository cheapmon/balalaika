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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSelectionListBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for dictionary selection
 *
 * The user may:
 * - See all available local dictionaries
 * - Navigate to a dictionary's detail view
 * - Set dictionary active or inactive
 */
@AndroidEntryPoint
class SelectionListFragment : Fragment(), SelectionAdapter.Listener {
    private val viewModel: SelectionViewModel by viewModels()

    private lateinit var binding: FragmentSelectionListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectionLayoutManager: LinearLayoutManager
    private lateinit var selectionAdapter: SelectionAdapter

    /** Create view */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selection_list, container, false)
        selectionLayoutManager = LinearLayoutManager(context)
        selectionAdapter = SelectionAdapter(this)
        binding.selectionRefresh.isEnabled = false
        recyclerView = binding.selectionList.apply {
            layoutManager = selectionLayoutManager
            adapter = selectionAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, selectionLayoutManager.orientation))
        }
        setHasOptionsMenu(true)
        submitData()
        return binding.root
    }

    /** Bind data */
    private fun submitData() {
        viewModel.dictionaries.observe(viewLifecycleOwner, Observer {
            selectionAdapter.submitList(ArrayList(it))
            binding.empty = it.isEmpty()
        })
    }

    /** Show dictionary in detail view */
    override fun onClickDictionary(dictionary: Dictionary) {
        val directions =
            SelectionFragmentDirections.actionNavSelectionToNavSelectionDetail(dictionary, false)
        findNavController().navigate(directions)
    }
}
