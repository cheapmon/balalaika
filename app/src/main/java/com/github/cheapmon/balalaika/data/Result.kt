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

sealed class Result<T, E> {
    data class Success<T, E>(val data: T) : Result<T, E>()
    data class Error<T, E>(val cause: E) : Result<T, E>()

    fun or(value: T): T = when (this) {
        is Success -> this.data
        else -> value
    }

    fun <R> map(block: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(block(this.data))
        is Error -> Error(this.cause)
    }

    fun <S> mapError(block: (E) -> S): Result<T, S> = when (this) {
        is Success -> Success(this.data)
        is Error -> Error(block(this.cause))
    }
}
