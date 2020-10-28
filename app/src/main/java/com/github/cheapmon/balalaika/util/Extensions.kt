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

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy

/** Exhaustive matching for `when` in statements */
val <T> T.exhaustive: T
    get() = this

/**
 * `Lazy` delegate to access fragment arguments inside of a `ViewModel`
 *
 * _Note_: This is especially useful when using dependency injection, since Hilt can inject a
 * `SavedStateHandle`, as explained in
 * [Hilt and Jetpack integrations](https://developer.android.com/training/dependency-injection/hilt-jetpack).
 *
 * Example:
 * ```
 * class MyViewModel @Inject constructor(
 *   @Assisted savedStateHandle: SavedStateHandle
 * ) : ViewModel() {
 *   private val navArgs: MyNavArgs by navArgs(savedStateHandle)
 * }
 * ```
 */
inline fun <reified Args : NavArgs> navArgs(savedStateHandle: SavedStateHandle) =
    NavArgsLazy(Args::class) { savedStateHandle.toBundle() }

/** Convert `SavedStateHandle` to `Bundle` */
fun SavedStateHandle.toBundle() =
    bundleOf(*this.keys().map { Pair(it, this.get<Any?>(it)) }.toTypedArray())
