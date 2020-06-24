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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSelectionDetailBinding
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectionDetailFragment : Fragment() {
    private lateinit var binding: FragmentSelectionDetailBinding

    private val args: SelectionDetailFragmentArgs by navArgs()
    private val viewModel: SelectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_selection_detail, container, false)
        bindUi()
        return binding.root
    }

    private fun bindUi() {
        viewModel.getDictionary(args.dictionary.dictionaryId).observe(viewLifecycleOwner, Observer {
            with(binding) {
                dictionary = it
                remote = args.remote
                listener = Listener()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    selectionDetailSummaryText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                    selectionDetailInfoText.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                }
                if (it?.isActive == true) {
                    (selectionButtonActivate as MaterialButton).icon =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_checkbox_empty)
                } else {
                    (selectionButtonActivate as MaterialButton).icon =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_checkbox)
                }
            }
        })
    }

    inner class Listener {
        fun onClickActivateButton(dictionary: Dictionary) {
            val message = if (dictionary.isActive) {
                R.string.selection_confirm_deactivate
            } else {
                R.string.selection_confirm_activate
            }
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(message)
                .setPositiveButton(R.string.affirm) { _, _ -> viewModel.toggleActive(dictionary.dictionaryId) }
                .setNegativeButton(R.string.cancel, null)
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
                    val directions =
                        SelectionDetailFragmentDirections.actionNavSelectionDetailToNavSelection(0)
                    findNavController().navigate(directions)
                    viewModel.addDictionary(dictionary)
                }.setNegativeButton(R.string.cancel, null)
                .show()
        }

        fun onClickRemoveButton(dictionary: Dictionary) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.selection_confirm_remove)
                .setPositiveButton(R.string.affirm) { _, _ ->
                    val directions =
                        SelectionDetailFragmentDirections.actionNavSelectionDetailToNavSelection()
                    findNavController().navigate(directions)
                    viewModel.removeDictionary(dictionary.dictionaryId)
                }.setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
}
