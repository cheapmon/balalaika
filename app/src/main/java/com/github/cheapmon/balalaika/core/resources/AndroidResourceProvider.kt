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
package com.github.cheapmon.balalaika.core.resources

import android.content.Context
import com.github.cheapmon.balalaika.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile
import javax.inject.Inject

/**
 * [Resource provider][ResourceProvider] which reads Android resource files
 */
class AndroidResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {
    override val dictionaryList: InputStream =
        context.resources.openRawResource(R.raw.dictionaries)

    private val dictionaries = context.assets.list("")
        ?.filter { it.endsWith(".zip") }
        .orEmpty()

    override fun getDictionaryZip(name: String): ZipFile? {
        val fileName = dictionaries.find { it.startsWith(name) } ?: return null
        val file = File(fileName).apply {
            outputStream().use { context.assets.open(fileName).copyTo(it) }
        }
        return ZipFile(file)
    }
}
