package com.kudu.shopapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityAddressListBinding

class AddressListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        binding.tvAddAddress.setOnClickListener {
            startActivity(Intent(this, AddEditAddressActivity::class.java))
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarAddressListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_icon_white)
        }
        binding.toolbarAddressListActivity.setNavigationOnClickListener { onBackPressed() }
    }


}