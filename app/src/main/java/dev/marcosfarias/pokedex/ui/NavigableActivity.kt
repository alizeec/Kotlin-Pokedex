package dev.marcosfarias.pokedex.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.example.navigation.IRouter
import dev.marcosfarias.pokedex.routing.RouterSingletonHolder

open class NavigableActivity : AppCompatActivity(){
    var router: IRouter = RouterSingletonHolder.getInstance()

    override fun onRestart() {
        super.onRestart()
        router.bindActivity(this, getFragmentHostId())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        router.bindActivity(this, getFragmentHostId())
    }

    @IdRes
    open fun getFragmentHostId(): Int? = null
}