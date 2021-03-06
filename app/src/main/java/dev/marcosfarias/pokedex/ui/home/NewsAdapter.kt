package dev.marcosfarias.pokedex.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.model.News
import dev.marcosfarias.pokedex.routing.NEWS
import dev.marcosfarias.pokedex.routing.POKEDEX
import dev.marcosfarias.pokedex.routing.RouterSingletonHolder

class NewsAdapter(
    private val list: List<News>,
    private val context: Context
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(item: News) {
//            itemView.textViewName.text = item.title
            itemView.setOnClickListener {
                RouterSingletonHolder.getInstance().navigateTo(NEWS)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bindView(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
