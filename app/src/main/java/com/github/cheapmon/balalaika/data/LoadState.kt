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
package com.github.cheapmon.balalaika.data

sealed class LoadState<T, E> {
    class Init<T, E> : LoadState<T, E>()
    class Loading<T, E> : LoadState<T, E>()
    data class Finished<T, E>(val data: Result<T, E>) : LoadState<T, E>()

    fun or(value: T): T = when (this) {
        is Finished -> this.data.or(value)
        else -> value
    }

    fun <R> map(block: (T) -> R): LoadState<R, E> = when (this) {
        is Init -> Init()
        is Loading -> Loading()
        is Finished -> Finished(this.data.map(block))
    }

    fun <S> mapError(block: (E) -> S): LoadState<T, S> = when (this) {
        is Init -> Init()
        is Loading -> Loading()
        is Finished -> Finished(this.data.mapError(block))
    }
}
