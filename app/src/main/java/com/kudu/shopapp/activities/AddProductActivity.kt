package com.kudu.shopapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityAddProductBinding
import com.kudu.shopapp.util.Constants
import com.kudu.shopapp.util.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        binding.ivAddUpdateProduct.setOnClickListener(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_icon_white)
        }
        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //if permission granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                showErrorSnackBar("The storage permission is granted.", false)
                Constants.showImageChooser(this@AddProductActivity)
            } else {
                //displaying another toast if permission not granted
                Toast.makeText(this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.edit_icon))

                    val selectedImageProfileUri = data.data!!
                    try {
                        GlideLoader(this).loadUserPicture(selectedImageProfileUri, iv_product_image)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddProductActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

}