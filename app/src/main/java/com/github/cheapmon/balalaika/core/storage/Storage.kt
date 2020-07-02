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
package com.github.cheapmon.balalaika.core.storage

/** Simple key-value storage */
interface Storage {
    /** Read an [Int] from storage */
    fun getInt(key: String, defValue: Int): Int

    /** Read a [String] from storage */
    fun getString(key: String, defValue: String?): String?

    /** Add an [Int] to storage */
    fun putInt(key: String, value: Int)

    /** Add a [String] to storage */
    fun putString(key: String, value: String)

    /** Check if storage contains [key] */
    fun contains(key: String): Boolean
}