package com.example.shopify.setting.MyAddress.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.model.addressModel.Address


class MyAddressAdapter(private var addresses: List<Address>,
                       private val onItemClick: (Address) -> Unit) : RecyclerView.Adapter<MyAddressAdapter.ViewHolder>() {

    private var selectedAddressPosition: Int = -1
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressLine1: TextView = view.findViewById(R.id.client_address_value)
        val city: TextView = view.findViewById(R.id.city_value)
        val country: TextView = view.findViewById(R.id.country_value)
        val phone: TextView = view.findViewById(R.id.client_phone_value)
        val cardView: CardView = view.findViewById(R.id.addresses_card)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val address = addresses[position]
                    if (selectedAddressPosition != position) {
                        // Unselect previously selected item
                        notifyItemChanged(selectedAddressPosition)
                        // Select the clicked item
                        selectedAddressPosition = position
                        notifyItemChanged(selectedAddressPosition)
                        onItemClick(address)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.addresses_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addresses[position]
        holder.addressLine1.text = address.address1
        holder.city.text = address.city
        holder.country.text = address.country
        holder.phone.text = address.phone

        // Apply shadow to selected item
        if (selectedAddressPosition == position) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.backgroundColor1))
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.backgroundColor1))
        }

    }

    override fun getItemCount(): Int {
        return addresses.size
    }

    fun updateAddresses(newAddresses: List<Address>) {
        addresses = newAddresses
        notifyDataSetChanged()
    }
}
