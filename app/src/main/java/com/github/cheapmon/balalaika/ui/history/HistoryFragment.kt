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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentHistoryBinding
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.ui.RecyclerViewFragment
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
class HistoryFragment :
    RecyclerViewFragment<HistoryViewModel, FragmentHistoryBinding, HistoryAdapter>(
        HistoryViewModel::class,
        R.layout.fragment_history,
        true
    ), HistoryAdapter.Listener {
    /** Notify about options menu */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /** @suppress */
    override fun createRecyclerView(binding: FragmentHistoryBinding) =
        binding.historyList

    /** @suppress */
    override fun createRecyclerViewAdapter() =
        HistoryAdapter(this)

    /** @suppress */
    override fun observeData(
        binding: FragmentHistoryBinding,
        owner: LifecycleOwner,
        adapter: HistoryAdapter
    ) {
        binding.lifecycleOwner = owner
        binding.viewModel = viewModel
        viewModel.items.observe(owner) {
            adapter.submitList(it)
        }
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
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.history_clear_title)
                    .setPositiveButton(R.string.history_clear_affirm) { _, _ -> clearHistory() }
                    .setNegativeButton(R.string.history_clear_cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Remove all history entries */
    private fun clearHistory() {
        viewModel.clearHistory()
        Snackbar.make(requireView(), R.string.history_clear_done, Snackbar.LENGTH_SHORT).show()
    }

    /** Remove single history entry */
    override fun onClickDeleteButton(historyItem: HistoryItem) {
        viewModel.removeItem(historyItem)
        Snackbar.make(requireView(), R.string.history_entry_removed, Snackbar.LENGTH_SHORT)
            .show()
    }

    /** Repeat search operation */
    override fun onClickRedoButton(historyItem: HistoryItem) {
        val directions = HistoryFragmentDirections.actionNavHistoryToNavSearch(
            query = historyItem.query,
            restriction = historyItem.restriction
        )
        findNavController().navigate(directions)
    }
}
