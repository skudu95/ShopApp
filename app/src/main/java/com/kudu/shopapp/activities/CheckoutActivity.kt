package com.kudu.shopapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityCheckoutBinding
import com.kudu.shopapp.model.Address
import com.kudu.shopapp.util.Constants

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var mAddressDetails: Address? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        }
        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text =
                "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote
            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarCheckoutActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_icon_white)
        }
        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
    }
}