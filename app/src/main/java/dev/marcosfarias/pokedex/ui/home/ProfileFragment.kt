package dev.marcosfarias.pokedex.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dev.marcosfarias.pokedex.R
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.changeThemeButton

class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val themeColor =
            requireActivity().getSharedPreferences("preferences.theme", Context.MODE_PRIVATE).getInt("color", R.color.lightTeal)
        background.setBackgroundColor(ContextCompat.getColor(requireContext(), themeColor))

        changeThemeButton.setOnClickListener {
            profileViewModel.openTheme()
        }

        profileViewModel.colorTheme.observe(viewLifecycleOwner, Observer {
            background.setBackgroundColor(ContextCompat.getColor(requireContext(), it))
        })
    }
}