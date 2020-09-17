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
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryBinding
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.model.SearchRestriction
import com.github.cheapmon.balalaika.ui.RecyclerViewFragment
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetActionListener
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetMenuListener
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
    ), DictionaryAdapter.Listener, WidgetMenuListener {
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
    override fun createRecyclerViewAdapter() = DictionaryAdapter(
        this,
        this,
        audioActionListener,
        referenceActionListener,
        urlActionListener,
        wordnetActionListener
    )

    /** @suppress */
    override fun observeData(
        binding: FragmentDictionaryBinding,
        owner: LifecycleOwner,
        adapter: DictionaryAdapter
    ) {
        lifecycleScope.launch {
            launch {
                viewModel.dictionaryEntries.collectLatest { data ->
                    adapter.submitData(data ?: PagingData.empty())
                    binding.empty = data == null
                }
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
            val names = categories?.map { it.name }?.toTypedArray()
            val selected = categories?.indexOfFirst { it == viewModel.category.first() }
            if (names != null && selected != null) {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_sort)
                    .setTitle(R.string.menu_order_by)
                    .setSingleChoiceItems(names, selected) { _, which ->
                        viewModel.setCategory(categories[which])
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
            val names = dictionaryViews?.map { it.name }?.toTypedArray()
            val selected = dictionaryViews?.indexOfFirst { it == viewModel.dictionaryView.first() }
            if (names != null && selected != null) {
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_view)
                    .setTitle(R.string.menu_setup_view)
                    .setSingleChoiceItems(names, selected) { _, which ->
                        viewModel.setDictionaryView(dictionaryViews[which])
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
        viewModel.toggleBookmark(entry)
        val message = if (isBookmark) {
            getString(R.string.dictionary_bookmark_remove, entry.representation)
        } else {
            getString(R.string.dictionary_bookmark_add, entry.representation)
        }
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    /** Go to base of a lexeme */
    override fun onClickBaseButton(entry: DictionaryEntry) {
        viewModel.setInitialEntry(entry.base)
    }

    private val audioActionListener = object : WidgetActionListener<Property.Audio> {
        /** Play audio file */
        override fun onAction(property: Property.Audio) {
            try {
                MediaPlayer.create(context, property.fileName.toUri()).apply {
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
    }

    private val referenceActionListener = object : WidgetActionListener<Property.Reference> {
        override fun onAction(property: Property.Reference) {
            viewModel.setInitialEntry(property.entry)
        }
    }

    private val urlActionListener = object : WidgetActionListener<Property.Url> {
        override fun onAction(property: Property.Url) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(property.url)))
        }
    }

    private val wordnetActionListener = object : WidgetActionListener<Property.Wordnet> {
        override fun onAction(property: Property.Wordnet) {
            WordnetDialog(
                property.name,
                viewModel.getWordnetData(property)
            ).show(parentFragmentManager, null)
        }
    }

    override fun onClickMenuItem(item: String, category: DataCategory) {
        val restriction = SearchRestriction(category, item)
        val directions = DictionaryFragmentDirections.actionNavHomeToNavSearch(restriction)
        findNavController().navigate(directions)
    }
}
