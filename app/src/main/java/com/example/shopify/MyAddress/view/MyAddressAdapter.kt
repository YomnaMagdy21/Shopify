package com.example.shopify.MyAddress.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.model.addressModel.Address


class MyAddressAdapter(private var addresses: List<Address>,
                       private val onItemClick: (Address) -> Unit,
                       private val onDeleteButtonClick: (Address) -> Unit,
                       private val onEditButtonClick: (Address) -> Unit) : RecyclerView.Adapter<MyAddressAdapter.ViewHolder>() {

    private var selectedAddressPosition: Int = -1
    private var defaultAddressId: Long? = null
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressLine1: TextView = view.findViewById(R.id.Building_value)
        val city: TextView = view.findViewById(R.id.tv_cityy_value)
        val country: TextView = view.findViewById(R.id.tv_countryy_value)
        val phone: TextView = view.findViewById(R.id.tv_Phonee_value)
        val cardView: CardView = view.findViewById(R.id.card_Address_list)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteeAddress)
        val editButton: ImageView = view.findViewById(R.id.imageViewEdit)

        init {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val address = addresses[position]
                    if (selectedAddressPosition != position) {
                        // Move selected item to top
                        addresses = addresses.toMutableList().apply {
                            removeAt(position)
                            add(0, address)
                        }
                        selectedAddressPosition = 0
                        notifyDataSetChanged()
                        onItemClick(address)
                    }
                }
                true
            }

            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val address = addresses[position]
                    onDeleteButtonClick(address)
                }
            }

            editButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val address = addresses[position]
                    onEditButtonClick(address)
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

        if (address.id == defaultAddressId) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.backgroundColor1))
            holder.cardView.setBackgroundResource(R.drawable.edittext_border)
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            //holder.cardView.background = null
        }

    }

    override fun getItemCount(): Int {
        return addresses.size
    }

    fun updateAddresses(newAddresses: List<Address>,defaultId: Long?) {
        defaultAddressId = defaultId
        val mutableAddresses = newAddresses.toMutableList()
        defaultId?.let { defaultId ->
            val defaultAddressIndex = mutableAddresses.indexOfFirst { it.id == defaultId }
            if (defaultAddressIndex != -1) {
                mutableAddresses.removeAt(defaultAddressIndex)
                mutableAddresses.add(0, newAddresses[defaultAddressIndex])
            }
        }

        addresses = mutableAddresses
        notifyDataSetChanged()
    }
}
