package dev.marcosfarias.pokedex.routing

import android.content.Context
import com.example.navigation.DefaultNavigator
import com.example.navigation.DefaultRouter
import com.example.navigation.IRouter

object RouterSingletonHolder {

    private lateinit var instance: IRouter

    fun getInstance() = instance

    fun initialize(context: Context) {
        instance =  DefaultRouter(PokedexRouteParser(), DefaultNavigator(context))
    }
}