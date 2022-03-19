package com.company.nima.favdish.model.database

import androidx.room.*
import com.company.nima.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)

    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favorite_dish = 1 ORDER BY ID")
    fun getFavoriteDishesList(): Flow<List<FavDish>>

    @Delete
    suspend fun deleteFavDishDetails(favDish: FavDish)
}