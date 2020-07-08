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
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.core.storage.Storage
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryBinding
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.util.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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
class DictionaryFragment : Fragment(), DictionaryAdapter.Listener, WidgetListener {
    private val viewModel: DictionaryViewModel by viewModels()

    /** @suppress */
    @Inject
    lateinit var storage: Storage

    /** @suppress */
    @Inject
    lateinit var constants: Constants

    private val args: DictionaryFragmentArgs by navArgs()

    private lateinit var binding: FragmentDictionaryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dictionaryLayoutManager: LinearLayoutManager
    private lateinit var dictionaryAdapter: DictionaryAdapter

    /** Set default values for the dictionary view and the dictionary ordering */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryId = storage.getString(constants.ORDER_KEY, null)
            ?: constants.DEFAULT_CATEGORY_ID
        storage.putString(constants.ORDER_KEY, categoryId)
        viewModel.setCategory(categoryId)
        val dictionaryViewId = storage.getString(constants.VIEW_KEY, null)?.toLong()
            ?: constants.DEFAULT_DICTIONARY_VIEW_ID
        storage.putString(constants.VIEW_KEY, dictionaryViewId.toString())
        viewModel.setDictionaryView(dictionaryViewId)
    }

    /** Prepare view and load data */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dictionary, container, false)
        dictionaryLayoutManager = LinearLayoutManager(context)
        dictionaryAdapter = DictionaryAdapter(this, this)
        with(binding) {
            recyclerView = entryList.apply {
                layoutManager = dictionaryLayoutManager
                adapter = dictionaryAdapter
                setHasFixedSize(true)
            }
        }
        bindUi()
        indicateProgress()
        scrollTo(args.externalId)
        return binding.root
    }

    private fun bindUi() {
        lifecycleScope.launch {
            viewModel.dictionary.collectLatest { data -> dictionaryAdapter.submitData(data) }
        }
    }

    private fun indicateProgress() {
        lifecycleScope.launch {
            dictionaryAdapter.loadStateFlow.collect { loadState ->
                binding.inProgress = loadState.refresh is LoadState.Loading
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
            val ids = categories.map { it.id }
            val selected = ids.indexOfFirst {
                it == storage.getString(constants.ORDER_KEY, null)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_sort)
                .setTitle(R.string.menu_order_by)
                .setSingleChoiceItems(names, selected) { _, which ->
                    val id = categories[which].id
                    storage.putString(constants.ORDER_KEY, id)
                    viewModel.setCategory(id)
                }.setPositiveButton(R.string.affirm, null)
                .show()
        }
    }

    private fun showDictionaryViewDialog() {
        lifecycleScope.launch {
            val dictionaryViews = viewModel.getDictionaryViews()
            val names = dictionaryViews.map { it.dictionaryView.name }.toTypedArray()
            val ids = dictionaryViews.map { it.dictionaryView.dictionaryViewId.toString() }
            val selected = ids.indexOfFirst {
                it == storage.getString(constants.VIEW_KEY, null)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_view)
                .setTitle(R.string.menu_setup_view)
                .setSingleChoiceItems(names, selected) { _, which ->
                    val id = dictionaryViews[which].dictionaryView.dictionaryViewId
                    storage.putString(constants.VIEW_KEY, id.toString())
                    viewModel.setDictionaryView(id)
                }.setPositiveButton(R.string.affirm, null)
                .show()
        }
    }

    /** Scroll to a dictionary entry */
    private fun scrollTo(externalId: String?) {
        lifecycleScope.launch {
            val initialKey = viewModel.getIdOf(externalId)
            viewModel.setInitialKey(initialKey)
        }
    }

    /** Add or remove an entry to bookmarks */
    override fun onClickBookmarkButton(entry: DictionaryEntry, isBookmark: Boolean) {
        viewModel.toggleBookmark(entry.lexemeWithBase.lexeme.lexemeId)
        val message = if (isBookmark) {
            getString(R.string.dictionary_bookmark_remove, entry.lexemeWithBase.lexeme.form)
        } else {
            getString(R.string.dictionary_bookmark_add, entry.lexemeWithBase.lexeme.form)
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    /** Go to base of a lexeme */
    override fun onClickBaseButton(entry: DictionaryEntry) =
        scrollTo(entry.lexemeWithBase.base?.externalId)

    /** Play audio file */
    override fun onClickAudioButton(resId: Int) {
        try {
            MediaPlayer.create(context, resId).apply {
                start()
                setOnCompletionListener { release() }
            }
        } catch (ex: Exception) {
            Snackbar.make(
                binding.root,
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
    override fun onClickScrollButton(externalId: String) = scrollTo(externalId)

    /** Open link in browser */
    override fun onClickLinkButton(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }
}
