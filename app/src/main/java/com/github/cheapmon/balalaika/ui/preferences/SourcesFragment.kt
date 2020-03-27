package com.github.cheapmon.balalaika.ui.preferences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.get
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.AndroidResourceLoader
import com.github.cheapmon.balalaika.util.ImportUtil
import com.github.cheapmon.balalaika.util.Source
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SourcesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.sources, rootKey)
        val category: PreferenceCategory? = preferenceScreen["contents"]
        preferenceScreen.addPreference(category)
        ImportUtil(AndroidResourceLoader(requireContext())).readConfig().sources.forEach { source ->
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

    private fun showSourceDialog(source: Source) {
        MaterialAlertDialogBuilder(context)
            .setTitle(source.name)
            .setMessage(source.summary)
            .setPositiveButton(R.string.preferences_source_open) { _, _ -> openLink(source.url) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

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