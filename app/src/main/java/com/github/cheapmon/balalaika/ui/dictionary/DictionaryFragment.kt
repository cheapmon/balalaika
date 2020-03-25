package com.github.cheapmon.balalaika.ui.dictionary

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.DictionaryEntry
import com.github.cheapmon.balalaika.databinding.FragmentDictionaryBinding
import com.github.cheapmon.balalaika.util.InjectorUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class DictionaryFragment : Fragment() {
    private val viewModel: DictionaryViewModel by viewModels {
        InjectorUtil.provideDictionaryViewModelFactory(requireContext())
    }

    private lateinit var binding: FragmentDictionaryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dictionaryAdapter: DictionaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dictionary, container, false)
        dictionaryAdapter = DictionaryAdapter(Listener())
        recyclerView = binding.entryList.apply {
            layoutManager = LinearLayoutManager(this@DictionaryFragment.context)
            adapter = dictionaryAdapter
            setHasFixedSize(true)
        }
        binding.inProgress = true
        bindUi()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dictionary, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.go_to_search -> {
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

    inner class Listener : DictionaryAdapter.DictionaryAdapterListener {
        override fun onClickBookmarkButton(dictionaryEntry: DictionaryEntry) {
            Snackbar.make(binding.root, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
        }
    }
}
