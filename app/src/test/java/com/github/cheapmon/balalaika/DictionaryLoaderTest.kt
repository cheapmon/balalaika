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

import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.domain.misc.Config
import com.github.cheapmon.balalaika.domain.misc.Response
import com.github.cheapmon.balalaika.domain.misc.data
import com.github.cheapmon.balalaika.domain.resources.ResourceLoader
import com.github.cheapmon.balalaika.domain.services.YamlDictionaryParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

// TODO: Rename
@RunWith(MockitoJUnitRunner::class)
class DictionaryLoaderTest {
    @Mock
    private lateinit var resourceLoader: ResourceLoader

    private fun loadFrom(contents: String): Response<Config> {
        `when`(resourceLoader.dictionaryList).thenReturn(contents.byteInputStream())
        return YamlDictionaryParser(resourceLoader).parse()
    }

    @Test
    fun `parses empty file`() {
        val response = loadFrom("dictionaries: []")
        assertTrue(response.isSuccess())
        assertTrue(response.data.dictionaries.isEmpty())
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
        assertEquals(response.data.dictionaries.size, 1)
        assertEquals(
            response.data.dictionaries, listOf(
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
        assertEquals(response.data.dictionaries.size, 2)
        assertEquals(
            response.data.dictionaries, listOf(
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