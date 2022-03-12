package com.company.nima.favdish.model.database

import androidx.annotation.WorkerThread
import com.company.nima.favdish.model.entities.FavDish

class FavDishRepository (private val favDishDao: FavDishDao){

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }
}