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
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryParserTest {
    private suspend fun loadFrom(contents: String) =
        YamlDictionaryParser(TestCoroutineDispatcher()).parse(contents)
            .attempt()
            .suspended()

    @Test
    fun `parses empty file`() = runBlockingTest {
        val response = loadFrom("dictionaries: []")
        assertTrue(response.isRight())
        assertTrue(response.fold({ false }, { it.isEmpty() }))
    }

    @Test
    fun `does not parse broken file`() = runBlockingTest {
        listOf("{", "}", ";").forEach { contents ->
            val response = loadFrom(contents)
            assertTrue(response.isLeft())
        }
    }

    @Test
    fun `parses single entry`() = runBlockingTest {
        val response = loadFrom(
            """
            dictionaries:
              - id: dict_a
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
                    id = "dict_a",
                    version = 1,
                    name = "Dictionary A",
                    summary = "This is dictionary A",
                    authors = "Some guy and another guy"
                )
            )
        )
    }

    @Test
    fun `parses multiple entries`() = runBlockingTest {
        val response = loadFrom(
            """
            dictionaries:
              - id: dict_a
                version: 1
                name: Dictionary A
                summary: This is dictionary A
                authors: Some guy and another guy
                isActive: true
              - id: dict_b
                name: Dictionary B
            """
        )
        assertTrue(response.isRight())
        val list = response.getOrElse { throw IllegalStateException() }
        assertEquals(list.size, 2)
        assertEquals(
            list, listOf(
                Dictionary(
                    id = "dict_a",
                    version = 1,
                    name = "Dictionary A",
                    summary = "This is dictionary A",
                    authors = "Some guy and another guy",
                    isActive = false
                ),
                Dictionary(
                    id = "dict_b",
                    name = "Dictionary B"
                )
            )
        )
    }
}
