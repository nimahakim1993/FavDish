package com.company.nima.favdish.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.company.nima.favdish.R
import com.company.nima.favdish.application.FavDishApplication
import com.company.nima.favdish.databinding.FragmentAllDishesBinding
import com.company.nima.favdish.model.entities.FavDish
import com.company.nima.favdish.view.activities.AddUpdateDishActivity
import com.company.nima.favdish.view.activities.MainActivity
import com.company.nima.favdish.view.adapters.FavDishAdapter
import com.company.nima.favdish.viewmodel.FavDishViewModel
import com.company.nima.favdish.viewmodel.FavDishViewModelFactory
import com.company.nima.favdish.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        val adapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = adapter

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){
                dishes ->
            dishes.let {
                if (it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    adapter.dishesList(it)
                }
                else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_all_dishes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionNavigationAllDishesToDishDetailsFragment(favDish))

        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigation()
        }
    }

    fun deleteDish(favDish: FavDish){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_delete_dish).
        setMessage(getString(R.string.msg_delete_dish_dialog, favDish.title)).
        setIcon(R.drawable.ic_delete).
        setPositiveButton(resources.getString(R.string.lbl_yes)){dialog, _ ->
            mFavDishViewModel.delete(favDish)
            dialog.dismiss()
        }.
        setNegativeButton(resources.getString(R.string.lbl_no)){dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.showBottomNavigation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        mBinding = null
    }
}