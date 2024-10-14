package com.example.shopify.BottomNavigationBar.OrderList.view
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.setting.currency.CurrencyConverter

class OrderListAdapter(
    private val context: Context,
    private var orders: List<Order>,
    private val currency: String,
    var listener: OnOrderClickListener
) : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.order_list_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.dateOrderCreatedAt.text = order.created_at

       // holder.priceOrder.text = "${order.total_price} $currency"
        val price = order.total_price?.let { CurrencyConverter.convertToUSD(it.toDouble()) }
        val priceFormat = price?.let { CurrencyConverter.formatCurrency(it) }
        holder.priceOrder.text = priceFormat

        holder.card.setOnClickListener {
            listener.onOrderClick(order)

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateOrderCreatedAt: TextView = itemView.findViewById(R.id.tv_created_at_date)
        val priceOrder: TextView = itemView.findViewById(R.id.tv_price_value)
        val card: CardView = itemView.findViewById(R.id.card_order_list)
    }
}
