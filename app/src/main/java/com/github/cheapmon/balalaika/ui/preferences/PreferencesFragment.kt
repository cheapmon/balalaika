package com.github.cheapmon.balalaika.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        findPreference<ListPreference>("default_view")?.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                val ids = withContext(Dispatchers.IO) {
                    BalalaikaDatabase.instance.dictionaryViewDao().getAll().map { it.viewId }.distinct()
                }
                entries = ids.map { it.capitalize() }.toTypedArray()
                entryValues = ids.toTypedArray()
            }
        }
        findPreference<ListPreference>("order_by")?.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                val categories = withContext(Dispatchers.IO) {
                    BalalaikaDatabase.instance.categoryDao().getOrdered().map { it.id to it.name }
                } + listOf("default" to "Default")
                entries = categories.map { it.second }.toTypedArray()
                entryValues = categories.map { it.first }.toTypedArray()
            }
        }
        return view
    }
}
