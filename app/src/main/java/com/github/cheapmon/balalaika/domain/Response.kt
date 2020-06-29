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
package com.github.cheapmon.balalaika.domain

sealed class Response<out T> {
    object Pending : Response<Nothing>()
    data class Success<out T>(val data: List<T>) : Response<T>()
    data class Failure<out T>(val cause: Throwable) : Response<T>()

    fun isPending(): Boolean = this is Pending
    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure
}

fun <T> Response<T>.or(value: List<T>): List<T> = when (this) {
    is Response.Pending -> value
    is Response.Success -> data
    is Response.Failure -> value
}

fun <T> Response<T>.orEmpty(): List<T> = when (this) {
    is Response.Pending -> listOf()
    is Response.Success -> data
    is Response.Failure -> listOf()
}
