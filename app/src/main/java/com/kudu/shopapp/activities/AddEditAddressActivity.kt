package com.kudu.shopapp.activities

import android.os.Bundle
import android.text.Editable
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

    private var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditAdressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {
                binding.tvTitle.text = resources.getString(R.string.title_add_address)
                binding.btnSubmitAddress.text = resources.getString(R.string.btn_lbl_update)
                binding.etFullName.text =
                    Editable.Factory.getInstance().newEditable(mAddressDetails?.name)
                binding.etPhoneNumber.text =
                    Editable.Factory.getInstance().newEditable(mAddressDetails?.mobileNumber)
                binding.etAddress.text =
                    Editable.Factory.getInstance().newEditable(mAddressDetails?.address)
                binding.etZipCode.text =
                    Editable.Factory.getInstance().newEditable(mAddressDetails?.zipCode)
                binding.etAdditionalNote.text =
                    Editable.Factory.getInstance().newEditable(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        binding.rbHome.isChecked = true
                    }
                    Constants.OFFICE -> {
                        binding.rbOffice.isChecked = true
                    }
                    else -> {
                        binding.rbOther.isChecked = true
                        binding.tilOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

        binding.btnSubmitAddress.setOnClickListener {
            saveAddressToFirestore()
        }
        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                binding.tilOtherDetails.visibility = View.VISIBLE
            } else {
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
        val fullName: String = binding.etFullName.text.toString().trim { it <= ' ' }
        val phoneNumber: String = binding.etPhoneNumber.text.toString().trim { it <= ' ' }
        val address: String = binding.etAddress.text.toString().trim { it <= ' ' }
        val zipCode: String = binding.etZipCode.text.toString().trim { it <= ' ' }
        val additionalNote: String = binding.etAdditionalNote.text.toString().trim { it <= ' ' }
        val otherDetails: String = binding.etOtherDetails.text.toString().trim { it <= ' ' }

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

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                Firestore().updateAddress(this, addressModel, mAddressDetails!!.id)
            } else {
                Firestore().addAddress(this, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage: String =
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                resources.getString(R.string.msg_your_address_updated_successfully)
            } else {
                resources.getString(R.string.err_your_address_added_successfully)
            }

        Toast.makeText(this, notifySuccessMessage, Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFullName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name),
                    true)
                false
            }
            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_phone_number),
                    true)
                false
            }
            TextUtils.isEmpty(binding.etAddress.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address),
                    true)
                false
            }
            TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name),
                    true)
                false
            }
            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }


}