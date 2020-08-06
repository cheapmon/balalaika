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

/** Result of an operation */
sealed class Result<T, E> {
    /** Operation succeeded */
    data class Success<T, E>(
        /** Result data */
        val data: T
    ) : Result<T, E>()

    /** Operation failed */
    data class Error<T, E>(
        /** Cause of Failure */
        val cause: E
    ) : Result<T, E>()

    /** Retrieve result or an alternative value if the operation failed */
    fun or(value: T): T = when (this) {
        is Success -> this.data
        else -> value
    }

    /** Transform result if the operation succeeded */
    inline fun <R> map(block: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(block(this.data))
        is Error -> Error(this.cause)
    }

    /** Transform result if the operation failed */
    inline fun <S> mapError(block: (E) -> S): Result<T, S> = when (this) {
        is Success -> Success(this.data)
        is Error -> Error(block(this.cause))
    }

    /** Run [block] if the operation succeeded */
    inline fun onSuccess(block: (T) -> Unit): Result<T, E> {
        if (this is Success) block(this.data)
        return this
    }

    /** Run [block] if the operation failed */
    inline fun onError(block: (E) -> Unit): Result<T, E> {
        if (this is Error) block(this.cause)
        return this
    }
}
