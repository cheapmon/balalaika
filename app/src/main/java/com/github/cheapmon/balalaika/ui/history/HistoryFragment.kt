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
package com.github.cheapmon.balalaika.ui.history

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.databinding.FragmentHistoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for search history organisation
 *
 * The user may:
 * - See the full history
 * - Repeat a search entry
 * - Remove one or all entries
 */
@AndroidEntryPoint
class HistoryFragment : Fragment(), HistoryAdapter.Listener {
    /** @suppress */
    private val viewModel: HistoryViewModel by viewModels()

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyLayoutManager: LinearLayoutManager
    private lateinit var historyAdapter: HistoryAdapter

    /** Prepare view and load data */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        historyLayoutManager = LinearLayoutManager(context)
        historyAdapter = HistoryAdapter(this)
        recyclerView = binding.historyList.apply {
            layoutManager = historyLayoutManager
            adapter = historyAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, historyLayoutManager.orientation))
        }
        setHasOptionsMenu(true)
        submitData()
        return binding.root
    }

    /**
     * Create options menu
     *
     * Consists one one button to remove all entries.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /** Options menu actions */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history_clear -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.history_clear_title)
                    .setPositiveButton(R.string.history_clear_affirm) { _, _ -> clearHistory() }
                    .setNegativeButton(R.string.history_clear_cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Bind data */
    private fun submitData() {
        viewModel.historyEntries.observe(viewLifecycleOwner, Observer {
            historyAdapter.submitList(ArrayList(it))
            if (it.isEmpty()) {
                binding.historyEmptyIcon.visibility = View.VISIBLE
                binding.historyEmptyText.visibility = View.VISIBLE
            } else {
                binding.historyEmptyIcon.visibility = View.GONE
                binding.historyEmptyText.visibility = View.GONE
            }
        })
    }

    /** Remove all history entries */
    private fun clearHistory() {
        viewModel.clearHistory()
        Snackbar.make(binding.root, R.string.history_clear_done, Snackbar.LENGTH_SHORT).show()
    }

    /** Remove single history entry */
    override fun onClickDeleteButton(historyEntry: HistoryEntryWithRestriction) {
        viewModel.removeEntry(historyEntry.historyEntry)
        Snackbar.make(binding.root, R.string.history_entry_removed, Snackbar.LENGTH_SHORT)
            .show()
    }

    /** Repeat search operation */
    override fun onClickRedoButton(historyEntry: HistoryEntryWithRestriction) {
        val directions = HistoryFragmentDirections.actionNavHistoryToNavSearch(
            query = historyEntry.historyEntry.query,
            restriction = historyEntry.restriction
        )
        findNavController().navigate(directions)
    }
}
