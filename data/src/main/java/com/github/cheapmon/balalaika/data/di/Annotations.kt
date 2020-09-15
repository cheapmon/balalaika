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
package com.github.cheapmon.balalaika.data.di

import javax.inject.Qualifier

/** Denotes the default dispatcher for this application */
@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class DefaultDispatcher

/** Denotes the IO dispatcher for this application */
@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class IoDispatcher

/** Denotes the main dispatcher for this application */
@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class MainDispatcher

/** Denotes the main immediate dispatcher for this application */
@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class MainImmediateDispatcher

/** Retrofit for dictionaries */
@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class DictionaryRetrofit

/** Retrofit for Wordnet */
@Retention(AnnotationRetention.BINARY)
@Qualifier
internal annotation class WordnetRetrofit
