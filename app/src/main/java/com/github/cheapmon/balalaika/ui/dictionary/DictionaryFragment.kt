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
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.model.SearchRestriction
import com.github.cheapmon.balalaika.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
class DictionaryFragment : Fragment() {
    private val viewModel: DictionaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                DictionaryEntryScreen(
                    viewModel = viewModel,
                    navController = findNavController(),
                    onClickBase = ::onClickBaseButton,
                    onBookmark = ::onClickBookmarkButton,
                    onClickProperty = ::onClickProperty,
                    onOpenDictionaries = ::onOpenDictionaries
                )
            }
        }
    }

    /*private fun showOrderingDialog() {
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
    }*/

    /*private fun showDictionaryViewDialog() {
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
    }*/

    /** Open dictionary screen */
    private fun onOpenDictionaries() {
        val directions = DictionaryFragmentDirections.selectDictionary()
        findNavController().navigate(directions)
    }

    /** Add or remove an entry to bookmarks */
    private fun onClickBookmarkButton(entry: DictionaryEntry) {
        viewModel.toggleBookmark(entry)
        viewModel.refresh()
        val message = if (entry.bookmark != null) {
            getString(R.string.dictionary_bookmark_remove, entry.representation)
        } else {
            getString(R.string.dictionary_bookmark_add, entry.representation)
        }
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    /** Go to base of a lexeme */
    private fun onClickBaseButton(entry: DictionaryEntry) {
        viewModel.setInitialEntry(entry.base)
    }

    /** Actions when a property is clicked */
    private fun onClickProperty(category: DataCategory, property: Property, text: String) {
        when (property) {
            is Property.Audio -> audioActionListener.onAction(property)
            is Property.Example -> {
            }
            is Property.Morphology -> searchWithRestriction(text, category)
            is Property.Plain -> searchWithRestriction(text, category)
            is Property.Reference -> referenceActionListener.onAction(property)
            is Property.Simple -> searchWithRestriction(text, category)
            is Property.Url -> urlActionListener.onAction(property)
            is Property.Wordnet -> wordnetActionListener.onAction(property)
        }.exhaustive
    }

    private interface WidgetActionListener<T : Property> {
        fun onAction(property: T)
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
            viewModel.setWordnetParam(property)
        }
    }

    private fun searchWithRestriction(item: String, category: DataCategory) {
        val restriction = SearchRestriction(category, item)
        val directions = DictionaryFragmentDirections.actionNavHomeToNavSearch(restriction)
        findNavController().navigate(directions)
    }
}
