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
package com.github.cheapmon.balalaika.ui.dictionary

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.LoadState
import com.github.cheapmon.balalaika.data.Result
import com.github.cheapmon.balalaika.data.dictionary.wordnet.WordnetInfo
import com.github.cheapmon.balalaika.databinding.DialogWordnetBinding
import com.github.cheapmon.balalaika.databinding.DialogWordnetItemDefinitionBinding
import com.github.cheapmon.balalaika.databinding.DialogWordnetItemExampleBinding
import com.github.cheapmon.balalaika.databinding.DialogWordnetItemTitleBinding
import com.github.cheapmon.balalaika.databinding.DialogWordnetItemWordBinding
import com.github.cheapmon.balalaika.util.exhaustive
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/** Dialog for displaying Wordnet data for a word */
class WordnetDialog(
    private val word: String,
    private val payload: Flow<LoadState<WordnetInfo, Throwable>>
) : DialogFragment() {
    private lateinit var binding: DialogWordnetBinding

    /** Create dialog and bind data */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        requireActivity().let {
            val itemAdapter = Adapter(
                wordsTitle = requireContext().getString(R.string.dictionary_wordnet_words),
                definitionsTitle = requireContext().getString(R.string.dictionary_wordnet_definitions),
                emptyMessage = requireContext().getString(R.string.dictionary_wordnet_empty),
                errorMessage = requireContext().getString(R.string.selection_error_internal)
            )
            binding = DialogWordnetBinding.inflate(it.layoutInflater)
            binding.dialogWordnetList.apply {
                adapter = itemAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }
            lifecycleScope.launchWhenCreated {
                payload.collect { loadState ->
                    binding.inProgress = loadState !is LoadState.Finished
                    if (loadState is LoadState.Finished) {
                        when (loadState.data) {
                            is Result.Success -> itemAdapter.submitInfo(loadState.data.data)
                            is Result.Error -> itemAdapter.submitError()
                        }.exhaustive
                    }
                }
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.dictionary_wordnet_title, word))
                .setView(binding.root)
                .setPositiveButton(R.string.affirm, null)
                .create()
        }

    private sealed class Item {
        data class TitleItem(val title: String) : Item()
        data class WordItem(val entry: WordnetInfo.LexicalEntry) : Item()
        data class DefinitionItem(val definition: String) : Item()
        data class ExampleItem(val example: String) : Item()
        object Space : Item()
    }

    private class Adapter(
        private val wordsTitle: String,
        private val definitionsTitle: String,
        private val emptyMessage: String,
        private val errorMessage: String
    ) : ListAdapter<Item, ViewHolder>(Diff) {
        fun submitInfo(info: WordnetInfo) {
            val list = mutableListOf<Item>()
            info.entries.let {
                if (it.isNotEmpty()) {
                    list.add(Item.TitleItem(wordsTitle))
                    list.addAll(it.map { entry -> Item.WordItem(entry) })
                    list.addAll(arrayOf(Item.Space, Item.Space, Item.Space))
                }
            }
            info.definitions.let {
                if (it.isNotEmpty()) {
                    list.add(Item.TitleItem(definitionsTitle))
                    it.forEach { definition ->
                        list.add(Item.DefinitionItem(definition.explanation))
                        list.addAll(definition.examples.map { ex -> Item.ExampleItem(ex) })
                        list.add(Item.Space)
                    }
                }
            }
            if (list.isEmpty()) list.add(Item.TitleItem(emptyMessage))
            submitList(list)
        }

        fun submitError() = submitList(listOf(Item.TitleItem(errorMessage)))

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                TYPE_TITLE -> ViewHolder.TitleViewHolder(
                    DialogWordnetItemTitleBinding.inflate(layoutInflater)
                )
                TYPE_WORD -> ViewHolder.WordViewHolder(
                    DialogWordnetItemWordBinding.inflate(layoutInflater)
                )
                TYPE_DEFINITION -> ViewHolder.DefinitionViewHolder(
                    DialogWordnetItemDefinitionBinding.inflate(layoutInflater)
                )
                TYPE_EXAMPLE -> ViewHolder.ExampleViewHolder(
                    DialogWordnetItemExampleBinding.inflate(layoutInflater)
                )
                TYPE_SPACE -> ViewHolder.SpaceViewHolder(
                    layoutInflater.inflate(R.layout.dialog_wordnet_item_space, parent, false)
                )
                else -> throw IllegalArgumentException("Unsupported view type")
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            holder.bind(item)
        }

        override fun getItemViewType(position: Int): Int {
            return when (getItem(position)) {
                is Item.TitleItem -> TYPE_TITLE
                is Item.WordItem -> TYPE_WORD
                is Item.DefinitionItem -> TYPE_DEFINITION
                is Item.ExampleItem -> TYPE_EXAMPLE
                is Item.Space -> TYPE_SPACE
            }
        }

        companion object {
            const val TYPE_TITLE = 1
            const val TYPE_WORD = 2
            const val TYPE_DEFINITION = 3
            const val TYPE_EXAMPLE = 4
            const val TYPE_SPACE = 5
        }

        object Diff : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
            override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
        }
    }

    private sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class TitleViewHolder(val binding: DialogWordnetItemTitleBinding) :
            ViewHolder(binding.root)

        class WordViewHolder(val binding: DialogWordnetItemWordBinding) :
            ViewHolder(binding.root)

        class DefinitionViewHolder(val binding: DialogWordnetItemDefinitionBinding) :
            ViewHolder(binding.root)

        class ExampleViewHolder(val binding: DialogWordnetItemExampleBinding) :
            ViewHolder(binding.root)

        class SpaceViewHolder(view: View) :
            ViewHolder(view)

        fun bind(item: Item) {
            when (item) {
                is Item.TitleItem -> bind(item)
                is Item.WordItem -> bind(item)
                is Item.DefinitionItem -> bind(item)
                is Item.ExampleItem -> bind(item)
                is Item.Space -> {
                }
            }.exhaustive
        }

        fun bind(item: Item.TitleItem) {
            if (this is TitleViewHolder) this.binding.title = item.title
        }

        fun bind(item: Item.WordItem) {
            if (this is WordViewHolder) this.binding.entry = item.entry
        }

        fun bind(item: Item.DefinitionItem) {
            if (this is DefinitionViewHolder) this.binding.definition = item.definition
        }

        fun bind(item: Item.ExampleItem) {
            if (this is ExampleViewHolder) this.binding.example = "\uA78F " + item.example
        }
    }
}
