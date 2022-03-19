package com.company.nima.favdish.model.database

import androidx.annotation.WorkerThread
import com.company.nima.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository (private val favDishDao: FavDishDao){

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }
    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish){
        favDishDao.updateFavDishDetails(favDish)
    }

    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishesList()

    val favoriteDishesList: Flow<List<FavDish>> = favDishDao.getFavoriteDishesList()

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish){
        favDishDao.deleteFavDishDetails(favDish)
    }
}