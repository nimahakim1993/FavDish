package com.company.nima.favdish.view.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.company.nima.favdish.R
import com.company.nima.favdish.databinding.ItemDishLayoutBinding
import com.company.nima.favdish.model.entities.FavDish
import com.company.nima.favdish.utils.Constants
import com.company.nima.favdish.view.activities.AddUpdateDishActivity
import com.company.nima.favdish.view.fragments.AllDishesFragment
import com.company.nima.favdish.view.fragments.FavoriteDishesFragment

class FavDishAdapter(private val fragment: Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment).load(dish.image).into(holder.ivDishImage)
        holder.tvTitle.text = dish.title

        holder.itemView.setOnClickListener {
            if (fragment is AllDishesFragment){
                fragment.dishDetails(dish)
            }
            if (fragment is FavoriteDishesFragment){
                fragment.dishDetails(dish)
            }
        }
        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context, holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)

            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_edit_dish){
                    val intent = Intent(fragment.context, AddUpdateDishActivity::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                    fragment.requireActivity().startActivity(intent)
                }
                else if (it.itemId == R.id.action_delete_dish){
                    if (fragment is AllDishesFragment){
                        fragment.deleteDish(dish)
                    }
                }
                true
            }
            popup.show()
        }

        if (fragment is FavoriteDishesFragment){
            holder.ibMore.visibility = View.GONE
        }
        else if (fragment is AllDishesFragment){
            holder.ibMore.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun dishesList(list: List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: ItemDishLayoutBinding): RecyclerView.ViewHolder(view.root) {
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
        val ibMore = view.ibMore
    }
}