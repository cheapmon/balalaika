package com.github.cheapmon.balalaika.ui.dictionary

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryBinding
import com.github.cheapmon.balalaika.ui.dictionary.widgets.WidgetListener
import com.github.cheapmon.balalaika.util.InjectorUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class DictionaryFragment : Fragment() {
    private val viewModel: DictionaryViewModel by viewModels {
        InjectorUtil.provideDictionaryViewModelFactory(requireContext())
    }

    private val args: DictionaryFragmentArgs by navArgs()

    private lateinit var binding: FragmentDictionaryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dictionaryLayoutManager: LinearLayoutManager
    private lateinit var dictionaryAdapter: DictionaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dictionary, container, false)
        dictionaryLayoutManager = LinearLayoutManager(context)
        dictionaryAdapter = DictionaryAdapter(Listener(), WListener())
        with(binding) {
            recyclerView = entryList.apply {
                layoutManager = dictionaryLayoutManager
                adapter = dictionaryAdapter
                setHasFixedSize(true)
            }
            inProgress = true
        }
        bindUi()
        handleArgs()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dictionary, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return when (item.itemId) {
            R.id.action_order_by -> {
                val comparators = viewModel.getComparators().toTypedArray()
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_sort)
                    .setTitle(R.string.menu_order_by)
                    .setNegativeButton(R.string.cancel, null)
                    .setItems(comparators) { _, which ->
                        preferences.edit(commit = true) {
                            val key = getString(R.string.preferences_key_order)
                            putString(key, comparators[which])
                        }
                        viewModel.setOrdering(comparators[which])
                        binding.inProgress = true
                    }.show()
                true
            }
            R.id.action_setup_view -> {
                lifecycleScope.launch {
                    val dictionaryViews = viewModel.getDictionaryViews()
                    val names = dictionaryViews.map { it.dictionaryView.name }.toTypedArray()
                    MaterialAlertDialogBuilder(requireContext())
                        .setIcon(R.drawable.ic_view)
                        .setTitle(R.string.menu_setup_view)
                        .setNegativeButton(R.string.cancel, null)
                        .setItems(names) { _, which ->
                            val id = dictionaryViews[which].dictionaryView.dictionaryViewId
                            preferences.edit(commit = true) {
                                val key = getString(R.string.preferences_key_view)
                                putString(key, id.toString())
                            }
                            viewModel.setDictionaryView(id)
                            binding.inProgress = true
                        }.show()
                }
                true
            }
            R.id.action_search -> {
                val directions = DictionaryFragmentDirections.actionNavHomeToNavSearch()
                findNavController().navigate(directions)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bindUi() {
        viewModel.lexemes.observe(viewLifecycleOwner, Observer {
            dictionaryAdapter.submitList(it)
            binding.inProgress = false
        })
    }

    private fun handleArgs() {
        val externalId = args.externalId
        if (externalId != null) {
            scrollTo(externalId)
        }
    }

    private fun scrollTo(externalId: String) {
        lifecycleScope.launch {
            val position = viewModel.getPositionOf(externalId)
            recyclerView.post {
                dictionaryLayoutManager.scrollToPositionWithOffset(position, 0)
            }
        }
    }

    inner class Listener : DictionaryAdapter.DictionaryAdapterListener {
        override fun onClickBookmarkButton(dictionaryEntry: DictionaryEntry) {
            viewModel.toggleBookmark(dictionaryEntry.lexeme.lexemeId)
            val message = if (dictionaryEntry.lexeme.isBookmark) {
                getString(R.string.dictionary_bookmark_remove, dictionaryEntry.lexeme.form)
            } else {
                getString(R.string.dictionary_bookmark_add, dictionaryEntry.lexeme.form)
            }
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }

        override fun onClickBaseButton(dictionaryEntry: DictionaryEntry) {
            if (dictionaryEntry.base != null) scrollTo(dictionaryEntry.base.externalId)
        }
    }

    inner class WListener : WidgetListener {
        override fun onClickAudioButton(resId: Int) {
            try {
                MediaPlayer.create(context, resId).apply {
                    start()
                    setOnCompletionListener { release() }
                }
            } catch (ex: Exception) {
                Snackbar.make(
                    binding.root,
                    R.string.dictionary_playback_failed,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        override fun onClickSearchButton(query: String, restriction: SearchRestriction) {
            val directions =
                DictionaryFragmentDirections.actionNavHomeToNavSearch(restriction, query)
            findNavController().navigate(directions)
        }

        override fun onClickScrollButton(externalId: String) {
            scrollTo(externalId)
        }

        override fun onClickLinkButton(link: String) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }
    }
}
