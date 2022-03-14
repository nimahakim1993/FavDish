package com.company.nima.favdish.application

import android.app.Application
import com.company.nima.favdish.model.database.FavDishRepository
import com.company.nima.favdish.model.database.FavDishRoomDatabase

class FavDishApplication : Application() {

    private val database by lazy { FavDishRoomDatabase.getDatabase(this@FavDishApplication) }
    val repository by lazy { FavDishRepository(database.favDishDao()) }
}