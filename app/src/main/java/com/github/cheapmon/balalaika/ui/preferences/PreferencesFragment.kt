package com.github.cheapmon.balalaika.ui.preferences

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.util.InjectorUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class PreferencesFragment : PreferenceFragmentCompat() {
    private val viewModel: PreferencesViewModel by viewModels {
        InjectorUtil.providePreferencesViewModelFactory(requireContext())
    }

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
    }
}