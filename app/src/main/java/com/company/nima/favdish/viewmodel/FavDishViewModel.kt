package com.company.nima.favdish.viewmodel

import androidx.lifecycle.*
import com.company.nima.favdish.model.database.FavDishRepository
import com.company.nima.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FavDishViewModel(private val repository: FavDishRepository): ViewModel() {

    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDishData(dish)
    }

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    val favoriteDishesList: LiveData<List<FavDish>> = repository.favoriteDishesList.asLiveData()

    fun delete(dish: FavDish) = viewModelScope.launch {
        repository.deleteFavDishData(dish)
    }
}

class FavDishViewModelFactory(private val repository: FavDishRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}