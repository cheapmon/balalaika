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

import android.util.Log
import kotlin.experimental.ExperimentalTypeInference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Run [block] and wrap it in a [Result]
 *
 * _Note_: This catches _any_ error encountered while performing [block]. Handling of errors
 * is up to the caller.
 */
internal suspend fun <T> tryRun(block: suspend () -> T): Result<T, Throwable> {
    return try {
        Result.Success(block())
    } catch (t: Throwable) {
        Log.e(Result::class.java.name, "Operation failed with\n$t")
        Result.Error(t)
    }
}

/** Run [block] and wrap the operation in a [LoadState] */
internal fun <T> tryLoad(block: suspend () -> T): Flow<LoadState<T, Throwable>> = flow {
    emit(LoadState.Init())
    emit(LoadState.Loading())
    emit(LoadState.Finished(tryRun(block)))
}

/**
 * Run [block] and wrap the operation in a [ProgressState], indicating progress while the
 * operation is ongoing
 *
 * Example:
 * ```
 * tryProgress {
 *   progress(25)
 *   someOperation()
 *   progress(50)
 *   someOtherOperation()
 *   progress(75)
 *   return@tryProgress someResult
 * }
 * ```
 */
@OptIn(ExperimentalTypeInference::class)
internal fun <T, M> tryProgress(
    @BuilderInference
    block: suspend ProgressState.Listener<M>.() -> T
): Flow<ProgressState<T, M, Throwable>> = flow {
    emit(ProgressState.Init)
    emit(ProgressState.InProgress(0))
    val listener = object : ProgressState.Listener<M> {
        override suspend fun progress(inProgress: ProgressState.InProgress<M>) {
            val percentage = inProgress.percentage.coerceIn(0, 100)
            emit(inProgress.copy(percentage = percentage))
        }
    }
    emit(ProgressState.Finished(tryRun { block(listener) }))
}
