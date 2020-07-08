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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSelectionDetailBinding
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectionDetailFragment : Fragment() {
    private lateinit var binding: FragmentSelectionDetailBinding

    private val args: SelectionDetailFragmentArgs by navArgs()
    private val viewModel: SelectionDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selection_detail, container, false)
        lifecycleScope.launch { bindUi() }
        return binding.root
    }

    private suspend fun bindUi() {
        viewModel.getDictionary(args.externalId).collect {
            with(binding) {
                dictionary = it
                listener = Listener()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    selectionDetailSummaryText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                    selectionDetailInfoText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                }
            }
        }
    }

    inner class Listener {
        fun onClickActivateButton(dictionary: Dictionary) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.selection_confirm_activate)
                .setPositiveButton(R.string.affirm) { _, _ ->
                    viewModel.toggleActive(dictionary)
                    val directions =
                        SelectionDetailFragmentDirections.actionNavSelectionDetailToNavSelection()
                    findNavController().navigate(directions)
                }.setNegativeButton(R.string.cancel, null)
                .show()
        }

        fun onClickDeactivateButton(dictionary: Dictionary) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.selection_confirm_deactivate)
                .setPositiveButton(R.string.affirm) { _, _ ->
                    viewModel.toggleActive(dictionary)
                    val directions =
                        SelectionDetailFragmentDirections.actionNavSelectionDetailToNavSelection()
                    findNavController().navigate(directions)
                }.setNegativeButton(R.string.cancel, null)
                .show()
        }

        fun onClickUpdateButton(dictionary: Dictionary) {
            Snackbar.make(binding.root, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
        }

        fun onClickAddButton(dictionary: Dictionary) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(
                    requireContext().getString(
                        R.string.selection_confirm_add,
                        dictionary.name
                    )
                ).setPositiveButton(R.string.affirm) { _, _ ->
                    viewModel.addDictionary(dictionary)
                    val directions =
                        SelectionDetailFragmentDirections.actionNavSelectionDetailToNavSelection()
                    findNavController().navigate(directions)
                }.setNegativeButton(R.string.cancel, null)
                .show()
        }

        fun onClickRemoveButton(dictionary: Dictionary) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.selection_confirm_remove)
                .setPositiveButton(R.string.affirm) { _, _ ->
                    viewModel.removeDictionary(dictionary.id)
                    val directions =
                        SelectionDetailFragmentDirections.actionNavSelectionDetailToNavSelection()
                    findNavController().navigate(directions)
                }.setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
}
