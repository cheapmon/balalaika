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
import androidx.datastore.DataStore
import androidx.datastore.Serializer
import androidx.datastore.createDataStore
import com.github.cheapmon.balalaika.data.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
internal class DataStoreStorage(
    @ApplicationContext context: Context
) : PreferenceStorage {
    private val dataStore: DataStore<UserPreferences> = context.createDataStore(
        fileName = "prefs",
        serializer = object : Serializer<UserPreferences> {
            override fun readFrom(input: InputStream): UserPreferences =
                UserPreferences.ADAPTER.decode(input)

            override fun writeTo(t: UserPreferences, output: OutputStream) =
                t.encode(output)
        }
    )

    override val openDictionary: Flow<String?> =
        dataStore.data.map { preferences -> preferences.openDictionary }

    override suspend fun setOpenDictionary(dictionaryId: String?) {
        dataStore.updateData { preferences ->
            preferences.copy(openDictionary = dictionaryId)
        }
    }
}
