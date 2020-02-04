package dev.marcosfarias.pokedex.routing

import android.content.Context
import com.example.navigation.DefaultNavigator
import com.example.navigation.DefaultRouter
import com.example.navigation.IRouter

/**
 * In this sample, the router is a singleton. In Aircall application we use Dagger to inject it
 * where we need, but the goal here was to show that no external library is required to use it.
 */
object RouterSingletonHolder {

    private lateinit var instance: IRouter

    fun getInstance() = instance

    fun initialize(context: Context) {
        instance =  DefaultRouter(PokedexRouteParser(), DefaultNavigator(context))
    }
}