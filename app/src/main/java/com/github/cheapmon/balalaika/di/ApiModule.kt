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
package com.github.cheapmon.balalaika.di

import com.github.cheapmon.balalaika.data.dictionary.wordnet.WordnetApi
import com.github.cheapmon.balalaika.data.selection.DictionaryApi
import com.github.cheapmon.balalaika.util.Constants
import com.squareup.moshi.Moshi
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/** API dependency injection module */
@Module
@InstallIn(ActivityComponent::class)
class ApiModule {
    /** @suppress */
    @ActivityScoped
    @Provides
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BASIC)
        })
        .build()

    /** @suppress */
    @ActivityScoped
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    /** @suppress */
    @ActivityScoped
    @Provides
    @DictionaryRetrofit
    fun provideRetrofit(constants: Constants, client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(constants.SERVER_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    /** @suppress */
    @ActivityScoped
    @Provides
    fun provideDictionaryApi(@DictionaryRetrofit retrofit: Retrofit): DictionaryApi =
        retrofit.create(DictionaryApi::class.java)

    /** @suppress */
    @ActivityScoped
    @Provides
    fun provideTikXml(): TikXml = TikXml.Builder().exceptionOnUnreadXml(false).build()

    /** @suppress */
    @ActivityScoped
    @Provides
    @WordnetRetrofit
    fun provideWordnetRetrofit(
        constants: Constants,
        client: OkHttpClient,
        tikXml: TikXml
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(constants.WORDNET_URL)
            .client(client)
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()

    /** @suppress */
    @ActivityScoped
    @Provides
    fun provideWordnetApi(@WordnetRetrofit retrofit: Retrofit): WordnetApi =
        retrofit.create(WordnetApi::class.java)
}
