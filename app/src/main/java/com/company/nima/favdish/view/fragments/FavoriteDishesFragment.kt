package com.company.nima.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.company.nima.favdish.application.FavDishApplication
import com.company.nima.favdish.databinding.FragmentDishDetailsBinding
import com.company.nima.favdish.databinding.FragmentFavoriteDishesBinding
import com.company.nima.favdish.model.entities.FavDish
import com.company.nima.favdish.view.activities.MainActivity
import com.company.nima.favdish.view.adapters.FavDishAdapter
import com.company.nima.favdish.viewmodel.DashboardViewModel
import com.company.nima.favdish.viewmodel.FavDishViewModel
import com.company.nima.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var mBinding: FragmentFavoriteDishesBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFavDishViewModel.favoriteDishesList.observe(viewLifecycleOwner){
            dishes ->
            run {
                dishes.let {
                    mBinding!!.rvFavoriteDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
                    val adapter = FavDishAdapter(this)
                    mBinding!!.rvFavoriteDishesList.adapter = adapter
                    if (it.isNotEmpty()) {
                        mBinding!!.rvFavoriteDishesList.visibility = View.VISIBLE
                        mBinding!!.tvNoFavoriteDishesAvailable.visibility = View.GONE
                        adapter.dishesList(it)
                    }
                    else{
                        mBinding!!.rvFavoriteDishesList.visibility = View.GONE
                        mBinding!!.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(FavoriteDishesFragmentDirections.actionNavigationFavoriteDishesToDishDetailsFragment(favDish))

        if (requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavigation()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}