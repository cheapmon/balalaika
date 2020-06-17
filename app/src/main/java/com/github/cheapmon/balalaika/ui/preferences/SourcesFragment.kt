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
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.get
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.config.Config
import com.github.cheapmon.balalaika.data.config.ConfigLoader
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Fragment for displaying data and media sources */
@AndroidEntryPoint
class SourcesFragment : PreferenceFragmentCompat() {
    /** @suppress */
    @Inject
    lateinit var configLoader: ConfigLoader

    /** Load data and add callbacks */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.sources, rootKey)
        val category: PreferenceCategory? = preferenceScreen["contents"]
        preferenceScreen.addPreference(category)
        configLoader.readConfig().sources.forEach { source ->
            val preference = Preference(context).apply {
                title = source.name
                summary = source.authors
                setOnPreferenceClickListener {
                    showSourceDialog(source)
                    true
                }
            }
            category?.addPreference(preference)
        }
        findPreference<Preference>("icon")?.apply {
            setOnPreferenceClickListener {
                val uri = Uri.parse(getString(R.string.preferences_icon_url))
                startActivity(Intent(Intent.ACTION_VIEW, uri))
                true
            }
        }
    }

    /** Show summary and actions for a single source */
    private fun showSourceDialog(source: Config.Source) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(source.name)
            .setMessage(source.summary)
            .setPositiveButton(R.string.preferences_source_open) { _, _ -> openLink(source.url) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    /** Open an external URL */
    private fun openLink(url: String?) {
        try {
            val uri = Uri.parse(url)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (ex: Exception) {
            Snackbar.make(requireView(), "Could not open link", Snackbar.LENGTH_SHORT)
                .show()
        }
    }
}