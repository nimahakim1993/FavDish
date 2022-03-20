package com.company.nima.favdish.viewmodel

import androidx.lifecycle.MutableLiveData
import com.company.nima.favdish.model.entities.RandomDish
import com.company.nima.favdish.model.network.RandomDishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel {
    private val randomRecipeApiService = RandomDishApiService()

    private val compositeDisposable = CompositeDisposable()

    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<RandomDish.Recipes>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    fun getRandomDishFromApi(){
        loadRandomDish.value = false

        compositeDisposable.add(
            randomRecipeApiService.getRandomDish().
                    subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribeWith(object: DisposableSingleObserver<RandomDish.Recipes>(){
                        override fun onSuccess(value: RandomDish.Recipes) {
                            loadRandomDish.value = true
                            randomDishResponse.value = value
                            randomDishLoadingError.value = false
                        }

                        override fun onError(e: Throwable) {
                            loadRandomDish.value = false
                            randomDishLoadingError.value = true
                            e.printStackTrace()
                        }

                    })

        )
    }
}