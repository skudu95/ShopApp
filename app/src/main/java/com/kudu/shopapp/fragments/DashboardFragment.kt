package com.kudu.shopapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import com.kudu.shopapp.R
import com.kudu.shopapp.activities.SettingsActivity
import com.kudu.shopapp.databinding.FragmentDashboardBinding
import com.kudu.shopapp.firestore.Firestore
import com.kudu.shopapp.model.Product

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {
        hideProgressDialog()
        for (i in dashboardItemsList) {
            Log.i("Item Title", i.title)
        }
    }

    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getDashboardItemsList(this@DashboardFragment)
    }

}