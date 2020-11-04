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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.cheapmon.balalaika.MainViewModel
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.SearchRestriction
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
                    onQueryChange = ::addToHistory,
                    onClickEntry = ::onClickItem
                )
            }
        }
    }

    /** Show entry in dictionary */
    private fun onClickItem(
        dictionaryEntry: DictionaryEntry,
        query: String?,
        restriction: SearchRestriction?
    ) {
        addToHistory(query, restriction)
        val directions = SearchFragmentDirections.actionNavSearchToNavHome(dictionaryEntry)
        findNavController().navigate(directions)
    }

    private fun addToHistory(query: String?, restriction: SearchRestriction?) {
        if (query != null) activityViewModel.addToHistory(query, restriction)
    }
}
