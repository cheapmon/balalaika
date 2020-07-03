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
package com.github.cheapmon.balalaika.data.selection

import com.github.cheapmon.balalaika.core.Response
import java.io.IOException
import java.lang.IllegalStateException
import java.util.zip.ZipFile
import javax.inject.Inject

class ZipExtractor @Inject constructor() {
    fun extract(zip: ZipFile): Response<DictionaryContents> {
        return try {
            val entries = zip.use { file ->
                file.entries().asSequence().map { entry ->
                    entry.name to file.getInputStream(entry)
                }.toMap()
            }
            val categories = entries["categories"] ?: return failureForFile("categories")
            val lexemes = entries["lexemes"] ?: return failureForFile("lexemes")
            val fullForms = entries["full_forms"] ?: return failureForFile("full_forms")
            val properties = entries["properties"] ?: return failureForFile("properties")
            val views = entries["views"] ?: return failureForFile("properties")
            val contents = DictionaryContents(
                categories,
                lexemes,
                fullForms,
                properties,
                views
            )
            Response.Success(contents)
        } catch (ex: IOException) {
            Response.Failure(ex)
        }
    }

    private fun failureForFile(name: String) = Response.Failure<DictionaryContents>(
        cause = IllegalStateException("File is missing: $name")
    )
}
