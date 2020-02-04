package dev.marcosfarias.pokedex.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.marcosfarias.pokedex.App
import dev.marcosfarias.pokedex.database.dao.PokemonDAO
import dev.marcosfarias.pokedex.model.Pokemon
import dev.marcosfarias.pokedex.routing.ABOUT
import dev.marcosfarias.pokedex.routing.EVOLUTION
import dev.marcosfarias.pokedex.routing.MOVES
import dev.marcosfarias.pokedex.routing.RouterSingletonHolder
import dev.marcosfarias.pokedex.routing.STATS

class DashboardViewModel : ViewModel() {

    private val pokemonDAO: PokemonDAO = App.database.pokemonDAO()
    val router = RouterSingletonHolder.getInstance()

    fun getPokemonById(id: String?): LiveData<Pokemon> {
        return pokemonDAO.getById(id)
    }

    fun navigateToAbout(id: String){
        router.navigateTo(ABOUT, mapOf("id" to id))
    }

    fun navigateToStats(id: String){
        router.navigateTo(STATS, mapOf("id" to id))
    }

    fun navigateToEvolution(id: String){
        router.navigateTo(EVOLUTION, mapOf("id" to id))
    }

    fun navigateToMoves(id: String){
        router.navigateTo(MOVES, mapOf("id" to id))
    }

}
