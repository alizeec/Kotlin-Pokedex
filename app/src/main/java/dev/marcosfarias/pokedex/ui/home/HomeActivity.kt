package dev.marcosfarias.pokedex.ui.home

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.model.Menu
import dev.marcosfarias.pokedex.model.News
import dev.marcosfarias.pokedex.ui.NavigableActivity
import dev.marcosfarias.pokedex.utils.PokemonColorUtil
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : NavigableActivity() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        window?.statusBarColor = PokemonColorUtil(this).convertColor(R.color.red)

        val recyclerViewMenu = recyclerViewMenu
        val recyclerViewNews = recyclerViewNews

        recyclerViewMenu.layoutManager = GridLayoutManager(this, 2)

        recyclerViewNews.layoutManager = GridLayoutManager(this, 1)
        recyclerViewNews.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        homeViewModel.getListMenu().observe(this, Observer {
            val items: List<Menu> = it
            recyclerViewMenu.adapter = MenuAdapter(items, this)
        })

        homeViewModel.getListNews().observe(this, Observer {
            val items: List<News> = it
            recyclerViewNews.adapter = NewsAdapter(items, this)
        })
    }
}
