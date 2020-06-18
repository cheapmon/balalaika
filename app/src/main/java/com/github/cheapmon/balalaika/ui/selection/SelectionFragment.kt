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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSelectionTabsBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectionFragment : Fragment() {
    private lateinit var fragmentStateAdapter: SelectionFragmentStateAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var binding: FragmentSelectionTabsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selection_tabs, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentStateAdapter = SelectionFragmentStateAdapter(this)
        viewPager = binding.selectionPager
        viewPager.adapter = fragmentStateAdapter
        TabLayoutMediator(binding.selectionTabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = requireContext().getString(R.string.selection_tab_list)
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_library)
                }
                1 -> {
                    tab.text = requireContext().getString(R.string.selection_tab_download)
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_download)
                }
            }
        }.attach()
    }
}
