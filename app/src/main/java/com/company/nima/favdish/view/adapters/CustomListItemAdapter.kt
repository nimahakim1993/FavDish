package com.company.nima.favdish.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.nima.favdish.databinding.ItemCustomListLayoutBinding
import com.company.nima.favdish.view.activities.AddUpdateDishActivity

class CustomListItemAdapter(private val activity: Activity, private val listItems: List<String>,
        private val selection: String) : RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomListItemAdapter.ViewHolder {
        val binding: ItemCustomListLayoutBinding = ItemCustomListLayoutBinding.inflate(
            LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomListItemAdapter.ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvText.text = item
        holder.tvText.setOnClickListener{
            if (activity is AddUpdateDishActivity){
                activity.selectedListItem(item, selection)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    class ViewHolder(binding: ItemCustomListLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        val tvText = binding.tvText
    }
}