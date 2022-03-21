package com.company.nima.favdish.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.company.nima.favdish.R
import com.company.nima.favdish.application.FavDishApplication
import com.company.nima.favdish.databinding.FragmentRandomDishBinding
import com.company.nima.favdish.model.entities.FavDish
import com.company.nima.favdish.model.entities.RandomDish
import com.company.nima.favdish.utils.Constants
import com.company.nima.favdish.viewmodel.FavDishViewModel
import com.company.nima.favdish.viewmodel.FavDishViewModelFactory
import com.company.nima.favdish.viewmodel.NotificationsViewModel
import com.company.nima.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var mBinding: FragmentRandomDishBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private var progressDialog: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomDishFromApi()
        randomDishViewModelObserver()

        mBinding!!.srlRefreshLayout.setOnRefreshListener {
            mRandomDishViewModel.getRandomDishFromApi()
        }
    }

    private fun randomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner,{
                randomDishResponse -> randomDishResponse?.let {
            setRandomDishResponseInUI(randomDishResponse.recipes[0])

            if (mBinding!!.srlRefreshLayout.isRefreshing){
                mBinding!!.srlRefreshLayout.isRefreshing = false
            }
        }
        })
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner, {
                dataError-> dataError?.let {
            Log.i("RANDOM DISH API ERROR", "$dataError")
            if (mBinding!!.srlRefreshLayout.isRefreshing){
                mBinding!!.srlRefreshLayout.isRefreshing = false
            }

        }
        })
        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner,{
                loadRandomDish->loadRandomDish?.let {
            Log.i("RANDOM DISH API RESPONSE", "$loadRandomDish")

            if (loadRandomDish && !mBinding!!.srlRefreshLayout.isRefreshing)
                showProgressDialog()
            else
                hideProgressDialog()
        }
        })
    }

    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {
        // Load the dish image in the ImageView.
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(mBinding!!.ivDishImage)

        mBinding!!.tvTitle.text = recipe.title

        // Default Dish Type
        var dishType: String = "other"

        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            mBinding!!.tvType.text = dishType
        }

        // There is not category params present in the response so we will define it as Other.
        mBinding!!.tvCategory.text = "Other"

        var ingredients = ""
        for (value in recipe.extendedIngredients) {

            if (ingredients.isEmpty()) {
                ingredients = value.original
            } else {
                ingredients = ingredients + ", \n" + value.original
            }
        }

        mBinding!!.tvIngredients.text = ingredients

        // The instruction or you can say the Cooking direction text is in the HTML format so we will you the fromHtml to populate it in the TextView.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            mBinding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        mBinding!!.tvCookingTime.text =
            resources.getString(
                R.string.lbl_estimate_cooking_time,
                recipe.readyInMinutes.toString()
            )



        // TODO step 6: Assign the click event to the Favorite Button and add the dish details to the local database if user click on it.
        // START

        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )

        var isAddedToFavorites: Boolean = false
        mBinding!!.ivFavoriteDish.setOnClickListener {
            if (isAddedToFavorites){
                Toast.makeText(context, "you already added to favorites", Toast.LENGTH_SHORT).show()
            }
            else{
                // TODO Step 7: Create a instance of FavDish data model class and fill it with required information from the API response.
                // START
                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )
                // END

                // TODO Step 8: Create an instance of FavDishViewModel class and call insert function and pass the required details.
                // START
                val mFavDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }

                mFavDishViewModel.insert(randomDishDetails)
                // END

                // TODO Step 9: Once the dish is inserted you can acknowledge user by Toast message as below and also update the favorite image by selected.
                // START
                isAddedToFavorites = true

                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )

                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()
                // END
            }
        }
        // END
    }

    fun showProgressDialog(){
        progressDialog = Dialog(requireActivity())
        progressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }
    fun hideProgressDialog(){
        progressDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}