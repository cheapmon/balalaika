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
package com.github.cheapmon.balalaika.data.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Suppress("EXPERIMENTAL_API_USAGE")
@Singleton
internal class SharedPreferenceStorage(
    @ApplicationContext context: Context
) : PreferenceStorage {
    private val prefs: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, MODE_PRIVATE
        )
    }

    override var openDictionary: String by StringPreference(
        prefs,
        PREF_DICTIONARY,
        PREF_DICTIONARY_DEFAULT
    )
    override var observableOpenDictionary: Flow<String>
        get() = prefs.value.observeKey(PREF_DICTIONARY, PREF_DICTIONARY_DEFAULT)
        set(_) = throw IllegalAccessException("This property can't be changed.")

    companion object {
        const val PREFS_NAME = "balalaika"
        const val PREF_DICTIONARY = "pref_dictionary"
        const val PREF_DICTIONARY_DEFAULT = ""
    }

    private fun <T> SharedPreferences.observeKey(
        key: String,
        default: T
    ): Flow<T> {
        return callbackFlow {
            send(getItem(key, default))

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
                if (key == k) {
                    offer(getItem(key, default))
                }
            }

            registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    private fun <T> SharedPreferences.getItem(key: String, default: T): T {
        @Suppress("UNCHECKED_CAST")
        return when (default) {
            is String -> getString(key, default) as T
            is Int -> getInt(key, default) as T
            is Long -> getLong(key, default) as T
            is Boolean -> getBoolean(key, default) as T
            is Float -> getFloat(key, default) as T
            is Set<*> -> getStringSet(key, default as Set<String>) as T
            is MutableSet<*> -> getStringSet(key, default as MutableSet<String>) as T
            else -> throw IllegalArgumentException("This type can't be handled.")
        }
    }

    private class BooleanPreference(
        private val preferences: Lazy<SharedPreferences>,
        private val name: String,
        private val defaultValue: Boolean
    ) : ReadWriteProperty<Any, Boolean> {
        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return preferences.value.getBoolean(name, defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
            preferences.value.edit { putBoolean(name, value) }
        }
    }

    private class StringPreference(
        private val preferences: Lazy<SharedPreferences>,
        private val name: String,
        private val defaultValue: String
    ) : ReadWriteProperty<Any, String?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): String {
            return preferences.value.getString(name, defaultValue) ?: defaultValue
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
            preferences.value.edit { putString(name, value) }
        }
    }
}
