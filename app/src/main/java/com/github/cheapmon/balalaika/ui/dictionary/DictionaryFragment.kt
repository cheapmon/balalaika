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
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.entry.GroupedEntry
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.storage.Storage
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryBinding
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.util.grouped
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

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
@AndroidEntryPoint
class DictionaryFragment : Fragment(), DictionaryAdapter.Listener, WidgetListener {
    private val viewModel: DictionaryViewModel by viewModels()

    /** @suppress */
    @Inject
    lateinit var storage: Storage

    private val args: DictionaryFragmentArgs by navArgs()

    private lateinit var binding: FragmentDictionaryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dictionaryLayoutManager: LinearLayoutManager
    private lateinit var dictionaryAdapter: DictionaryAdapter

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
            inProgress = true
        }
        bindUi()
        handleArgs()
        return binding.root
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
        val orderKey = getString(R.string.preferences_key_order)
        val viewKey = getString(R.string.preferences_key_view)
        return when (item.itemId) {
            R.id.action_order_by -> {
                lifecycleScope.launch {
                    val categories = viewModel.getCategories()
                    val names = categories.map { it.name }.toTypedArray()
                    val ids = categories.map { it.categoryId.toString() }
                    val selected = ids.indexOfFirst {
                        it == storage.getString(orderKey, null)
                    }
                    MaterialAlertDialogBuilder(requireContext())
                        .setIcon(R.drawable.ic_sort)
                        .setTitle(R.string.menu_order_by)
                        .setSingleChoiceItems(names, selected) { _, which ->
                            val id = categories[which].categoryId
                            storage.putString(orderKey, id.toString())
                            viewModel.setCategory(id)
                        }.setPositiveButton(R.string.affirm, null)
                        .show()
                }
                true
            }
            R.id.action_setup_view -> {
                lifecycleScope.launch {
                    val dictionaryViews = viewModel.getDictionaryViews()
                    val names = dictionaryViews.map { it.dictionaryView.name }.toTypedArray()
                    val ids = dictionaryViews.map { it.dictionaryView.dictionaryViewId.toString() }
                    val selected = ids.indexOfFirst {
                        it == storage.getString(viewKey, null)
                    }
                    MaterialAlertDialogBuilder(requireContext())
                        .setIcon(R.drawable.ic_view)
                        .setTitle(R.string.menu_setup_view)
                        .setSingleChoiceItems(names, selected) { _, which ->
                            val id = dictionaryViews[which].dictionaryView.dictionaryViewId
                            storage.putString(viewKey, id.toString())
                            viewModel.setDictionaryView(id)
                            dictionaryAdapter.notifyDataSetChanged()
                        }.setPositiveButton(R.string.affirm, null)
                        .show()
                }
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

    /** Bind data */
    private fun bindUi() {
        viewModel.lexemes.observe(viewLifecycleOwner, Observer {
            dictionaryAdapter.submitList(it)
        })
        viewModel.inProgress.observe(viewLifecycleOwner, Observer {
            binding.inProgress = it
        })
    }

    /** Handle fragment arguments */
    private fun handleArgs() {
        val externalId = args.externalId
        if (externalId != null) {
            scrollTo(externalId)
        }
    }

    /** Scroll to a dictionary entry */
    private fun scrollTo(externalId: String) {
        lifecycleScope.launch {
            val position = viewModel.getPositionOf(externalId)
            delay(200)
            recyclerView.post {
                dictionaryLayoutManager.scrollToPositionWithOffset(position, 0)
            }
        }
    }

    /** Add or remove an entry to bookmarks */
    override fun onClickBookmarkButton(entry: GroupedEntry) {
        viewModel.toggleBookmark(entry.lexeme.lexemeId)
        val message = if (entry.lexeme.isBookmark) {
            getString(R.string.dictionary_bookmark_remove, entry.lexeme.form)
        } else {
            getString(R.string.dictionary_bookmark_add, entry.lexeme.form)
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    /** Go to base of a lexeme */
    override fun onClickBaseButton(entry: GroupedEntry) {
        if (entry.base != null) scrollTo(entry.base.externalId)
    }

    /** Load dictionary entries for a lexeme */
    override fun onLoadLexeme(lexeme: Lexeme, block: (entry: GroupedEntry?) -> Unit) {
        lifecycleScope.launch {
            val entry = viewModel.getDictionaryEntriesFor(lexeme.lexemeId).grouped()
            block(entry)
        }
    }

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
    override fun onClickScrollButton(externalId: String) {
        scrollTo(externalId)
    }

    /** Open link in browser */
    override fun onClickLinkButton(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }
}
