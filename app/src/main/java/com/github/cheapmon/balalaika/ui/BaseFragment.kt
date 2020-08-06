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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

/** Simple fragment boilerplate using view model and data binding */
abstract class BaseFragment<VM : ViewModel, B : ViewDataBinding>(
    viewModelClass: KClass<VM>,
    @LayoutRes private val layoutId: Int
) : Fragment() {
    /** View model for this fragment */
    val viewModel: VM by createViewModelLazy(viewModelClass, { this.viewModelStore })

    private lateinit var binding: B

    /** Create view binding */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            layoutId,
            container,
            false
        )
        onCreateBinding(binding)
        return binding.root
    }

    /** Observe data when view has been created */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData(binding, viewLifecycleOwner)
    }

    /** Additional operations on the view binding */
    abstract fun onCreateBinding(binding: B)

    /** Observe data from the view model */
    abstract fun observeData(binding: B, owner: LifecycleOwner)
}
