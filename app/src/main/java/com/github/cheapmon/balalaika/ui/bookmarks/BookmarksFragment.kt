package com.github.cheapmon.balalaika.ui.bookmarks

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.Application
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.Lexeme
import com.github.cheapmon.balalaika.databinding.FragmentBookmarksBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class BookmarksFragment : Fragment(), BookmarksAdapter.Listener {
    @Inject
    lateinit var viewModelFactory: BookmarksViewModelFactory
    lateinit var viewModel: BookmarksViewModel

    private lateinit var binding: FragmentBookmarksBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookmarksLayoutManager: LinearLayoutManager
    private lateinit var bookmarksAdapter: BookmarksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmarks, container, false)
        bookmarksLayoutManager = LinearLayoutManager(context)
        bookmarksAdapter = BookmarksAdapter(this)
        recyclerView = binding.bookmarksList.apply {
            layoutManager = bookmarksLayoutManager
            adapter = bookmarksAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, bookmarksLayoutManager.orientation))
        }
        setHasOptionsMenu(true)
        submitData()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as Application).appComponent.inject(this)
        val model by viewModels<BookmarksViewModel> { viewModelFactory }
        viewModel = model
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bookmarks, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bookmarks_clear -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.bookmarks_clear_title)
                    .setPositiveButton(R.string.bookmarks_clear_affirm) { _, _ -> clearHistory() }
                    .setNegativeButton(R.string.bookmarks_clear_cancel, null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun submitData() {
        viewModel.lexemes.observe(viewLifecycleOwner, Observer {
            bookmarksAdapter.submitList(ArrayList(it))
            if (it.isEmpty()) {
                binding.bookmarksEmptyIcon.visibility = View.VISIBLE
                binding.bookmarksEmptyText.visibility = View.VISIBLE
            } else {
                binding.bookmarksEmptyIcon.visibility = View.GONE
                binding.bookmarksEmptyText.visibility = View.GONE
            }
        })
    }

    private fun clearHistory() {
        viewModel.clearBookmarks()
        Snackbar.make(binding.root, R.string.bookmarks_clear_done, Snackbar.LENGTH_SHORT).show()
    }

    override fun onClickDeleteButton(lexeme: Lexeme) {
        viewModel.removeBookmark(lexeme.lexemeId)
        Snackbar.make(binding.root, R.string.bookmarks_item_removed, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onClickRedoButton(lexeme: Lexeme) {
        val directions =
            BookmarksFragmentDirections.actionNavBookmarksToNavHome(lexeme.externalId)
        findNavController().navigate(directions)
    }
}
