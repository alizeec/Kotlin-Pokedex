package dev.marcosfarias.pokedex.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import dev.marcosfarias.pokedex.R
import kotlinx.android.synthetic.main.theme_fragment.*

class ThemeFragment : Fragment() {

    private lateinit var themeViewModel: ThemeViewModel
    private val possibleThemes = mutableListOf(R.color.red, R.color.lightBlue, R.color.lightPurple, R.color.lightYellow)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeViewModel = ViewModelProviders.of(this).get(ThemeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.theme_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentThemeColor = context?.getSharedPreferences("preferences.theme", Context.MODE_PRIVATE)?.getInt("color", R.color.red) ?: R.color.red
        possibleThemes.remove(currentThemeColor)

        currentChoice.setBackgroundColor(ContextCompat.getColor(requireContext(), currentThemeColor))

        theme1Choice.setBackgroundColor(ContextCompat.getColor(requireContext(), possibleThemes[0]))
        theme1Choice.setOnClickListener {
            updateTheme(possibleThemes[0])
        }
        theme2Choice.setBackgroundColor(ContextCompat.getColor(requireContext(), possibleThemes[1]))
        theme2Choice.setOnClickListener {
            updateTheme(possibleThemes[1])
        }
        theme3Choice.setBackgroundColor(ContextCompat.getColor(requireContext(), possibleThemes[2]))
        theme3Choice.setOnClickListener {
            updateTheme(possibleThemes[2])
        }

        backButton.setOnClickListener {
            themeViewModel.clickOnNavigateBack()
        }
    }

    fun updateTheme(@ColorRes color: Int){
        requireContext().getSharedPreferences("preferences.theme", Context.MODE_PRIVATE).edit()
            .putInt("color", color)
            .apply()
        themeViewModel.onThemeUpdated(color)
    }

}