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
package com.github.cheapmon.balalaika.util

import android.content.Context

object ResourceUtil {
    private val idCache = hashMapOf<String, Int>()

    fun drawable(context: Context, name: String) = getIdentifier(context, name, "drawable")
    fun raw(context: Context, name: String) = getIdentifier(context, name, "raw")

    private fun getIdentifier(context: Context, name: String, defType: String): Int {
        val id = idCache[name]
        return if (id == null) {
            val newId = context.resources.getIdentifier(
                name,
                defType,
                "com.github.cheapmon.balalaika"
            )
            idCache[name] = newId
            newId
        } else {
            id
        }
    }
}