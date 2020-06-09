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

import android.content.Context
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
import com.github.cheapmon.balalaika.Application
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentBookmarksBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

/**
 * Fragment for bookmark organisation
 *
 * The user may:
 * - See existing bookmarks
 * - Follow a bookmark to its dictionary entry
 * - Remove one or all bookmarks
 */
class BookmarksFragment : Fragment(), BookmarksAdapter.Listener {
    /** @suppress */
    @Inject
    lateinit var viewModelFactory: BookmarksViewModelFactory

    /** @suppress */
    private lateinit var viewModel: BookmarksViewModel

    private lateinit var binding: FragmentBookmarksBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookmarksLayoutManager: LinearLayoutManager
    private lateinit var bookmarksAdapter: BookmarksAdapter

    /** Prepare view and load data */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmarks, container, false)
        bookmarksLayoutManager = LinearLayoutManager(context)
        bookmarksAdapter = BookmarksAdapter(this)
        recyclerView = binding.bookmarksList.apply {
            layoutManager = bookmarksLayoutManager
            adapter = bookmarksAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, bookmarksLayoutManager.orientation))
        }
        setHasOptionsMenu(true)
        submitData()
        return binding.root
    }

    /** Inject view model */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as Application).appComponent.inject(this)
        val model by viewModels<BookmarksViewModel> { viewModelFactory }
        viewModel = model
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
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.bookmarks_clear_title)
                    .setPositiveButton(R.string.bookmarks_clear_affirm) { _, _ -> clearBookmarks() }
                    .setNegativeButton(R.string.bookmarks_clear_cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Bind data */
    private fun submitData() {
        viewModel.lexemes.observe(viewLifecycleOwner, Observer {
            bookmarksAdapter.submitList(ArrayList(it))
            if (it.isEmpty()) {
                binding.bookmarksEmptyIcon.visibility = View.VISIBLE
                binding.bookmarksEmptyText.visibility = View.VISIBLE
            } else {
                binding.bookmarksEmptyIcon.visibility = View.GONE
                binding.bookmarksEmptyText.visibility = View.GONE
            }
        })
    }

    /** Remove all bookmarks */
    private fun clearBookmarks() {
        viewModel.clearBookmarks()
        Snackbar.make(binding.root, R.string.bookmarks_clear_done, Snackbar.LENGTH_SHORT).show()
    }

    /** Remove single bookmark */
    override fun onClickDeleteButton(lexeme: Lexeme) {
        viewModel.removeBookmark(lexeme.lexemeId)
        Snackbar.make(binding.root, R.string.bookmarks_item_removed, Snackbar.LENGTH_SHORT)
            .show()
    }

    /** Show bookmarked entry in dictionary */
    override fun onClickRedoButton(lexeme: Lexeme) {
        val directions =
            BookmarksFragmentDirections.actionNavBookmarksToNavHome(lexeme.externalId)
        findNavController().navigate(directions)
    }
}
