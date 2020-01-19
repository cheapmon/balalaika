package com.github.cheapmon.balalaika.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.ui.widgets.Widget
import kotlinx.coroutines.CoroutineScope

class HomeFragment : Fragment() {
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        recyclerView = view.findViewById<RecyclerView>(R.id.home)
        val adapter = HomeAdapter(viewLifecycleOwner.lifecycleScope, recyclerView, fragmentManager)
        viewModel.lexemes.observe(this, Observer {
            adapter.submitList(it)
        })
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(view.context)
        recyclerView?.setHasFixedSize(true)
        return view
    }

    class HomeAdapter(
            private val scope: CoroutineScope,
            val recyclerView: RecyclerView?,
            val fragmentManager: FragmentManager?
    ) : PagedListAdapter<DictionaryEntry, HomeAdapter.HomeViewHolder>(
            object : DiffUtil.ItemCallback<DictionaryEntry>() {
                override fun areContentsTheSame(oldItem: DictionaryEntry, newItem: DictionaryEntry): Boolean {
                    return oldItem.fullForm.fullForm == newItem.fullForm.fullForm
                }

                override fun areItemsTheSame(oldItem: DictionaryEntry, newItem: DictionaryEntry): Boolean {
                    return oldItem == newItem
                }
            }
    ) {
        class HomeViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

        override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
            val cardView = holder.cardView
            val container = cardView.findViewById<LinearLayoutCompat>(R.id.container)
            val entry: DictionaryEntry? = getItem(position)
            if (entry != null) {
                container.removeAllViews()
                for (line in entry.lines) {
                    if (line.properties.isNotEmpty()) {
                        val widget = Widget.get(fragmentManager, this, scope, container, line)
                        container.addView(widget)
                    }
                }
                cardView.minimumHeight = 0
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
            val cardView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.lexeme_card, parent, false) as CardView
            return HomeViewHolder(cardView)
        }
    }
}
