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
package com.github.cheapmon.balalaika.ui.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSelectionBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class SelectionFragment : Fragment() {
    lateinit var binding: FragmentSelectionBinding
    private val viewModel: SelectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()
        binding.selectionPager.adapter = SelectionFragmentStateAdapter(this, viewModel)
        TabLayoutMediator(binding.selectionTabs, binding.selectionPager) { tab, position ->
            tab.icon = when (position) {
                0 -> ContextCompat.getDrawable(context, R.drawable.ic_library)
                1 -> ContextCompat.getDrawable(context, R.drawable.ic_phone)
                2 -> ContextCompat.getDrawable(context, R.drawable.ic_download)
                else -> throw IllegalArgumentException("Position must be <= 2")
            }
            tab.text = when (position) {
                0 -> context.getString(R.string.selection_tab_list)
                1 -> context.getString(R.string.selection_tab_local)
                2 -> context.getString(R.string.selection_tab_download)
                else -> throw IllegalArgumentException("Position must be <= 2")
            }
        }.attach()
    }
}
