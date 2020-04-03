package com.github.cheapmon.balalaika.ui.preferences

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.cheapmon.balalaika.Application
import com.github.cheapmon.balalaika.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class PreferencesFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var viewModel: PreferencesViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<ListPreference>(getString(R.string.preferences_key_order))?.apply {
            val comparators = viewModel.getComparators()
            entries = comparators
            entryValues = comparators
            setOnPreferenceChangeListener { _, value ->
                viewModel.setOrdering(value as String)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as Application).appComponent.inject(this)
    }
}