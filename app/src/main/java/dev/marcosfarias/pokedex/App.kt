package dev.marcosfarias.pokedex

import android.app.Application
import android.content.Context
import androidx.room.Room
import dev.marcosfarias.pokedex.database.AppDatabase
import dev.marcosfarias.pokedex.routing.RouterSingletonHolder

class App : Application() {

    companion object {
        lateinit var context: Context
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            getString(R.string.app_name)
        )
            .fallbackToDestructiveMigration()
            .build()

        getSharedPreferences("preferences.theme", Context.MODE_PRIVATE).edit()
            .putInt("color", R.color.red)
            .apply()

        RouterSingletonHolder.initialize(this)
    }

}
