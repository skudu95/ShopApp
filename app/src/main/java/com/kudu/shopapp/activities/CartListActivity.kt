package com.kudu.shopapp.activities

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kudu.shopapp.R
import com.kudu.shopapp.adapter.CartItemsListAdapter
import com.kudu.shopapp.databinding.ActivityCartListBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.CartItem

class CartListActivity : BaseActivity() {

    private lateinit var binding: ActivityCartListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        if (cartList.size > 0) {
            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this)
            binding.rvCartItemsList.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(this, cartList)
            binding.rvCartItemsList.adapter = cartListAdapter
            var subTotal = 0.0
            for (item in cartList) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                subTotal += (price * quantity)
            }
            binding.tvSubTotal.text = "৳ $subTotal"
            binding.tvShippingCharge.text = "৳ 80" //TODO:  shipping charge based on area or weight

            if (subTotal > 0) {
                binding.llCheckout.visibility = View.VISIBLE
                val total = subTotal + 80 //TODO: add logic for shipping charge
                binding.tvTotalAmount.text = "৳ $total"
            }else{
                binding.llCheckout.visibility = View.GONE
            }
        }else{
            binding.rvCartItemsList.visibility = View.GONE
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    private fun getCartItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getCartList(this)
    }

    override fun onResume() {
        super.onResume()
        getCartItemsList()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarCartListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_icon_white)
        }
        binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }
    }
}