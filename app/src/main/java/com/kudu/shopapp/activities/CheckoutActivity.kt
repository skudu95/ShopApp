package com.kudu.shopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kudu.shopapp.R
import com.kudu.shopapp.adapter.CartItemsListAdapter
import com.kudu.shopapp.databinding.ActivityCheckoutBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.Address
import com.kudu.shopapp.model.CartItem
import com.kudu.shopapp.model.Order
import com.kudu.shopapp.model.Product
import com.kudu.shopapp.util.Constants

class CheckoutActivity : BaseActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        getProductList()
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
        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }
    }

    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(this, "Your order has been placed successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess() {
        Firestore().updateAllDetails(this, mCartItemsList, mOrderDetails)
    }

    fun successProductsListFromFirestore(productList: ArrayList<Product>) {
        mProductsList = productList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        Firestore().getCartList(this)
    }

    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mAddressDetails != null) {
            //object of order
            mOrderDetails = Order(
                Firestore().getCurrentUserId(),
                mCartItemsList,
                mAddressDetails!!,
                "My Order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "80.0",//TODO: dynamically change shipping charge according to area(area to be implemented),
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )
            Firestore().placeOrder(this, mOrderDetails)
        }
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        for (product in mProductsList) {
            for (cartItem in cartList) {
                if (product.id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList

        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter

        for (item in mCartItemsList) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }

        binding.tvCheckoutSubTotal.text = "৳ $mSubTotal"
        binding.tvCheckoutShippingCharge.text = "৳ 80.0"

        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 80.0
            binding.tvCheckoutTotalAmount.text = "৳ $mTotalAmount"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }

    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getAllProductsList(this)
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