package com.example.shopify.productdetails.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.productdetails.model.Review





class ReviewsAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    var visibleReviews: List<Review> = reviews.take(3)

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = visibleReviews[position]
        holder.titleTextView.text = review.title
        holder.contentTextView.text = review.content
        holder.authorTextView.text = review.author
    }

    override fun getItemCount(): Int {
        return visibleReviews.size
    }

    fun loadAllReviews() {
        visibleReviews = reviews
        notifyDataSetChanged()
    }
}
