package com.github.cheapmon.balalaika.data.storage

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import javax.inject.Inject

class PreferenceStorage @Inject constructor(
    context: Context
) : Storage {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getInt(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    override fun getString(key: String, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    override fun putInt(key: String, value: Int) {
        preferences.edit { putInt(key, value) }
    }

    override fun putString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    override fun contains(key: String) = preferences.contains(key)
}