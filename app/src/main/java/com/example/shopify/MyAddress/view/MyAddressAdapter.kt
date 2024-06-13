package com.example.shopify.MyAddress.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.model.addressModel.Address


class MyAddressAdapter(private var addresses: List<Address>,
                       private val onItemClick: (Address) -> Unit,
                       private val onDeleteButtonClick: (Address) -> Unit) : RecyclerView.Adapter<MyAddressAdapter.ViewHolder>() {

    private var selectedAddressPosition: Int = -1
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressLine1: TextView = view.findViewById(R.id.Building_value)
        val city: TextView = view.findViewById(R.id.tv_cityy_value)
        val country: TextView = view.findViewById(R.id.tv_countryy_value)
        val phone: TextView = view.findViewById(R.id.tv_Phonee_value)
        val cardView: CardView = view.findViewById(R.id.card_Address_list)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteeAddress)

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

            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val address = addresses[position]
                    onDeleteButtonClick(address)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_card_update, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addresses[position]

        holder.addressLine1.text = address.address1
        holder.city.text = address.address2
        holder.country.text = address.city
        holder.phone.text = address.company

        Log.i("phone", "onBindViewHolder: "+holder.country.text+","+holder.phone.text)

        // Apply shadow to selected item
        if (selectedAddressPosition == position) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
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
