package com.kudu.shopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivitySettingsBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.User
import com.kudu.shopapp.util.Constants
import com.kudu.shopapp.util.GlideLoader
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        binding.btnLogout.setOnClickListener(this)
        binding.tvEdit.setOnClickListener(this)

    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_icon_white)
        }
        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user
        hideProgressDialog()
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, binding.ivUserPhoto)
        binding.tvName.text = "${user.firstname} ${user.lastName}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
            }
        }
    }


}