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

import arrow.fx.IO
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class RetrofitTest {
    lateinit var server: MockWebServer
    lateinit var api: DictionaryApi

    @Before
    fun before() {
        server = MockWebServer()
        server.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        api = retrofit.create(DictionaryApi::class.java)
    }

    @After
    fun after() {
        server.shutdown()
    }

    @Test
    fun `reads empty list`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("[]"))
        val result = IO.effect { api.listDictionaries() }.attempt().suspended()
        assertTrue(result.isRight())
        assertTrue(result.fold({ false }, { it.isEmpty() }))
    }

    @Test
    fun `reads single entry`() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                [{
                    "id": "dict_a",
                    "version": 1,
                    "name": "Dictionary A",
                    "summary": "This is dictionary A",
                    "authors": "Some guy and another guy",
                    "isActive": true
                }]
                """
            )
        )
        val result = IO.effect { api.listDictionaries() }.attempt().suspended()
        assertTrue(result.isRight())
        assertTrue(result.fold({ false }, { it.size == 1 }))
        assertEquals(
            result.fold({ emptyList<Dictionary>() }, { it }),
            listOf(
                Dictionary(
                    id = "dict_a",
                    version = 1,
                    name = "Dictionary A",
                    summary = "This is dictionary A",
                    authors = "Some guy and another guy",
                    isActive = true
                )
            )
        )
    }

    @Test(expected = JsonEncodingException::class)
    fun `rejects malformed JSON`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("&&;"))
        api.listDictionaries()
        Unit
    }

    interface DictionaryApi {
        @GET("dictionaries")
        suspend fun listDictionaries(): List<Dictionary>

        @GET("dictionary/{id}")
        suspend fun getDictionary(@Path("id") id: String): ResponseBody
    }
}
