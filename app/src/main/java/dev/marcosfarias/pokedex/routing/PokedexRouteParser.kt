package dev.marcosfarias.pokedex.routing

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import com.example.navigation.IRouteParser
import com.example.navigation.Transition
import com.example.navigation.activityRoute
import com.example.navigation.fragmentRoute
import com.example.navigation.missingExtra
import com.example.navigation.modalRoute
import com.example.navigation.wrongTypeExtra
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.ui.dashboard.DashboardActivity
import dev.marcosfarias.pokedex.ui.dashboard.about.AboutFragment
import dev.marcosfarias.pokedex.ui.dashboard.evolution.EvolutionFragment
import dev.marcosfarias.pokedex.ui.dashboard.moves.MovesFragment
import dev.marcosfarias.pokedex.ui.dashboard.stats.StatsFragment
import dev.marcosfarias.pokedex.ui.generation.GenerationModal
import dev.marcosfarias.pokedex.ui.newsdetail.NewsDetailModal
import dev.marcosfarias.pokedex.ui.pokedex.PokedexActivity

class PokedexRouteParser : IRouteParser {

    override fun parse(destination: String, extras: Map<String, Any>?) = when (destination) {
        ABOUT -> {
            extras.checkStringExtra("id", ABOUT)
            fragmentRoute<DashboardActivity, AboutFragment>(
                extras,
                fragmentTransition = createDefaultFragmentTransition()
            )
        }
        EVOLUTION -> {
            extras.checkStringExtra("id", EVOLUTION)
            fragmentRoute<DashboardActivity, EvolutionFragment>(
                extras,
                fragmentTransition = createDefaultFragmentTransition()
            )
        }
        MOVES -> {
            extras.checkStringExtra("id", MOVES)
            fragmentRoute<DashboardActivity, MovesFragment>(
                extras,
                fragmentTransition = createDefaultFragmentTransition()
            )
        }
        STATS -> {
            extras.checkStringExtra("id", STATS)
            fragmentRoute<DashboardActivity, StatsFragment>(
                extras,
                fragmentTransition = createDefaultFragmentTransition()
            )
        }
        DASHBOARD -> {
            extras.checkStringExtra("id", DASHBOARD)
            fragmentRoute<DashboardActivity, AboutFragment>(
                extras,
                fragmentTransition = createDefaultFragmentTransition()
            )
        }
        POKEDEX -> {
            activityRoute<PokedexActivity>(
                extras,
                fragmentTransition = createDefaultActivityTransition()
            )
        }
        NEWS -> {
            modalRoute<NewsDetailModal>(extras)
        }
        GENERATION -> {
            modalRoute<GenerationModal>(extras)
        }

        else -> error("Unknown route")
    }

    private fun createDefaultActivityTransition() = Transition(
        Pair(R.anim.activity_start_enter_anim, R.anim.activity_start_exit_anim),
        Pair(R.anim.activity_back_enter_anim, R.anim.activity_back_exit_anim)
    )

    private fun createDefaultFragmentTransition() = Transition(
        Pair(R.anim.slide_in_left, R.anim.slide_out_left),
        Pair(R.anim.slide_in_right, R.anim.slide_out_right)
    )
}

@VisibleForTesting
fun Map<String, Any>?.checkIntExtra(extraName: String, route: String) {
    if (this == null) missingExtra(route, extraName)

    if (!containsKey(extraName)) {
        missingExtra(route, extraName)
    }
    if (get(extraName) !is Int) {
        wrongTypeExtra(route, extraName, Int::class.java.name)
    }
}

@VisibleForTesting
fun Map<String, Any>?.checkIntArrayExtra(extraName: String, route: String) {
    if (this == null) missingExtra(route, extraName)

    if (!containsKey(extraName)) {
        missingExtra(route, extraName)
    }
    if (get(extraName) !is IntArray) {
        wrongTypeExtra(route, extraName, Int::class.java.name)
    }
}

@VisibleForTesting
fun Map<String, Any>?.checkStringExtra(extraName: String, route: String) {
    if (this == null) missingExtra(route, extraName)

    if (!containsKey(extraName)) {
        missingExtra(route, extraName)
    }
    if (get(extraName) !is String) {
        wrongTypeExtra(route, extraName, String::class.java.name)
    }
}

@VisibleForTesting
fun Map<String, Any>?.checkParcelableExtra(extraName: String, route: String) {
    if (this == null) missingExtra(route, extraName)

    if (!containsKey(extraName)) {
        missingExtra(route, extraName)
    }
    if (get(extraName) !is Parcelable) {
        wrongTypeExtra(route, extraName, String::class.java.name)
    }
}