package com.kudu.shopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kudu.shopapp.R
import com.kudu.shopapp.adapter.CartItemsListAdapter
import com.kudu.shopapp.databinding.ActivityCartListBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.CartItem
import com.kudu.shopapp.model.Product
import com.kudu.shopapp.util.Constants

class CartListActivity : BaseActivity() {

    private lateinit var binding: ActivityCartListBinding
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductsList) {
            for (cartItem in cartList) {
                if (product.id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0) {
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if (mCartListItems.size > 0) {
            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this)
            binding.rvCartItemsList.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(this, cartList)
            binding.rvCartItemsList.adapter = cartListAdapter
            var subTotal = 0.0
            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }
            binding.tvSubTotal.text = "৳ $subTotal"
            binding.tvShippingCharge.text = "৳ 80" //TODO:  shipping charge based on area or weight

            if (subTotal > 0) {
                binding.llCheckout.visibility = View.VISIBLE
                val total = subTotal + 80 //TODO: add logic for shipping charge
                binding.tvTotalAmount.text = "৳ $total"
            } else {
                binding.llCheckout.visibility = View.GONE
            }
        } else {
            binding.rvCartItemsList.visibility = View.GONE
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    fun successProductsListFromFirestore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        mProductsList = productsList
        getCartItemsList()
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getAllProductsList(this)
    }

    private fun getCartItemsList() {
        //showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getCartList(this)
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }

    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductList()
    }

    fun itemRemoveSuccess() {
        hideProgressDialog()
        Toast.makeText(this,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT).show()
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