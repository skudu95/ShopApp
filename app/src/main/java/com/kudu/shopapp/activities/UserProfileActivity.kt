package com.kudu.shopapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityUserProfileBinding
import com.kudu.shopapp.model.User
import com.kudu.shopapp.util.Constants
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var userDetails = User()
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            //get the user details from intent as ParcelableExtra
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        binding.etFirstName.isEnabled = false
        binding.etFirstName.setText(userDetails.firstname)

        binding.etLastName.isEnabled = false
        binding.etLastName.setText(userDetails.lastName)

        binding.etEmail.isEnabled = false
        binding.etEmail.setText(userDetails.email)

        binding.ivUserPhoto.setOnClickListener(this@UserProfileActivity)
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    //checking if permission granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    ) {
//                        showErrorSnackBar("Storage permission already granted.", false)
                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE)
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
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                //displaying another toast if permission not granted
                Toast.makeText(this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // the uri of selected image from phone storage
                        val selectedImageFilterUri = data.data!!
                        binding.ivUserPhoto.setImageURI(Uri.parse(selectedImageFilterUri.toString()))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


}