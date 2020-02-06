package dev.marcosfarias.pokedex.ui.home

import androidx.annotation.ColorRes
import com.example.navigation.ViewDelegate

interface ThemeViewDelegate: ViewDelegate {
    fun onThemeChanged(@ColorRes color: Int)
}