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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
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
    private val args: HomeFragmentArgs by navArgs()

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
        val preferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        viewModel.setView(preferences.getString("default_view", "all") ?: "all")
        viewModel.setCategory(preferences.getString("order_by", "default") ?: "default")
        recyclerView = view.findViewById<RecyclerView>(R.id.home)
        viewModel.lexemes.observe(this, Observer {
            val category = preferences.getString("order_by", "default") ?: "default"
            val adapter = HomeAdapter(viewLifecycleOwner.lifecycleScope, findNavController(), recyclerView, fragmentManager, category, it)
            adapter.submitList(it)
            recyclerView?.adapter = adapter
            recyclerView?.layoutManager = LinearLayoutManager(view.context)
            recyclerView?.setHasFixedSize(true)
            checkArgs()
        })
        return view
    }

    private fun checkArgs() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val id = args.fullFormId
            if (id != null) {
                (recyclerView?.adapter as? HomeAdapter)?.scrollToId(id)
            }
        }
    }

    class HomeAdapter(
            private val scope: CoroutineScope,
            private val navController: NavController,
            private val recyclerView: RecyclerView?,
            private val fragmentManager: FragmentManager?,
            private val category: String,
            private val list: PagedList<DictionaryEntry>
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
                        val widget = Widgets.get(fragmentManager, this, scope, navController, container, line)
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

        fun scrollToId(id: String) {
            scope.launch {
                delay(1000)
                val pos = withContext(Dispatchers.IO) {
                    if (category == "default") {
                        val form = BalalaikaDatabase.instance.fullFormDao().getById(id)?.fullForm
                                ?: ""
                        BalalaikaDatabase.instance.fullFormDao().getPositionOf(form)
                    } else {
                        BalalaikaDatabase.instance.fullFormDao().getIdsOrderedBy(category).indexOf(id)
                    }
                }
                list.loadAround(pos)
                recyclerView?.scrollToPosition(pos)
            }
        }
    }
}
