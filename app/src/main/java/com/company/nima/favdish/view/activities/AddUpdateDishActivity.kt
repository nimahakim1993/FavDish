package com.company.nima.favdish.view.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.company.nima.favdish.R
import com.company.nima.favdish.databinding.ActivityAddUpdateDishBinding
import com.company.nima.favdish.databinding.DialogCustomImageSelectionBinding

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding : ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.ivAddDishImage.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.iv_add_dish_image -> {customImageSelectionDialog()}
        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvCamera.setOnClickListener{
            Toast.makeText(applicationContext, "camera selected", Toast.LENGTH_SHORT).show()
        }
        binding.tvGallery.setOnClickListener{
            Toast.makeText(applicationContext, "gallery selected", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }
}