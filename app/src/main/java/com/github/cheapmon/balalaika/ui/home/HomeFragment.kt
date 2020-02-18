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
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.DictionaryEntry
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.ui.widgets.Widgets
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.*

class HomeFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    val args: HomeFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSearchInput()
    }

    override fun onResume() {
        super.onResume()
        hideSearchInput()
    }

    private fun hideSearchInput() {
        activity?.search_input?.visibility = View.GONE
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.setView(PreferenceManager.getDefaultSharedPreferences(view.context).getString("default_view", "all")
                ?: "all")
        recyclerView = view.findViewById<RecyclerView>(R.id.home)
        val adapter = HomeAdapter(viewLifecycleOwner.lifecycleScope, recyclerView, fragmentManager)
        viewModel.lexemes.observe(this, Observer {
            adapter.submitList(it)
        })
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(view.context)
        recyclerView?.setHasFixedSize(true)
        checkArgs(viewModel.lexemes.value)
        return view
    }

    private fun checkArgs(list: PagedList<DictionaryEntry>?) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val id = args.fullFormId
            if (id != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(1000)
                    val pos = withContext(Dispatchers.IO) {
                        val form = BalalaikaDatabase.instance.fullFormDao().getById(id)?.fullForm
                                ?: ""
                        BalalaikaDatabase.instance.fullFormDao().getPositionOf(form)
                    }
                    list?.loadAround(pos)
                    recyclerView?.scrollToPosition(pos)
                }
            }
        }
    }

    class HomeAdapter(
            private val scope: CoroutineScope,
            val recyclerView: RecyclerView?,
            private val fragmentManager: FragmentManager?
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
                        val widget = Widgets.get(fragmentManager, this, scope, container, line)
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
