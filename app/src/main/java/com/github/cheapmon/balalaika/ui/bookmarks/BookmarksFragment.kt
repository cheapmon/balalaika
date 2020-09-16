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
package com.github.cheapmon.balalaika.ui.bookmarks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentBookmarksBinding
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.ui.RecyclerViewFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for bookmark organisation
 *
 * The user may:
 * - See existing bookmarks
 * - Follow a bookmark to its dictionary entry
 * - Remove one or all bookmarks
 */
@AndroidEntryPoint
class BookmarksFragment :
    RecyclerViewFragment<BookmarksViewModel, FragmentBookmarksBinding, BookmarksAdapter>(
        BookmarksViewModel::class,
        R.layout.fragment_bookmarks,
        true
    ), BookmarksAdapter.Listener {
    /** Notify about options menu */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /** @suppress */
    override fun createRecyclerView(binding: FragmentBookmarksBinding) =
        binding.bookmarksList

    /** @suppress */
    override fun createRecyclerViewAdapter() =
        BookmarksAdapter(this)

    /** @suppress */
    override fun observeData(
        binding: FragmentBookmarksBinding,
        owner: LifecycleOwner,
        adapter: BookmarksAdapter
    ) {
        binding.lifecycleOwner = owner
        binding.viewModel = viewModel
        viewModel.bookmarkedEntries.observe(owner) {
            adapter.submitList(it)
        }
    }

    /**
     * Create options menu
     *
     * This only consists of one button to remove all bookmarks.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bookmarks, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /** Options menu actions */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bookmarks_clear -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.bookmarks_clear_title)
                    .setPositiveButton(R.string.bookmarks_clear_affirm) { _, _ -> clearBookmarks() }
                    .setNegativeButton(R.string.bookmarks_clear_cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Remove all bookmarks */
    private fun clearBookmarks() {
        viewModel.clearBookmarks()
        Snackbar.make(requireView(), R.string.bookmarks_clear_done, Snackbar.LENGTH_SHORT).show()
    }

    /** Remove single bookmark */
    override fun onClickDeleteButton(dictionaryEntry: DictionaryEntry) {
        viewModel.removeBookmark(dictionaryEntry)
        Snackbar.make(requireView(), R.string.bookmarks_item_removed, Snackbar.LENGTH_SHORT)
            .show()
    }

    /** Show bookmarked entry in dictionary */
    override fun onClickRedoButton(dictionaryEntry: DictionaryEntry) {
        val directions =
            BookmarksFragmentDirections.actionNavBookmarksToNavHome(dictionaryEntry.id)
        findNavController().navigate(directions)
    }
}
