package dev.marcosfarias.pokedex.ui.pokedex

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.marcosfarias.pokedex.App
import dev.marcosfarias.pokedex.database.dao.PokemonDAO
import dev.marcosfarias.pokedex.model.Pokemon
import dev.marcosfarias.pokedex.repository.APIService
import dev.marcosfarias.pokedex.routing.GENERATION
import dev.marcosfarias.pokedex.routing.RouterSingletonHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class PokedexViewModel : ViewModel() {

    private val pokemonDAO: PokemonDAO = App.database.pokemonDAO()
    private val router = RouterSingletonHolder.getInstance()

    init {
        initNetworkRequest()
    }

    private fun initNetworkRequest() {
        val call = APIService.pokemonService.get()
        call.enqueue(object : Callback<List<Pokemon>?> {
            override fun onResponse(
                call: Call<List<Pokemon>?>?,
                response: Response<List<Pokemon>?>?
            ) {
                response?.body()?.let { pokemons: List<Pokemon> ->
                    thread {
                        pokemonDAO.add(pokemons)
                    }
                }
            }

            override fun onFailure(call: Call<List<Pokemon>?>?, t: Throwable?) {
                // TODO handle failure
            }
        })

    }

    fun getListPokemon(): LiveData<List<Pokemon>> {
        return pokemonDAO.all()
    }

    fun openAllGenModal(){
        router.navigateTo(GENERATION)
    }

}
