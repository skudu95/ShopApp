package com.kudu.shopapp.activities

import android.os.Bundle
import android.text.TextUtils
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityAddEditAdressBinding

class AddEditAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityAddEditAdressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditAdressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarAddEditAddressActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_icon_white)
        }
        binding.toolbarAddEditAddressActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFullName.text.toString().trim() { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name),
                    true)
                false
            }
            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim() { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_phone_number),
                    true)
                false
            }
            TextUtils.isEmpty(binding.etAddress.text.toString().trim() { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address),
                    true)
                false
            }
            TextUtils.isEmpty(binding.etZipCode.text.toString().trim() { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name),
                    true)
                false
            }
            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim() { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }



}