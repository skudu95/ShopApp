package com.kudu.shopapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.FragmentSoldProductsBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.SoldProduct

class SoldProductsFragment : BaseFragment() {

    private var _binding: FragmentSoldProductsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getSoldProductsList()
    }

    private fun getSoldProductsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getSoldProductsList(this)
    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        hideProgressDialog()
        if (soldProductsList.size > 0) {
            binding.rvSoldProductItems.visibility = View.VISIBLE
            binding.tvNoSoldProductsFound.visibility = View.GONE
        } else {
            binding.rvSoldProductItems.visibility = View.GONE
            binding.tvNoSoldProductsFound.visibility = View.VISIBLE
        }
    }

}