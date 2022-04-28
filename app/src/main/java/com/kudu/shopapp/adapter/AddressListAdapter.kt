package com.kudu.shopapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kudu.shopapp.R
import com.kudu.shopapp.activities.AddEditAddressActivity
import com.kudu.shopapp.activities.CheckoutActivity
import com.kudu.shopapp.model.Address
import com.kudu.shopapp.util.Constants
import kotlinx.android.synthetic.main.item_address_layout.view.*

open class AddressListAdapter(
    private val context: Context,
    private val list: ArrayList<Address>,
    private val selectAddress: Boolean,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_address_layout, parent, false))
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS__CODE)
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_type.text = model.type
            holder.itemView.tv_address_details.text = "${model.address}. ${model.zipCode}"
            holder.itemView.tv_address_mobile_number.text = model.mobileNumber

            if (selectAddress) {
                holder.itemView.setOnClickListener {

                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}