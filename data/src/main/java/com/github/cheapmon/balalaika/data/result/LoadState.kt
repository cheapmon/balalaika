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
package com.github.cheapmon.balalaika.data.result

/** State of an asynchronous operation */
public sealed class LoadState<T, E> {
    /** Operation has been started */
    public class Init<T, E> : LoadState<T, E>()

    /** Operation is in progress */
    public class Loading<T, E> : LoadState<T, E>()

    /** Operation is finished and has either succeeded or failed */
    public data class Finished<T, E>(
        /** Result of operation */
        val data: Result<T, E>
    ) : LoadState<T, E>()

    /** Retrieve result or an alternative value if the operation failed */
    public fun or(value: T): T = when (this) {
        is Finished -> this.data.or(value)
        else -> value
    }

    /** Transform result if the operation succeeded */
    public inline fun <R> map(block: (T) -> R): LoadState<R, E> = when (this) {
        is Init -> Init()
        is Loading -> Loading()
        is Finished -> Finished(this.data.map(block))
    }

    /** Transform result if the operation failed */
    public inline fun <S> mapError(block: (E) -> S): LoadState<T, S> = when (this) {
        is Init -> Init()
        is Loading -> Loading()
        is Finished -> Finished(this.data.mapError(block))
    }

    /** Run [block] if this operation succeeded */
    public inline fun onSuccess(block: (T) -> Unit): LoadState<T, E> {
        if (this is Finished) this.data.onSuccess(block)
        return this
    }

    /** Run [block] if this operation failed */
    public inline fun onError(block: (E) -> Unit): LoadState<T, E> {
        if (this is Finished) this.data.onError(block)
        return this
    }
}
