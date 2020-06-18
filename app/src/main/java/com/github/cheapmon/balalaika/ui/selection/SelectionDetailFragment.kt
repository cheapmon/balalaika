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

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSelectionDetailBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectionDetailFragment : Fragment() {
    private lateinit var binding: FragmentSelectionDetailBinding

    private val args: SelectionDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selection_detail, container, false)
        with(binding) {
            dictionary = args.dictionary
            remote = args.remote
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                selectionDetailSummaryText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                selectionDetailInfoText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
            }
            selectionButtonActivate.setOnClickListener { onClickActivateButton() }
            selectionButtonUpdate.setOnClickListener { onClickUpdateButton() }
            selectionButtonAdd.setOnClickListener { onClickAddButton() }
            selectionButtonRemove.setOnClickListener { onClickRemoveButton() }
        }
        return binding.root
    }

    private fun onClickActivateButton() {
        Snackbar.make(binding.root, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
    }

    private fun onClickUpdateButton() {
        Snackbar.make(binding.root, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
    }

    private fun onClickAddButton() {
        Snackbar.make(binding.root, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
    }

    private fun onClickRemoveButton() {
        Snackbar.make(binding.root, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
    }
}