package com.kudu.shopapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kudu.shopapp.R
import com.kudu.shopapp.adapter.AddressListAdapter
import com.kudu.shopapp.databinding.ActivityAddressListBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.Address
import com.kudu.shopapp.util.Constants
import com.kudu.shopapp.util.SwipeToDeleteCallback
import com.kudu.shopapp.util.SwipeToEditCallback

class AddressListActivity : BaseActivity() {

    private lateinit var binding: ActivityAddressListBinding

    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        getAddressList()

        binding.tvAddAddress.setOnClickListener {
            @Suppress("DEPRECATION")
            startActivityForResult(Intent(this, AddEditAddressActivity::class.java),
                Constants.ADD_ADDRESS__CODE)
        }

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        if (mSelectAddress) {
            binding.tvTitle.text = resources.getString(R.string.title_select_address)
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            getAddressList()
        }
    }

    /*override fun onResume() {
        super.onResume()
        getAddressList()
    }*/

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(this,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT).show()

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
            val addressAdapter = AddressListAdapter(this, addressList, mSelectAddress)
            binding.rvAddressList.adapter = addressAdapter

            //checking selected address
            if (!mSelectAddress) {
                //edit swiper
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = binding.rvAddressList.adapter as AddressListAdapter
                        adapter.notifyEditItem(this@AddressListActivity, viewHolder.adapterPosition)
                    }
                }
                // attaching the touch helper to rv
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)

                //delete swiper
                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        Firestore().deleteAddress(this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id)
                    }
                }
                // attaching the touch helper to rv
                val deleteItemSwiper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemSwiper.attachToRecyclerView(binding.rvAddressList)
            }
        } else {
            binding.rvAddressList.visibility = View.GONE
            binding.tvNoAddressFound.visibility = View.VISIBLE
        }
    }

}