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
package com.github.cheapmon.balalaika.ui.search

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.cheapmon.balalaika.MainViewModel
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.model.SearchRestriction
import com.github.cheapmon.balalaika.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for querying the database
 *
 * The user can:
 * - Search for dictionary entries using a query
 * - Apply restriction to the search query
 * - Show an entry in the dictionary
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(
                    navController = findNavController(),
                    viewModel = viewModel,
                    onQuerySubmit = ::addToHistory,
                    onClickBase = ::onClickBaseButton,
                    onBookmark = ::onBookmark,
                    onClickProperty = ::onClickProperty
                )
            }
        }
    }

    private fun addToHistory(query: String?, restriction: SearchRestriction?) {
        if (query != null) activityViewModel.addToHistory(query, restriction)
    }

    /** Go to base of a lexeme */
    private fun onClickBaseButton(entry: DictionaryEntry) {
        search(entry.representation)
    }

    private fun onBookmark(entry: DictionaryEntry) {
        viewModel.toggleBookmark(entry)
        viewModel.refresh()
        val message = if (entry.bookmark != null) {
            getString(R.string.dictionary_bookmark_remove, entry.representation)
        } else {
            getString(R.string.dictionary_bookmark_add, entry.representation)
        }
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    /** Actions when a property is clicked */
    private fun onClickProperty(category: DataCategory, property: Property, text: String) {
        when (property) {
            is Property.Audio -> onAudio(property)
            is Property.Example -> {
            }
            is Property.Morphology -> searchWithRestriction(text, category)
            is Property.Plain -> searchWithRestriction(text, category)
            is Property.Reference -> onReference(property)
            is Property.Simple -> searchWithRestriction(text, category)
            is Property.Url -> onUrl(property)
            is Property.Wordnet -> onWordnet(property)
        }.exhaustive
    }

    /** Play audio file */
    private fun onAudio(property: Property.Audio) {
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

    private fun onReference(property: Property.Reference) {
        search(property.entry.representation)
    }

    private fun onUrl(property: Property.Url) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(property.url)))
    }

    private fun onWordnet(property: Property.Wordnet) {
        viewModel.setWordnetParam(property)
    }

    private fun search(item: String) {
        val directions = SearchFragmentDirections.searchToSearch(query = item)
        findNavController().navigate(directions)
    }

    private fun searchWithRestriction(item: String, category: DataCategory) {
        val restriction = SearchRestriction(category, item)
        val directions = SearchFragmentDirections.searchToSearch(restriction)
        findNavController().navigate(directions)
    }
}
