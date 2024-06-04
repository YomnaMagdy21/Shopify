package com.example.shopify.BottomNavigationBar.OrderList
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.orderItem.OrderItemFragment
import com.example.shopify.R
import com.example.shopify.shoppingCard.view.shoppingCardFragment

class OrderListAdapter(
    private val context: Context,
    private val dates: List<String>,
    private val prices: List<String>,
    private val currency: String,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.order_list_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dateOrderCreatedAt.text = dates[position]
        holder.priceOrder.text = "${prices[position]} $currency"
        holder.card.setOnClickListener{
            val newFragment = OrderItemFragment()
            fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateOrderCreatedAt: TextView = itemView.findViewById(R.id.tv_created_at_date)
        val priceOrder: TextView = itemView.findViewById(R.id.tv_price_value)
        val card: CardView = itemView.findViewById(R.id.card_order_list)
    }
}
