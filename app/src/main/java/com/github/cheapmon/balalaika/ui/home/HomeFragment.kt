package com.github.cheapmon.balalaika.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.Lexeme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            val lexemes = withContext(Dispatchers.Default) {
                BalalaikaDatabase.connect(view.context).lexemeDao().getAll()
            }
            viewManager = LinearLayoutManager(view.context)
            viewAdapter = HomeAdapter(lexemes)
            recyclerView = view.findViewById<RecyclerView>(R.id.home).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }
        return view
    }

    class HomeAdapter(private val data: List<Lexeme>) :
            RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
        class HomeViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): HomeViewHolder {
            val cardView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.lexeme_card, parent, false) as CardView
            return HomeViewHolder(cardView)
        }

        override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
            holder.cardView.findViewById<TextView>(R.id.title).text = data[position].lexeme
        }

        override fun getItemCount() = data.size
    }
}
