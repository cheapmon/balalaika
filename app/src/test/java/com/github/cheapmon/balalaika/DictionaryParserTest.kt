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

import arrow.core.getOrElse
import com.github.cheapmon.balalaika.data.selection.YamlDictionaryParser
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import java.lang.IllegalStateException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryParserTest {
    private fun loadFrom(contents: String) =
        YamlDictionaryParser().parse(contents.byteInputStream(), null)

    @Test
    fun `parses empty file`() {
        val response = loadFrom("dictionaries: []")
        assertTrue(response.isRight())
        assertTrue(response.fold({ false }, { it.isEmpty() }))
    }

    @Test
    fun `does not parse broken file`() {
        listOf("{", "}", ";").forEach { contents ->
            val response = loadFrom(contents)
            assertTrue(response.isLeft())
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
        assertTrue(response.isRight())
        val list = response.getOrElse { throw IllegalStateException() }
        assertEquals(list.size, 1)
        assertEquals(
            list, listOf(
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
        assertTrue(response.isRight())
        val list = response.getOrElse { throw IllegalStateException() }
        assertEquals(list.size, 2)
        assertEquals(
            list, listOf(
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
