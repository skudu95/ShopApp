package com.kudu.shopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kudu.shopapp.R
import com.kudu.shopapp.adapter.AddressListAdapter
import com.kudu.shopapp.databinding.ActivityAddressListBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.Address

class AddressListActivity : BaseActivity() {

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

    override fun onResume() {
        super.onResume()
        getAddressList()
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

    private fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getAddressList(this)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {
        hideProgressDialog()
        if (addressList.size > 0) {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE

            binding.rvAddressList.layoutManager = LinearLayoutManager(this)
            binding.rvAddressList.setHasFixedSize(true)
            val addressAdapter = AddressListAdapter(this, addressList)
            binding.rvAddressList.adapter = addressAdapter
        } else {
            binding.rvAddressList.visibility = View.GONE
            binding.tvNoAddressFound.visibility = View.VISIBLE
        }
    }

}