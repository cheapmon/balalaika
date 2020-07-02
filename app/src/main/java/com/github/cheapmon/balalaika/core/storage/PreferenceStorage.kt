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
package com.github.cheapmon.balalaika.core.storage

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Android preferences [storage][Storage] */
class PreferenceStorage @Inject constructor(
    @ApplicationContext context: Context
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
