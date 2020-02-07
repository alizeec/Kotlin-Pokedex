package dev.marcosfarias.pokedex.ui.newsdetail

import androidx.lifecycle.ViewModel
import com.example.navigation.ExternalAppType
import dev.marcosfarias.pokedex.routing.RouterSingletonHolder

class NewsDetailViewModel : ViewModel() {

    fun onShareClicked() {
        RouterSingletonHolder.getInstance().openExternalApplication(ExternalAppType.SHARE) {
            text = "https://www.pokemon.com/us/pokemon-news/pokemon-rumble-rush-arrives-soon-on-mobile/"
        }
    }
}