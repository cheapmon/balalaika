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

import com.github.cheapmon.balalaika.core.ListResponse
import com.github.cheapmon.balalaika.core.data
import com.github.cheapmon.balalaika.data.selection.YamlDictionaryParser
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryParserTest {
    private fun loadFrom(contents: String): ListResponse<Dictionary> {
        return YamlDictionaryParser().parse(contents.byteInputStream())
    }

    @Test
    fun `parses empty file`() {
        val response = loadFrom("dictionaries: []")
        assertTrue(response.isSuccess())
        assertTrue(response.data.isEmpty())
    }

    @Test
    fun `does not parse broken file`() {
        listOf("{", "}", ";").forEach { contents ->
            val response = loadFrom(contents)
            assertTrue(response.isFailure())
        }
    }

    @Test
    fun `parses single entry`() {
        val response = loadFrom(
            """
            dictionaries:
              - dictionaryId: 12
                externalId: dict_a
                version: 1
                name: Dictionary A
                summary: This is dictionary A
                authors: Some guy and another guy
                isActive: true
            """
        )
        assertTrue(response.isSuccess())
        assertEquals(response.data.size, 1)
        assertEquals(
            response.data, listOf(
                Dictionary(
                    externalId = "dict_a",
                    version = 1,
                    name = "Dictionary A",
                    summary = "This is dictionary A",
                    authors = "Some guy and another guy"
                )
            )
        )
    }

    @Test
    fun `parses multiple entries`() {
        val response = loadFrom(
            """
            dictionaries:
              - dictionaryId: 12
                externalId: dict_a
                version: 1
                name: Dictionary A
                summary: This is dictionary A
                authors: Some guy and another guy
                isActive: true
              - externalId: dict_b
                name: Dictionary B
            """
        )
        assertTrue(response.isSuccess())
        assertEquals(response.data.size, 2)
        assertEquals(
            response.data, listOf(
                Dictionary(
                    dictionaryId = 0,
                    externalId = "dict_a",
                    version = 1,
                    name = "Dictionary A",
                    summary = "This is dictionary A",
                    authors = "Some guy and another guy",
                    isActive = false
                ),
                Dictionary(
                    externalId = "dict_b",
                    name = "Dictionary B"
                )
            )
        )
    }
}
