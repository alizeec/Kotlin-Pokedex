package dev.marcosfarias.pokedex.ui.home

import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModel
import dev.marcosfarias.pokedex.routing.RouterSingletonHolder

class ThemeViewModel : ViewModel() {
    private val router = RouterSingletonHolder.getInstance()

    fun onThemeUpdated(@ColorRes color: Int) {
        val router = RouterSingletonHolder.getInstance()
        router.findViewDelegateByType<ThemeViewDelegate> { it.onThemeChanged(color) }
        router.navigateBack()
    }

    fun clickOnNavigateBack() {
        router.navigateBack()
    }
}
