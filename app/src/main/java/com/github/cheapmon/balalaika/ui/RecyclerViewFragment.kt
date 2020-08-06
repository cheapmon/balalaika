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
package com.github.cheapmon.balalaika.ui

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

/** Simple boilerplate for fragments with a recycler view */
abstract class RecyclerViewFragment<VM : ViewModel,
    B : ViewDataBinding,
    R : RecyclerView.Adapter<*>>(
        viewModelClass: KClass<VM>,
        @LayoutRes layoutId: Int,
        private val hasDivider: Boolean
    ) : BaseFragment<VM, B>(viewModelClass, layoutId) {
    private lateinit var adapter: R

    /** @suppress */
    override fun onCreateBinding(binding: B) {
        val linearLayoutManager = LinearLayoutManager(context)
        val recyclerView = createRecyclerView(binding)
        adapter = createRecyclerViewAdapter()
        with(recyclerView) {
            adapter = this@RecyclerViewFragment.adapter
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            if (hasDivider) {
                addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            }
        }
    }

    /** @suppress */
    override fun observeData(binding: B, owner: LifecycleOwner) {
        observeData(binding, owner, adapter)
    }

    /** Additional setup for the recycler view */
    abstract fun createRecyclerView(binding: B): RecyclerView

    /** Additional setup for the recycler view adapter */
    abstract fun createRecyclerViewAdapter(): R

    /** Observe data and submit to the adapter */
    abstract fun observeData(binding: B, owner: LifecycleOwner, adapter: R)
}
