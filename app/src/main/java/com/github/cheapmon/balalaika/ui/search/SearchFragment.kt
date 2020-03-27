package com.github.cheapmon.balalaika.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.Lexeme
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import com.github.cheapmon.balalaika.databinding.FragmentSearchBinding
import com.github.cheapmon.balalaika.util.InjectorUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels {
        InjectorUtil.provideSearchViewModelFactory(requireContext())
    }

    private val args: SearchFragmentArgs by navArgs()

    private lateinit var binding: FragmentSearchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        searchAdapter = SearchAdapter(Listener())
        with(binding) {
            recyclerView = searchList.apply {
                layoutManager = LinearLayoutManager(this@SearchFragment.context)
                adapter = searchAdapter
                setHasFixedSize(true)
            }
            searchInput.addTextChangedListener(Watcher())
            searchRestriction.setOnCloseIconClickListener {
                viewModel.clearRestriction()
                searchRestriction.visibility = View.GONE
            }
            inProgress = true
        }
        handleArgs()
        bindUi()
        return binding.root
    }

    private fun handleArgs() {
        val query = args.query
        val restriction = args.restriction
        if (query != null) {
            viewModel.setQuery(query)
        }
        if (restriction != null) viewModel.setRestriction(restriction)
    }

    private fun bindUi() {
        viewModel.lexemes.observe(viewLifecycleOwner, Observer {
            searchAdapter.submitList(it)
            binding.inProgress = false
            if (it.isEmpty()) {
                binding.searchEmptyIcon.visibility = View.VISIBLE
                binding.searchEmptyText.visibility = View.VISIBLE
            } else {
                binding.searchEmptyIcon.visibility = View.GONE
                binding.searchEmptyText.visibility = View.GONE
            }
        })
        viewModel.query.observe(viewLifecycleOwner, Observer {
            binding.query = it
        })
        viewModel.restriction.observe(viewLifecycleOwner, Observer {
            when (it) {
                is SearchRestriction.None -> {
                    binding.restriction = ""
                    binding.searchRestriction.visibility = View.GONE
                }
                is SearchRestriction.Some -> {
                    binding.restriction = getString(
                        R.string.search_restriction,
                        it.category.name, it.restriction
                    )
                    binding.searchRestriction.visibility = View.VISIBLE
                }
            }
        })
    }

    inner class Listener : SearchAdapter.SearchAdapterListener {
        override fun onClickItem(lexeme: Lexeme) {
            viewModel.addToHistory()
            val directions = SearchFragmentDirections.actionNavSearchToNavHome(lexeme.externalId)
            findNavController().navigate(directions)
        }
    }

    inner class Watcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.inProgress = true
            viewModel.setQuery(s.toString().trim())
        }
    }
}