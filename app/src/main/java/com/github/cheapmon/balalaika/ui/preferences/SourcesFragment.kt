package com.github.cheapmon.balalaika.ui.preferences

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.get
import com.github.cheapmon.balalaika.Application
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.config.Config
import com.github.cheapmon.balalaika.data.config.ConfigLoader
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class SourcesFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var configLoader: ConfigLoader

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as Application).appComponent.inject(this)
    }

    private fun showSourceDialog(source: Config.Source) {
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