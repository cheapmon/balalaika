/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.util

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.*
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

/*
    IMPORTANT NOTE

    This code is taken directly from here:
    https://android-review.googlesource.com/c/platform/frameworks/support/+/1428676

    It should be available as an official androidx library artifact very soon.
 */

/**
 * The class responsible for accessing the data from a [Flow] of [PagingData].
 * In order to obtain an instance of [LazyPagingItems] use the [collectAsLazyPagingItems] extension
 * method of [Flow] with [PagingData].
 * This instance can be used by the [items] and [itemsIndexed] methods inside [LazyListScope] to
 * display data received from the [Flow] of [PagingData].
 *
 * @param flow the [Flow] object which contains a stream of [PagingData] elements.
 * @param T the type of value used by [PagingData].
 */
class LazyPagingItems<T : Any> internal constructor(
    private val flow: Flow<PagingData<T>>
) {
    /**
     * The number of items which can be accessed.
     */
    val itemCount: Int
        get() = pagingDataDiffer.size
    private val mainDispatcher = Dispatchers.Main
    internal val recomposerPlaceholder: MutableState<Int> = mutableStateOf(0)

    @SuppressLint("RestrictedApi")
    private val differCallback = object : DifferCallback {
        override fun onChanged(position: Int, count: Int) {
            // TODO: b/168285687 explore how to recompose only when the currently visible item
            //  has been changed
            recomposerPlaceholder.value++
        }

        override fun onInserted(position: Int, count: Int) {
            recomposerPlaceholder.value++
        }

        override fun onRemoved(position: Int, count: Int) {
            recomposerPlaceholder.value++
        }
    }
    private val pagingDataDiffer = object : PagingDataDiffer<T>(
        differCallback = differCallback,
        mainDispatcher = mainDispatcher
    ) {
        override suspend fun presentNewList(
            previousList: NullPaddedList<T>,
            newList: NullPaddedList<T>,
            newCombinedLoadStates: CombinedLoadStates,
            lastAccessedIndex: Int
        ): Int? {
            // TODO: This logic may be changed after the implementation of an async model which
            //  composes the offscreen elements
            recomposerPlaceholder.value++
            return null
        }
    }

    /**
     * Returns the item specified at [index] and notifies Paging of the item accessed in
     * order to trigger any loads necessary to fulfill [PagingConfig.prefetchDistance].
     *
     * @param index the index of the item which should be returned.
     * @return the item specified at [index] or null if the [index] is not between correct
     * bounds or the item is a placeholder.
     */
    operator fun get(index: Int): T? {
        return pagingDataDiffer[index]
    }

    /**
     * A [CombinedLoadStates] object which represents the current loading state.
     */
    var loadState by mutableStateOf(CombinedLoadStates(InitialLoadStates))
        private set

    internal suspend fun collectLoadState() {
        pagingDataDiffer.loadStateFlow.collect {
            loadState = it
        }
    }

    internal suspend fun collectPagingData() {
        flow.collectLatest {
            pagingDataDiffer.collectFrom(it)
        }
    }
}

private val IncompleteLoadState = LoadState.NotLoading(false)
private val InitialLoadStates = LoadStates(
    IncompleteLoadState,
    IncompleteLoadState,
    IncompleteLoadState
)

/**
 * Collects values from this [Flow] of [PagingData] and represents them inside a [LazyPagingItems]
 * instance. The [LazyPagingItems] instance can be used by the [items] and [itemsIndexed] methods
 * from [LazyListScope] in order to display the data obtained from a [Flow] of [PagingData].
 *
 * @sample androidx.compose.paging.samples.PagingBackendSample
 */
@Composable
fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItems(): LazyPagingItems<T> {
    val lazyPagingItems = remember(this) { LazyPagingItems(this) }
    LaunchedTask(lazyPagingItems) {
        lazyPagingItems.collectPagingData()
    }
    LaunchedTask(lazyPagingItems) {
        lazyPagingItems.collectLoadState()
    }
    return lazyPagingItems
}

/**
 * Adds the [LazyPagingItems] and their content to the scope. The range from 0 (inclusive) to
 * [LazyPagingItems.itemCount] (inclusive) always represents the full range of presentable items,
 * because every event from [PagingDataDiffer] will trigger a recomposition.
 *
 * @sample androidx.compose.paging.samples.ItemsDemo
 *
 * @param lazyPagingItems the items received from a [Flow] of [PagingData].
 * @param itemContent the content displayed by a single item. In case the item is `null`, the
 * [itemContent] method should handle the logic of displaying a placeholder instead of the main
 * content displayed by an item which is not `null`.
 */
fun <T : Any> LazyListScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    // this state recomposes every time the LazyPagingItems receives an update and changes the
    // value of recomposerPlaceholder
    @Suppress("UNUSED_VARIABLE")
    val recomposerPlaceholder = lazyPagingItems.recomposerPlaceholder.value
    items((0 until lazyPagingItems.itemCount).toList()) { index ->
        val item = lazyPagingItems[index]
        itemContent(item)
    }
}

/**
 * Adds the [LazyPagingItems] and their content to the scope where the content of an item is
 * aware of its local index. The range from 0 (inclusive) to [LazyPagingItems.itemCount] (inclusive)
 * always represents the full range of presentable items, because every event from
 * [PagingDataDiffer] will trigger a recomposition.
 *
 * @sample androidx.compose.paging.samples.ItemsIndexedDemo
 *
 * @param lazyPagingItems the items received from a [Flow] of [PagingData].
 * @param itemContent the content displayed by a single item. In case the item is `null`, the
 * [itemContent] method should handle the logic of displaying a placeholder instead of the main
 * content displayed by an item which is not `null`.
 */
fun <T : Any> LazyListScope.itemsIndexed(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(index: Int, value: T?) -> Unit
) {
    // this state recomposes every time the LazyPagingItems receives an update and changes the
    // value of recomposerPlaceholder
    @Suppress("UNUSED_VARIABLE")
    val recomposerPlaceholder = lazyPagingItems.recomposerPlaceholder.value
    items((0 until lazyPagingItems.itemCount).toList()) { index ->
        val item = lazyPagingItems[index]
        itemContent(index, item)
    }
}