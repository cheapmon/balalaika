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
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
class BookmarksFragment : Fragment() {
    private val viewModel: BookmarksViewModel by viewModels()

    /** Notify about options menu */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                BookmarksScreen(viewModel, ::onClickEntry)
            }
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
                    .setPositiveButton(R.string.bookmarks_clear_affirm) { _, _ -> viewModel.clearBookmarks() }
                    .setNegativeButton(R.string.bookmarks_clear_cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onClickEntry(entry: DictionaryEntry) {
        val directions = BookmarksFragmentDirections.actionNavBookmarksToNavHome(entry)
        findNavController().navigate(directions)
    }
}
