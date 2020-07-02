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
package com.github.cheapmon.balalaika.core

sealed class InstallState<out T>(val t: T) {
    data class Installable<out T>(private val tt: T) : InstallState<T>(tt)
    data class Installed<out T>(private val tt: T) : InstallState<T>(tt)
    data class Updatable<out T>(private val tt: T) : InstallState<T>(tt)

    fun isInstalled(): Boolean = this is Installed || this is Updatable
    fun isUpdatable(): Boolean = this is Updatable
}
