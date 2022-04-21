package com.kudu.shopapp.activities

import android.os.Bundle
import android.util.Log
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityProductDetailsBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.Product
import com.kudu.shopapp.util.Constants
import com.kudu.shopapp.util.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityProductDetailsBinding

    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }
        getProductDetails()
    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getProductDetails(this, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(product.image,
            iv_product_detail_image)
        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "৳ ${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsStockQuantity.text = product.stock_quantity
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.back_icon_white)
        }
        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }
}