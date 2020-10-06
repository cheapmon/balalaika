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

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.github.cheapmon.balalaika.MainViewModel
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.InstallationMessage
import com.github.cheapmon.balalaika.data.result.ProgressState
import com.github.cheapmon.balalaika.databinding.DictionaryCardBinding
import com.github.cheapmon.balalaika.databinding.FragmentSelectionListBinding
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SimpleDictionary
import com.github.cheapmon.balalaika.model.sameAs
import com.github.cheapmon.balalaika.util.exhaustive
import com.github.cheapmon.balalaika.util.icon
import com.github.cheapmon.balalaika.util.setIconById
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectionListFragment(
    private val data: LiveData<List<SimpleDictionary>>,
    private val listener: SelectionFragment.Listener,
    private val refreshable: Boolean = false,
    private val refreshListener: SwipeRefreshLayout.OnRefreshListener? = null
) : Fragment() {
    private lateinit var binding: FragmentSelectionListBinding
    private lateinit var adapter: Adapter

    private val viewModel: SelectionViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = Adapter()
        binding = FragmentSelectionListBinding.inflate(inflater)
        binding.selectionList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SelectionListFragment.adapter
            setHasFixedSize(true)
        }
        binding.selectionRefresh.apply {
            isEnabled = refreshable
            setOnRefreshListener(refreshListener)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.refreshing.observe(viewLifecycleOwner) {
            binding.selectionRefresh.isRefreshing = it
        }
    }

    private inner class Adapter : ListAdapter<SimpleDictionary, ViewHolder>(Diff) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DictionaryCardBinding.inflate(inflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(getItem(position))
    }

    private inner class ViewHolder(private val binding: DictionaryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimpleDictionary) {
            binding.dictionary = item
            binding.installed = item is InstalledDictionary
            binding.opened = item is InstalledDictionary && item.isOpened
            binding.listener = this@SelectionListFragment.Listener()
            binding.body.visibility = View.GONE
            binding.collapseButton.setIconById(R.drawable.ic_expand)
            binding.root.setOnClickListener { onClick() }
            binding.collapseButton.setOnClickListener { onClick() }
        }

        private fun onClick() {
            when (binding.body.visibility) {
                View.GONE -> {
                    binding.collapseButton.setIconById(R.drawable.ic_expand)
                    when (val drawable = binding.collapseButton.icon) {
                        is AnimatedVectorDrawableCompat -> drawable.start()
                        is AnimatedVectorDrawable -> drawable.start()
                    }
                    animateExpand(binding.body)
                }
                else -> {
                    binding.collapseButton.setIconById(R.drawable.ic_collapse)
                    when (val drawable = binding.collapseButton.icon) {
                        is AnimatedVectorDrawableCompat -> drawable.start()
                        is AnimatedVectorDrawable -> drawable.start()
                    }
                    animateCollapse(binding.body)
                }
            }.exhaustive
        }
    }

    object Diff : DiffUtil.ItemCallback<SimpleDictionary>() {
        override fun areItemsTheSame(
            oldItem: SimpleDictionary,
            newItem: SimpleDictionary
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: SimpleDictionary,
            newItem: SimpleDictionary
        ): Boolean = oldItem sameAs newItem
    }

    private fun animateExpand(view: View) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val height = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height = if (interpolatedTime == 1f) {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    (height * interpolatedTime).toInt()
                }
                view.requestLayout()
            }
        }.apply {
            duration = (height / view.resources.displayMetrics.density).toLong()
            interpolator = LinearOutSlowInInterpolator()
            view.startAnimation(this)
        }
    }

    private fun animateCollapse(view: View) {
        val height = view.measuredHeight
        object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height = height - (height * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }
        }.apply {
            duration = height / view.resources.displayMetrics.density.toLong()
            interpolator = LinearOutSlowInInterpolator()
            view.startAnimation(this)
        }
    }

    inner class Listener {
        fun onOpenDictionary(dictionary: SimpleDictionary) {
            if (dictionary is InstalledDictionary) activityViewModel.openDictionary(dictionary)
        }

        fun onCloseDictionary(dictionary: SimpleDictionary) {
            if (dictionary is InstalledDictionary) activityViewModel.closeDictionary()
        }

        fun onAddDictionary(dictionary: SimpleDictionary) {
            if (dictionary is DownloadableDictionary)
                showProgress(activityViewModel.installDictionary(dictionary))
        }

        fun onRemoveDictionary(dictionary: SimpleDictionary) {
            if (dictionary is InstalledDictionary)
                showProgress(activityViewModel.removeDictionary(dictionary))
        }
    }

    private fun showProgress(data: Flow<ProgressState<Unit, InstallationMessage, Throwable>>) {
        activity?.lifecycleScope?.launch {
            data.collect { progress ->
                when (progress) {
                    is ProgressState.Init -> binding.selectionRefresh.isRefreshing = true
                    is ProgressState.InProgress -> Snackbar.make(
                        binding.root,
                        progress.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    is ProgressState.Finished -> {
                        binding.selectionRefresh.isRefreshing = false
                        Snackbar.make(
                            binding.root,
                            progress.data.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        listener.goToTab(0)
                    }
                }.exhaustive
            }
        }
    }
}
