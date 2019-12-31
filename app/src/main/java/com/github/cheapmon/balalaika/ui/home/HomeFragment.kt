package com.github.cheapmon.balalaika.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.LemmaProperty
import com.github.cheapmon.balalaika.db.Lexeme
import com.github.cheapmon.balalaika.ui.Widget
import kotlinx.coroutines.CoroutineScope
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
                val db = BalalaikaDatabase.connect(view.context)
                db.lexemeDao().getAll().map {
                    it to db.lemmaPropertyDao().findByLexeme(it.lexeme)
                            .filter { property -> property.value != null }
                }
            }
            viewManager = LinearLayoutManager(view.context)
            viewAdapter = HomeAdapter(viewLifecycleOwner.lifecycleScope, lexemes)
            recyclerView = view.findViewById<RecyclerView>(R.id.home).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }
        return view
    }

    class HomeAdapter(private val scope: CoroutineScope, private val data: List<Pair<Lexeme, List<LemmaProperty>>>) :
            RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
        class HomeViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): HomeViewHolder {
            val cardView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.lexeme_card, parent, false) as CardView
            return HomeViewHolder(cardView)
        }

        override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
            val container = holder.cardView.findViewById<LinearLayoutCompat>(R.id.container)
            val (lexeme, values) = data[position]
            scope.launch {
                for (value in values) {
                    container.addView(Widget.get(container, lexeme, value))
                }
            }
        }

        override fun getItemCount() = data.size
    }
}
