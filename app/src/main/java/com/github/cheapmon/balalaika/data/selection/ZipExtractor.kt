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

import android.content.Context
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.github.cheapmon.balalaika.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.zip.ZipFile
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
class ZipExtractor @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) {
    suspend fun saveZip(fileName: String, bytes: ByteArray): Either<Throwable, ZipFile> =
        withContext(dispatcher) {
            try {
                val file = context.filesDir.resolve(fileName)
                file.outputStream().use { out ->
                    ByteArrayInputStream(bytes).use { it.copyTo(out) }
                }
                ZipFile(file).right()
            } catch (ex: IOException) {
                ex.left()
            }
        }

    suspend fun extract(zip: ZipFile): Either<Throwable, DictionaryContents> =
        withContext(dispatcher) {
            try {
                val entries = zip.use { file ->
                    file.entries().asSequence().map { entry ->
                        Pair(
                            entry.name.removeSuffix(".csv"),
                            file.getInputStream(entry).bufferedReader().readText()
                        )
                    }.toMap()
                }
                val categories = entries["categories"] ?: failForFile("categories")
                val lexemes = entries["lexemes"] ?: failForFile("lexemes")
                val fullForms = entries["full_forms"] ?: failForFile("full_forms")
                val properties = entries["properties"] ?: failForFile("properties")
                val views = entries["views"] ?: failForFile("properties")
                val contents = DictionaryContents(
                    categories,
                    lexemes,
                    fullForms,
                    properties,
                    views
                )
                contents.right()
            } catch (ex: Exception) {
                ex.left()
            }
        }

    suspend fun removeZip(fileName: String): Either<Throwable, Unit> =
        withContext(dispatcher) {
            try {
                context.filesDir.resolve(fileName).delete()
                Unit.right()
            } catch (ex: Exception) {
                ex.left()
            }
        }

    private fun failForFile(name: String): Nothing =
        throw IllegalStateException("File is missing: $name")
}
