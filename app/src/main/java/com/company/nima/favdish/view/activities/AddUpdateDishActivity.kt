package com.company.nima.favdish.view.activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.company.nima.favdish.R
import com.company.nima.favdish.application.FavDishApplication
import com.company.nima.favdish.databinding.ActivityAddUpdateDishBinding
import com.company.nima.favdish.databinding.DialogCustomImageSelectionBinding
import com.company.nima.favdish.databinding.DialogCustomListBinding
import com.company.nima.favdish.model.entities.FavDish
import com.company.nima.favdish.utils.Constants
import com.company.nima.favdish.view.adapters.CustomListItemAdapter
import com.company.nima.favdish.viewmodel.FavDishViewModel
import com.company.nima.favdish.viewmodel.FavDishViewModelFactory
import com.karumi.dexter.PermissionToken

import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener

import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding : ActivityAddUpdateDishBinding
    private var mImagePath: String = ""
    private lateinit var mCustomListDialog: Dialog

    private var mFavDishDetails: FavDish? = null

    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    companion object{
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "FavDishDirectory"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra(Constants.EXTRA_DISH_DETAILS)){
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }

        mFavDishDetails?.let {
            if (it.id != 0){
                mImagePath = it.image
                Glide.with(this).load(mImagePath).centerCrop().into(mBinding.ivAddDishImage)

                mBinding.etTitle.setText(it.title)
                mBinding.etType.setText(it.type)
                mBinding.etCategory.setText(it.category)
                mBinding.etIngredients.setText(it.ingredients)
                mBinding.etCookingTime.setText(it.cookingTime)
                mBinding.etDirectionToCook.setText(it.directionToCook)

                mBinding.btnAddDish.text = resources.getString(R.string.lbl_update_dish)
            }
        }

        setupActionBar()

        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        if (mFavDishDetails != null && mFavDishDetails!!.id != 0){
            supportActionBar.let {
                it?.title = resources.getString(R.string.title_edit_dish)
            }
        }
        else{
            supportActionBar.let {
                it?.title = resources.getString(R.string.title_add_dish)
            }
        }
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.iv_add_dish_image -> {
                customImageSelectionDialog()
                return
            }
            R.id.et_type -> {
                customItemDialog(
                    resources.getString(R.string.title_select_dish_type), Constants.dishTypes(), Constants.DISH_TYPE)
                return
            }
            R.id.et_category -> {
                customItemDialog(
                    resources.getString(R.string.title_select_dish_category), Constants.dishCategories(), Constants.DISH_CATEGORY)
                return
            }
            R.id.et_cooking_time -> {
                customItemDialog(
                    resources.getString(R.string.title_select_dish_cooking_time), Constants.dishCookTime(), Constants.DISH_COOKING_TIME)
                return
            }
            R.id.btn_add_dish -> {

                // Define the local variables and get the EditText values.
                // For Dish Image we have the global variable defined already.

                val title = mBinding.etTitle.text.toString().trim { it <= ' ' }
                val type = mBinding.etType.text.toString().trim { it <= ' ' }
                val category = mBinding.etCategory.text.toString().trim { it <= ' ' }
                val ingredients = mBinding.etIngredients.text.toString().trim { it <= ' ' }
                val cookingTimeInMinutes = mBinding.etCookingTime.text.toString().trim { it <= ' ' }
                val cookingDirection = mBinding.etDirectionToCook.text.toString().trim { it <= ' ' }

                when {

                    TextUtils.isEmpty(mImagePath) -> {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            resources.getString(R.string.err_msg_select_dish_image),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    TextUtils.isEmpty(title) -> {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            resources.getString(R.string.err_msg_enter_dish_title),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    TextUtils.isEmpty(type) -> {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            resources.getString(R.string.err_msg_select_dish_type),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    TextUtils.isEmpty(category) -> {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            resources.getString(R.string.err_msg_select_dish_category),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    TextUtils.isEmpty(ingredients) -> {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            resources.getString(R.string.err_msg_enter_dish_ingredients),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    TextUtils.isEmpty(cookingTimeInMinutes) -> {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            resources.getString(R.string.err_msg_select_dish_cooking_time),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    TextUtils.isEmpty(cookingDirection) -> {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            resources.getString(R.string.err_msg_enter_dish_cooking_instructions),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        var dishId = 0
                        var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                        var favoriteDish = false

                        mFavDishDetails?.let {
                            if (it.id != 0) {
                                dishId = it.id
                                imageSource = it.imageSource
                                favoriteDish = it.favoriteDish
                            }
                        }

                        val favDishDetails: FavDish = FavDish(
                            mImagePath,
                            imageSource,
                            title,
                            type,
                            category,
                            ingredients,
                            cookingTimeInMinutes,
                            cookingDirection,
                            favoriteDish,
                            dishId)

                        if (dishId == 0){
                            mFavDishViewModel.insert(favDishDetails)
                            Toast.makeText(
                                applicationContext,
                                "You successfully added your dish",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("Insertion", "successfully")
                        }
                        else{
                            mFavDishViewModel.update(favDishDetails)
                            Toast.makeText(
                                applicationContext,
                                "You successfully updated your dish",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("Updating", "successfully")
                        }
                        finish()
                    }
                }
            }

        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvCamera.setOnClickListener{
            Dexter.withContext(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalPermissionForShowPermission()
                    }

                }).onSameThread().check()
            dialog.dismiss()
        }
        binding.tvGallery.setOnClickListener{
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(applicationContext, "you have denied storage permission", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        showRationalPermissionForShowPermission()
                    }

                }).onSameThread().check()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
//                    mBinding.ivDishImage.setImageBitmap(thumbnail)
                    Glide.with(this).load(thumbnail).centerCrop().into(mBinding.ivDishImage)
                    saveImageToInternalStorage(thumbnail)
                }
            }
            if (requestCode == GALLERY){
                data?.data.let {
                    val selectedImageUri = data!!.data
//                    mBinding.ivDishImage.setImageURI(selectedImageUri)
                    Glide.with(this).
                    load(selectedImageUri).
                    centerCrop().
                    diskCacheStrategy(DiskCacheStrategy.ALL).
                    listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            Log.i("TAG", "error loading image", e)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            resource?.let {
                                val bitmap : Bitmap = resource.toBitmap()
                                mImagePath = saveImageToInternalStorage(bitmap)
                                Log.i("ImagePath", mImagePath)
                            }
                            return false
                        }

                    }).
                    into(mBinding.ivDishImage)


                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
                }
            }
        }
    }

    private fun showRationalPermissionForShowPermission() {
        AlertDialog.Builder(this).setMessage("It looks like you don`t grant permission, go to setting for grant it")
            .setPositiveButton("Go To Setting", DialogInterface.OnClickListener { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }).setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }).show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String{
        val wrapContext = ContextWrapper(applicationContext)
        var file = wrapContext.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }
        catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun customItemDialog(title: String, listItems: List<String>, selection: String){
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, listItems, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    fun selectedListItem(item :String, selection: String){
        when(selection){
            Constants.DISH_TYPE ->{
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY ->{
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            else ->{
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }
}