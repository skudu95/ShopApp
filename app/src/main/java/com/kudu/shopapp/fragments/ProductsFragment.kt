package com.kudu.shopapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.kudu.shopapp.R
import com.kudu.shopapp.activities.AddProductActivity
import com.kudu.shopapp.adapter.ProductListAdapter
import com.kudu.shopapp.databinding.FragmentProductsBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.Product

class ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun successProductListFromFirestore(productList: ArrayList<Product>) {
        hideProgressDialog()

        if (productList.size > 0) {
            binding.rvMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE

            binding.rvMyProductItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyProductItems.setHasFixedSize(true)
            val adapterProducts = ProductListAdapter(requireActivity(), productList)
            binding.rvMyProductItems.adapter = adapterProducts
        }
    }

    private fun getProductListFromFirestore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getProductList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFirestore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}