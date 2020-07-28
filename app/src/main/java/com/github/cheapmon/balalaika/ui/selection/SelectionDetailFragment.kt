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

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.cheapmon.balalaika.MainViewModel
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.FragmentSelectionDetailBinding
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.ui.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Fragment for displaying information about a dictionary
 *
 * The user can:
 * - Activate a dictionary
 * - Deactivate a dictionary
 * - Update a dictionary
 * - Add a dictionary to the library
 * - Remove a dictionary from the library
 */
@AndroidEntryPoint
class SelectionDetailFragment :
    BaseFragment<SelectionDetailViewModel, FragmentSelectionDetailBinding>(
        SelectionDetailViewModel::class,
        R.layout.fragment_selection_detail
    ) {
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreateBinding(binding: FragmentSelectionDetailBinding) {}

    override fun observeData(binding: FragmentSelectionDetailBinding, owner: LifecycleOwner) {
        lifecycleScope.launch {
            viewModel.dictionary.collect {
                with(binding) {
                    dictionary = it
                    listener = Listener()
                }
            }
        }
    }

    /** @suppress */
    inner class Listener {
        fun onClickActivateButton(dictionary: Dictionary) {
            val message = requireContext().getString(R.string.selection_confirm_activate)
            showDialog(message) { activityViewModel.activate(dictionary) }
        }

        fun onClickDeactivateButton(dictionary: Dictionary) {
            val message = requireContext().getString(R.string.selection_confirm_deactivate)
            showDialog(message) { activityViewModel.deactivate(dictionary) }
        }

        fun onClickUpdateButton(dictionary: Dictionary) {
            val message = requireContext().getString(R.string.selection_confirm_update)
            showDialog(message) { activityViewModel.update(dictionary) }
        }

        fun onClickAddButton(dictionary: Dictionary) {
            val message = requireContext().getString(
                R.string.selection_confirm_add,
                dictionary.name
            )
            showDialog(message) { activityViewModel.install(dictionary) }
        }

        fun onClickRemoveButton(dictionary: Dictionary) {
            val message = requireContext().getString(R.string.selection_confirm_remove)
            showDialog(message) { activityViewModel.remove(dictionary) }
        }

        private fun showDialog(message: String, block: () -> Unit) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(message)
                .setPositiveButton(R.string.affirm) { _, _ ->
                    block()
                    val directions =
                        SelectionDetailFragmentDirections.actionNavSelectionDetailToNavSelection()
                    findNavController().navigate(directions)
                }.setNegativeButton(R.string.cancel, null)
                .show()
        }
    }
}
