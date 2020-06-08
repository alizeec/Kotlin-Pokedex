package dev.marcosfarias.pokedex.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.marcosfarias.pokedex.routing.RouterSingletonHolder
import dev.marcosfarias.pokedex.routing.THEME

class ProfileViewModel : ViewModel(), ThemeViewDelegate {
    private val router = RouterSingletonHolder.getInstance()
    val colorTheme = MutableLiveData<Int>()

    fun openTheme(){
        router.navigateTo(THEME)
    }

    override fun onThemeChanged(color: Int) {
        colorTheme.postValue(color)
    }
}