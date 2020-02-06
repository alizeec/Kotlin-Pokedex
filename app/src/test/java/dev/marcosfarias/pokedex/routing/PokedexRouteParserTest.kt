package dev.marcosfarias.pokedex.routing

import androidx.core.os.bundleOf
import com.example.navigation.MissingExtraException
import com.example.navigation.ModalRoute
import com.example.navigation.Route
import com.example.navigation.ScreenRoute
import com.example.navigation.Transition
import com.example.navigation.WrongTypeExtraException
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.ui.dashboard.DashboardActivity
import dev.marcosfarias.pokedex.ui.dashboard.about.AboutFragment
import dev.marcosfarias.pokedex.ui.dashboard.evolution.EvolutionFragment
import dev.marcosfarias.pokedex.ui.dashboard.moves.MovesFragment
import dev.marcosfarias.pokedex.ui.dashboard.stats.StatsFragment
import dev.marcosfarias.pokedex.ui.generation.GenerationModal
import dev.marcosfarias.pokedex.ui.newsdetail.NewsDetailModal
import dev.marcosfarias.pokedex.ui.pokedex.PokedexActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PokedexRouteParserTest(
    private val type: RouteType,
    private val destination: DestinationWrapper,
    private val expectedRoute: Route?,
    private val expectedExceptionType: Class<out Exception>?
) {

    private val routeParser = PokedexRouteParser()

    @JvmField
    @Rule
    val thrown = ExpectedException.none()

    @Test
    fun parse() {
        expectedExceptionType?.let {
            thrown.expect(it)
        }

        val actual = routeParser.parse(destination.destination, destination.extras)

        when (type) {
            RouteType.SCREEN -> parseScreen(actual as? ScreenRoute?, expectedRoute as? ScreenRoute?)
            RouteType.MODAL -> parseModal(actual as? ModalRoute?, expectedRoute as? ModalRoute?)
        }
    }

    fun parseScreen(actual: ScreenRoute?, expected: ScreenRoute?) {
        assertEquals(expected?.activityType, actual?.activityType)
        assertEquals(expected?.fragmentType, actual?.fragmentType)
    }

    fun parseModal(actual: ModalRoute?, expected: ModalRoute?) {
        assertEquals(expected?.modalHostType, actual?.modalHostType)
        assertEquals(expected?.modalContentType, actual?.modalContentType)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<Array<out Any?>> {
            return listOf(
                //ABOUT
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(ABOUT, mapOf("id" to "123")),
                    ScreenRoute(
                        DashboardActivity::class.java,
                        AboutFragment::class.java,
                        bundleOf("id" to "123"),
                        null,
                        Transition(
                            Pair(R.anim.slide_in_left, R.anim.slide_out_left),
                            Pair(R.anim.slide_in_right, R.anim.slide_out_right)
                        )
                    ),
                    null
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(ABOUT, null),
                    null,
                    MissingExtraException::class.java
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(ABOUT, mapOf("id" to 123)),
                    null,
                    WrongTypeExtraException::class.java
                ),
                //EVOLUTION
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(EVOLUTION, mapOf("id" to "123")),
                    ScreenRoute(
                        DashboardActivity::class.java,
                        EvolutionFragment::class.java,
                        bundleOf("id" to "123"),
                        null,
                        Transition(
                            Pair(R.anim.slide_in_left, R.anim.slide_out_left),
                            Pair(R.anim.slide_in_right, R.anim.slide_out_right)
                        )
                    ),
                    null
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(EVOLUTION, null),
                    null,
                    MissingExtraException::class.java
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(EVOLUTION, mapOf("id" to 123)),
                    null,
                    WrongTypeExtraException::class.java
                ),
                //MOVES
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(MOVES, mapOf("id" to "123")),
                    ScreenRoute(
                        DashboardActivity::class.java,
                        MovesFragment::class.java,
                        bundleOf("id" to "123"),
                        null,
                        Transition(
                            Pair(R.anim.slide_in_left, R.anim.slide_out_left),
                            Pair(R.anim.slide_in_right, R.anim.slide_out_right)
                        )
                    ),
                    null
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(MOVES, null),
                    null,
                    MissingExtraException::class.java
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(MOVES, mapOf("id" to 123)),
                    null,
                    WrongTypeExtraException::class.java
                ),
                //STATS
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(STATS, mapOf("id" to "123")),
                    ScreenRoute(
                        DashboardActivity::class.java,
                        StatsFragment::class.java,
                        bundleOf("id" to "123"),
                        null,
                        Transition(
                            Pair(R.anim.slide_in_left, R.anim.slide_out_left),
                            Pair(R.anim.slide_in_right, R.anim.slide_out_right)
                        )
                    ),
                    null
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(STATS, null),
                    null,
                    MissingExtraException::class.java
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(STATS, mapOf("id" to 123)),
                    null,
                    WrongTypeExtraException::class.java
                ),
                //DASHBOARD
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(DASHBOARD, mapOf("id" to "123")),
                    ScreenRoute(
                        DashboardActivity::class.java,
                        AboutFragment::class.java,
                        bundleOf("id" to "123"),
                        null,
                        Transition(
                            Pair(R.anim.slide_in_left, R.anim.slide_out_left),
                            Pair(R.anim.slide_in_right, R.anim.slide_out_right)
                        )
                    ),
                    null
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(DASHBOARD, null),
                    null,
                    MissingExtraException::class.java
                ),
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(DASHBOARD, mapOf("id" to 123)),
                    null,
                    WrongTypeExtraException::class.java
                ),

                //POKEDEX
                expectedData(
                    RouteType.SCREEN,
                    DestinationWrapper(POKEDEX, null),
                    ScreenRoute(
                        PokedexActivity::class.java,
                        null,
                       null,
                        Transition(
                            Pair(R.anim.activity_start_enter_anim, R.anim.activity_start_exit_anim),
                            Pair(R.anim.activity_back_enter_anim, R.anim.activity_back_exit_anim)
                        ),
                        null
                    ),
                    null
                ),
                //NEWS
                expectedData(
                    RouteType.MODAL,
                    DestinationWrapper(NEWS, null),
                    ModalRoute(NewsDetailModal::class.java, null, null, null),
                    null
                ),
                //GENERATION
                expectedData(
                    RouteType.MODAL,
                    DestinationWrapper(GENERATION, null),
                    ModalRoute(GenerationModal::class.java, null, null, null),
                    null
                )
            )

        }
    }
}

data class DestinationWrapper(val destination: String, val extras: Map<String, Any>?)

fun expectedData(
    type: RouteType,
    destination: DestinationWrapper,
    expectedRoute: Route?,
    expectedExceptionType: Class<out Exception>?
) = arrayOf(type, destination, expectedRoute, expectedExceptionType)

enum class RouteType {
    SCREEN,
    MODAL
}