package dev.marcosfarias.pokedex.ui.dashboard

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.ui.NavigableActivity
import dev.marcosfarias.pokedex.utils.PokemonColorUtil
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardActivity : NavigableActivity() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        setContentView(R.layout.fragment_dashboard)

        val id = checkNotNull(intent.extras?.getString("id"))

        dashboardViewModel.getPokemonById(id).observe(this, Observer { pokemonValue ->
            pokemonValue?.let { pokemon ->
                textViewID.text = pokemon.id
                textViewName.text = pokemon.name

                val color =
                    PokemonColorUtil(this).getPokemonColor(pokemon.typeofpokemon)
                app_bar.background.colorFilter =
                    PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                toolbar_layout.contentScrim?.colorFilter =
                    PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                window?.statusBarColor =
                    PokemonColorUtil(this).getPokemonColor(pokemon.typeofpokemon)

                pokemon.typeofpokemon?.getOrNull(0).let { firstType ->
                    textViewType3.text = firstType
                    textViewType3.isVisible = firstType != null
                }

                pokemon.typeofpokemon?.getOrNull(1).let { secondType ->
                    textViewType2.text = secondType
                    textViewType2.isVisible = secondType != null
                }

                pokemon.typeofpokemon?.getOrNull(2).let { thirdType ->
                    textViewType1.text = thirdType
                    textViewType1.isVisible = thirdType != null
                }

                Glide.with(this)
                    .load(pokemon.imageurl)
                    .placeholder(android.R.color.transparent)
                    .into(imageView)

                about_tab.setOnClickListener {
                    dashboardViewModel.navigateToAbout(pokemon.id!!)
                    about_tab.setTextAppearance(R.style.TabAppearanceClicked)
                    stats_tab.setTextAppearance(R.style.TabAppearance)
                    evolution_tab.setTextAppearance(R.style.TabAppearance)
                    moves_tab.setTextAppearance(R.style.TabAppearance)
                }

                stats_tab.setOnClickListener {
                     dashboardViewModel.navigateToStats(pokemon.id!!)
                    stats_tab.setTextAppearance(R.style.TabAppearanceClicked)
                    about_tab.setTextAppearance(R.style.TabAppearance)
                    evolution_tab.setTextAppearance(R.style.TabAppearance)
                    moves_tab.setTextAppearance(R.style.TabAppearance)
                }

                evolution_tab.setOnClickListener {
                    dashboardViewModel.navigateToEvolution(pokemon.id!!)
                    evolution_tab.setTextAppearance(R.style.TabAppearanceClicked)
                    about_tab.setTextAppearance(R.style.TabAppearance)
                    stats_tab.setTextAppearance(R.style.TabAppearance)
                    moves_tab.setTextAppearance(R.style.TabAppearance)
                }

                moves_tab.setOnClickListener {
                    dashboardViewModel.navigateToMoves(pokemon.id!!)
                    moves_tab.setTextAppearance(R.style.TabAppearanceClicked)
                    about_tab.setTextAppearance(R.style.TabAppearance)
                    evolution_tab.setTextAppearance(R.style.TabAppearance)
                    evolution_tab.setTextAppearance(R.style.TabAppearance)
                }
            }
        })
    }

    override fun getFragmentHostId() = R.id.navHostFragment

}
