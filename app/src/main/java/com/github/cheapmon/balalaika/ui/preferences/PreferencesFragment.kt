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
package com.github.cheapmon.balalaika.ui.preferences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.cheapmon.balalaika.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/** Fragment for configuring preferences */
@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat() {
    private val viewModel: PreferencesViewModel by viewModels()

    /** Load data and add callbacks */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<ListPreference>(getString(R.string.preferences_key_order))?.apply {
            entries = arrayOf()
            entryValues = arrayOf()
            lifecycleScope.launch {
                val categories = viewModel.getCategories()
                entries = categories.map { it.name }.toTypedArray()
                entryValues = categories.map { it.id }.toTypedArray()
            }
            setOnPreferenceChangeListener { _, value ->
                viewModel.setCategory((value as String).toLong())
                true
            }
        }
        findPreference<ListPreference>(getString(R.string.preferences_key_view))?.apply {
            entries = arrayOf()
            entryValues = arrayOf()
            lifecycleScope.launch {
                val views = viewModel.getDictionaryViews()
                val names = views.map { it.dictionaryView.name }.toTypedArray()
                val ids = views.map { it.dictionaryView.dictionaryViewId.toString() }.toTypedArray()
                entries = names
                entryValues = ids
            }
            setOnPreferenceChangeListener { _, value ->
                viewModel.setDictionaryView((value as String).toLong())
                true
            }
        }
        findPreference<Preference>("sources")?.apply {
            setOnPreferenceClickListener {
                val directions = PreferencesFragmentDirections.actionNavPreferencesToNavSources()
                findNavController().navigate(directions)
                true
            }
        }
        findPreference<Preference>("author")?.apply {
            setOnPreferenceClickListener {
                val uri = Uri.parse(getString(R.string.preferences_author_url))
                startActivity(Intent(Intent.ACTION_VIEW, uri))
                true
            }
        }
        findPreference<Preference>("license")?.apply {
            setOnPreferenceClickListener {
                val uri = Uri.parse(getString(R.string.preferences_license_url))
                startActivity(Intent(Intent.ACTION_VIEW, uri))
                true
            }
        }
    }
}
