package dev.marcosfarias.pokedex.routing

import com.example.navigation.MissingExtraException
import com.example.navigation.WrongTypeExtraException
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class RouteParserExtraValidatorTest {

    private lateinit var routeParser: PokedexRouteParser

    @Before
    fun setup() {
        routeParser = PokedexRouteParser()
    }

    @Test
    fun checkIntExtra() {
        val extras: Map<String, Any>? = mapOf(EXTRA_NAME to INT_EXTRA_VALUE)

        extras.checkIntExtra(EXTRA_NAME, ROUTE_NAME)
    }

    @Test
    fun checkIntExtra_NullExtra() {
        val extras: Map<String, Any>? = null

        try {
            extras.checkIntExtra(EXTRA_NAME, ROUTE_NAME)
        } catch (e: MissingExtraException) {
            Assert.assertEquals(
                "the given destination $ROUTE_NAME require the extra $EXTRA_NAME",
                e.message
            )
        }
    }

    @Test
    fun checkIntExtra_MissingExtra() {
        val extras: Map<String, Any>? = emptyMap()

        try {
            extras.checkIntExtra(EXTRA_NAME, ROUTE_NAME)
        } catch (e: MissingExtraException) {
            Assert.assertEquals(
                "the given destination $ROUTE_NAME require the extra $EXTRA_NAME",
                e.message
            )
        }
    }

    @Test
    fun checkIntExtra_InvalidExtra() {
        val extras: Map<String, Any>? = mapOf(EXTRA_NAME to STRING_EXTRA_VALUE)

        try {
            extras.checkIntExtra(EXTRA_NAME, ROUTE_NAME)
        } catch (e: WrongTypeExtraException) {
            Assert.assertEquals(
                "The extra $EXTRA_NAME for the given destination $ROUTE_NAME should be a int",
                e.message
            )
        }
    }

    @Test
    fun checkStringExtra() {
        val extras: Map<String, Any>? = mapOf(EXTRA_NAME to STRING_EXTRA_VALUE)

        extras.checkStringExtra(EXTRA_NAME, ROUTE_NAME)
    }

    @Test
    fun checkStringExtra_NullExtra() {
        val extras: Map<String, Any>? = null

        try {
            extras.checkStringExtra(EXTRA_NAME, ROUTE_NAME)
        } catch (e: MissingExtraException) {
            Assert.assertEquals(
                "the given destination $ROUTE_NAME require the extra $EXTRA_NAME",
                e.message
            )
        }
    }

    @Test
    fun checkStringExtra_MissingExtra() {
        val extras: Map<String, Any>? = emptyMap()

        try {
            extras.checkStringExtra(EXTRA_NAME, ROUTE_NAME)
        } catch (e: MissingExtraException) {
            Assert.assertEquals(
                "the given destination $ROUTE_NAME require the extra $EXTRA_NAME",
                e.message
            )
        }
    }

    @Test
    fun checkStringExtra_InvalidExtra() {
        val extras: Map<String, Any>? = mapOf(EXTRA_NAME to INT_EXTRA_VALUE)

        try {
            extras.checkStringExtra(EXTRA_NAME, ROUTE_NAME)
        } catch (e: WrongTypeExtraException) {
            Assert.assertEquals(
                "The extra $EXTRA_NAME for the given destination $ROUTE_NAME should be a java.lang.String",
                e.message
            )
        }
    }

    private companion object {
        const val EXTRA_NAME = "foobar"
        const val ROUTE_NAME = "app://foobar.io"

        const val INT_EXTRA_VALUE = 22
        const val STRING_EXTRA_VALUE = "FOO_BAZ"
    }
}