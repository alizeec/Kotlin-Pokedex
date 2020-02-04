package dev.marcosfarias.pokedex.ui.pokedex

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.leinardi.android.speeddial.SpeedDialView
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.model.Pokemon
import dev.marcosfarias.pokedex.ui.NavigableActivity
import dev.marcosfarias.pokedex.ui.search.SearchFragment
import dev.marcosfarias.pokedex.utils.PokemonColorUtil
import kotlinx.android.synthetic.main.fragment_pokedex.*

class PokedexActivity : NavigableActivity() {

    private lateinit var pokedexViewModel: PokedexViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_pokedex)
        pokedexViewModel = ViewModelProviders.of(this).get(PokedexViewModel::class.java)

        window?.statusBarColor =
            PokemonColorUtil(this).convertColor(R.color.background)

        val progressBar = progressBar
        val recyclerView = recyclerView
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager

        pokedexViewModel.getListPokemon().observe(this, Observer {
            val pokemons: List<Pokemon> = it
            recyclerView.adapter = PokemonAdapter(pokemons, this)
            if (pokemons.isNotEmpty())
                progressBar.visibility = View.GONE
        })

        val speedDialView = speedDial
        speedDialView.inflate(R.menu.menu_pokedex)
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.menuAllGen -> {
                    showAllGen()
                    speedDialView.close()
                    return@OnActionSelectedListener true
                }
                R.id.menuSearch -> {
                    showSearch()
                    speedDialView.close()
                    return@OnActionSelectedListener true
                }
                else -> {
                    speedDialView.close()
                    return@OnActionSelectedListener true
                }
            }
        })
    }

    private fun showAllGen() {
        pokedexViewModel.openAllGenModal()
    }

    private fun showSearch() {
        val dialog = SearchFragment()
        dialog.show(supportFragmentManager, "")
    }

}
