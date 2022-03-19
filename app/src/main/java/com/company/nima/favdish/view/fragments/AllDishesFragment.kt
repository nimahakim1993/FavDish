package com.company.nima.favdish.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.nima.favdish.R
import com.company.nima.favdish.application.FavDishApplication
import com.company.nima.favdish.databinding.DialogCustomListBinding
import com.company.nima.favdish.databinding.FragmentAllDishesBinding
import com.company.nima.favdish.model.entities.FavDish
import com.company.nima.favdish.utils.Constants
import com.company.nima.favdish.view.activities.AddUpdateDishActivity
import com.company.nima.favdish.view.activities.MainActivity
import com.company.nima.favdish.view.adapters.CustomListItemAdapter
import com.company.nima.favdish.view.adapters.FavDishAdapter
import com.company.nima.favdish.viewmodel.FavDishViewModel
import com.company.nima.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding
    private lateinit var mCustomListDialog: Dialog
    private lateinit var mFavDishAdapter: FavDishAdapter

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
        mFavDishAdapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = mFavDishAdapter

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){
                dishes ->
            dishes.let {
                if (it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    mFavDishAdapter.dishesList(it)
                }
                else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }

            }
        }
    }



    private fun filterDishesListDialog() {
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)
        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(requireActivity(),this@AllDishesFragment, dishTypes, Constants.FILTER_SELECTION)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_all_dishes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun filterSelection(filterItemSelection: String) {
        mCustomListDialog.dismiss()

        if (filterItemSelection == Constants.ALL_ITEMS){
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){
                    dishes ->
                dishes.let {
                    if (it.isNotEmpty()){
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    }
                    else{
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }

                }
            }
        }
        else{
            mFavDishViewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner){
                dishes ->
                run {
                    dishes.let {
                        if (dishes.isNotEmpty()) {
                            mBinding.rvDishesList.visibility = View.VISIBLE
                            mBinding.tvNoDishesAddedYet.visibility = View.GONE
                            mFavDishAdapter.dishesList(it)
                        } else {
                            mBinding.rvDishesList.visibility = View.GONE
                            mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        mBinding = null
    }
}