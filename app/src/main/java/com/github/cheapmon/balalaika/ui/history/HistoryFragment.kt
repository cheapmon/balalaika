package com.github.cheapmon.balalaika.ui.history

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.Application
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.databinding.FragmentHistoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class HistoryFragment : Fragment() {
    @Inject
    lateinit var viewModel: HistoryViewModel

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyLayoutManager: LinearLayoutManager
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        historyLayoutManager = LinearLayoutManager(context)
        historyAdapter = HistoryAdapter(Listener())
        recyclerView = binding.historyList.apply {
            layoutManager = historyLayoutManager
            adapter = historyAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, historyLayoutManager.orientation))
        }
        setHasOptionsMenu(true)
        submitData()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as Application).appComponent.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history_clear -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.history_clear_title)
                    .setPositiveButton(R.string.history_clear_affirm) { _, _ -> clearHistory() }
                    .setNegativeButton(R.string.history_clear_cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun submitData() {
        viewModel.historyEntries.observe(viewLifecycleOwner, Observer {
            historyAdapter.submitList(ArrayList(it))
            if (it.isEmpty()) {
                binding.historyEmptyIcon.visibility = View.VISIBLE
                binding.historyEmptyText.visibility = View.VISIBLE
            } else {
                binding.historyEmptyIcon.visibility = View.GONE
                binding.historyEmptyText.visibility = View.GONE
            }
        })
    }

    private fun clearHistory() {
        viewModel.clearHistory()
        Snackbar.make(binding.root, R.string.history_clear_done, Snackbar.LENGTH_SHORT).show()
    }

    inner class Listener : HistoryAdapter.HistoryAdapterListener {
        override fun onClickDeleteButton(historyEntry: HistoryEntryWithRestriction) {
            viewModel.removeEntry(historyEntry.historyEntry)
            Snackbar.make(binding.root, R.string.history_entry_removed, Snackbar.LENGTH_SHORT)
                .show()
        }

        override fun onClickRedoButton(historyEntry: HistoryEntryWithRestriction) {
            val directions = HistoryFragmentDirections.actionNavHistoryToNavSearch(
                query = historyEntry.historyEntry.query,
                restriction = historyEntry.restriction
            )
            findNavController().navigate(directions)
        }
    }
}
