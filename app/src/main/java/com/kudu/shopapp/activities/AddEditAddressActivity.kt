package com.kudu.shopapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityAddEditAdressBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.Address
import com.kudu.shopapp.util.Constants

class AddEditAddressActivity : BaseActivity() {

    private lateinit var binding: ActivityAddEditAdressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditAdressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnSubmitAddress.setOnClickListener {
            saveAddressToFirestore()
        }
        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other){
                binding.tilOtherDetails.visibility = View.VISIBLE
            }else{
                binding.tilOtherDetails.visibility = View.GONE
            }
        }
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

    private fun saveAddressToFirestore() {
        val fullName: String = binding.etFullName.text.toString().trim() { it <= ' ' }
        val phoneNumber: String = binding.etPhoneNumber.text.toString().trim() { it <= ' ' }
        val address: String = binding.etAddress.text.toString().trim() { it <= ' ' }
        val zipCode: String = binding.etZipCode.text.toString().trim() { it <= ' ' }
        val additionalNote: String = binding.etAdditionalNote.text.toString().trim() { it <= ' ' }
        val otherDetails: String = binding.etOtherDetails.text.toString().trim() { it <= ' ' }

        if (validateData()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                binding.rbHome.isChecked -> {
                    Constants.HOME
                }
                binding.rbOffice.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = Address(
                Firestore().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )
            Firestore().addAddress(this, addressModel)
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(this,
            resources.getString(R.string.err_your_address_added_successfully),
            Toast.LENGTH_SHORT).show()

        finish()
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