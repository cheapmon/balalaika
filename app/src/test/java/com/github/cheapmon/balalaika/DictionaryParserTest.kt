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
package com.github.cheapmon.balalaika

import com.github.cheapmon.balalaika.data.selection.DictionaryInfo
import com.github.cheapmon.balalaika.data.selection.JsonDictionaryParser
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryParserTest {
    private val dispatcher = TestCoroutineDispatcher()

    private suspend fun loadFrom(contents: String) =
        JsonDictionaryParser(dispatcher, Moshi.Builder().build()).parse(contents)

    @Test
    fun `parses empty file`() = runBlockingTest {
        val response = loadFrom("[]")
        assertTrue(response.isEmpty())
    }

    @Test(expected = JsonEncodingException::class)
    fun `does not parse broken file`() = runBlockingTest {
        loadFrom("&&;")
    }

    @Test
    fun `parses single entry`() = runBlockingTest {
        val list = loadFrom(
            """
            [{
                "id": "dict_a",
                "version": 1,
                "name": "Dictionary A",
                "summary": "This is dictionary A",
                "authors": "Some guy and another guy",
                "additionalInfo": "",
                "isActive": true
            }]
            """
        )
        assertEquals(list.size, 1)
        assertEquals(
            listOf(
                DictionaryInfo(
                    id = "dict_a",
                    version = 1,
                    name = "Dictionary A",
                    summary = "This is dictionary A",
                    authors = "Some guy and another guy",
                    additionalInfo = ""
                )
            ), list
        )
    }

    @Test
    fun `parses multiple entries`() = runBlockingTest {
        val list = loadFrom(
            """
            [
                {
                    "id": "dict_a",
                    "version": 1,
                    "name": "Dictionary A",
                    "summary": "This is dictionary A",
                    "authors": "Some guy and another guy",
                    "additionalInfo": "",
                    "isActive": true
                },
                {
                    "id": "dict_b",
                    "version": 2,
                    "name": "Dictionary B",
                    "summary": "This is dictionary B",
                    "authors": "No one",
                    "additionalInfo": "Info",
                    "isInstalled": true
                }
            ]
            """
        )
        assertEquals(list.size, 2)
        assertEquals(
            listOf(
                DictionaryInfo(
                    id = "dict_a",
                    version = 1,
                    name = "Dictionary A",
                    summary = "This is dictionary A",
                    authors = "Some guy and another guy",
                    additionalInfo = ""
                ),
                DictionaryInfo(
                    id = "dict_b",
                    version = 2,
                    name = "Dictionary B",
                    summary = "This is dictionary B",
                    authors = "No one",
                    additionalInfo = "Info"
                )
            ), list
        )
    }
}
