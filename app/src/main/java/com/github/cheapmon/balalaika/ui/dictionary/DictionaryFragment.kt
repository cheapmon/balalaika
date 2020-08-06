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
package com.github.cheapmon.balalaika.ui.dictionary

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryBinding
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.ui.RecyclerViewFragment
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Fragment for dictionary usage
 *
 * The user may:
 * - See lexemes and their properties
 * - Reorder dictionary entries
 * - Choose a dictionary view
 * - Go to the base of a lexeme
 * - Bookmark an entry
 * - Collapse an entry
 * - See advanced actions on an entry
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DictionaryFragment :
    RecyclerViewFragment<DictionaryViewModel, FragmentDictionaryBinding, DictionaryAdapter>(
        DictionaryViewModel::class,
        R.layout.fragment_dictionary,
        false
    ), DictionaryAdapter.Listener, WidgetListener {
    /** Notify about options menu */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /** @suppress */
    override fun onCreateBinding(binding: FragmentDictionaryBinding) {
        super.onCreateBinding(binding)
        binding.dictionaryEmptyButton.setOnClickListener {
            val directions = DictionaryFragmentDirections.selectDictionary()
            findNavController().navigate(directions)
        }
    }

    /** @suppress */
    override fun createRecyclerView(binding: FragmentDictionaryBinding) =
        binding.entryList

    /** @suppress */
    override fun createRecyclerViewAdapter() =
        DictionaryAdapter(this, this)

    /** @suppress */
    override fun observeData(
        binding: FragmentDictionaryBinding,
        owner: LifecycleOwner,
        adapter: DictionaryAdapter
    ) {
        lifecycleScope.launch {
            launch {
                viewModel.dictionary.collectLatest { data -> adapter.submitData(data) }
            }
            launch {
                viewModel.currentDictionary.collectLatest { binding.empty = it == null }
            }
            launch {
                adapter.loadStateFlow.collect { loadState ->
                    binding.inProgress = loadState.refresh is LoadState.Loading
                }
            }
        }
    }

    /** Create options menu */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dictionary, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Options menu actions
     *
     * - Change dictionary ordering
     * - Change dictionary view
     * - Navigate to search
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_order_by -> {
                showOrderingDialog()
                true
            }
            R.id.action_setup_view -> {
                showDictionaryViewDialog()
                true
            }
            R.id.action_search -> {
                val directions = DictionaryFragmentDirections.actionNavHomeToNavSearch()
                findNavController().navigate(directions)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showOrderingDialog() {
        lifecycleScope.launch {
            val categories = viewModel.getCategories()
            val names = categories.map { it.name }.toTypedArray()
            val selected = categories.indexOfFirst { it.id == viewModel.category.first() }
            val dictionary = viewModel.currentDictionary.first()
            if (dictionary != null) {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_sort)
                    .setTitle(R.string.menu_order_by)
                    .setSingleChoiceItems(names, selected) { _, which ->
                        val id = categories[which].id
                        viewModel.setCategory(id)
                    }.setPositiveButton(R.string.affirm, null)
                    .show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_sort)
                    .setTitle(R.string.menu_order_by)
                    .setMessage(R.string.dictionary_empty)
                    .setPositiveButton(R.string.affirm, null)
                    .show()
            }
        }
    }

    private fun showDictionaryViewDialog() {
        lifecycleScope.launch {
            val dictionaryViews = viewModel.getDictionaryViews()
            val names = dictionaryViews.map { it.dictionaryView.name }.toTypedArray()
            val selected = dictionaryViews.indexOfFirst {
                it.dictionaryView.id == viewModel.dictionaryView.first()
            }
            val dictionary = viewModel.currentDictionary.first()
            if (dictionary != null) {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_view)
                    .setTitle(R.string.menu_setup_view)
                    .setSingleChoiceItems(names, selected) { _, which ->
                        val id = dictionaryViews[which].dictionaryView.id
                        viewModel.setDictionaryView(id)
                    }.setPositiveButton(R.string.affirm, null)
                    .show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_view)
                    .setTitle(R.string.menu_setup_view)
                    .setMessage(R.string.dictionary_empty)
                    .setPositiveButton(R.string.affirm, null)
                    .show()
            }
        }
    }

    /** Add or remove an entry to bookmarks */
    override fun onClickBookmarkButton(entry: DictionaryEntry, isBookmark: Boolean) {
        viewModel.toggleBookmark(entry.lexeme.id)
        val message = if (isBookmark) {
            getString(R.string.dictionary_bookmark_remove, entry.lexeme.form)
        } else {
            getString(R.string.dictionary_bookmark_add, entry.lexeme.form)
        }
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    /** Go to base of a lexeme */
    override fun onClickBaseButton(entry: DictionaryEntry) =
        viewModel.setInitialKey(entry.base?.id)

    /** Play audio file */
    override fun onClickAudioButton(resId: Int) {
        try {
            MediaPlayer.create(context, resId).apply {
                start()
                setOnCompletionListener { release() }
            }
        } catch (ex: Exception) {
            Snackbar.make(
                requireView(),
                R.string.dictionary_playback_failed,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    /** Navigate to search */
    override fun onClickSearchButton(query: String, restriction: SearchRestriction) {
        val directions =
            DictionaryFragmentDirections.actionNavHomeToNavSearch(restriction, query)
        findNavController().navigate(directions)
    }

    /** Scroll to a dictionary entry */
    override fun onClickScrollButton(id: String) = viewModel.setInitialKey(id)

    /** Open link in browser */
    override fun onClickLinkButton(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    /** Open Wordnet dialog */
    override fun onClickWordnetButton(word: String, url: String) {
        WordnetDialog(word, viewModel.getWordnetData(url)).show(parentFragmentManager, null)
    }
}
