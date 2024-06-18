package com.example.shopify.productdetails.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val img :ImageView = itemView.findViewById(R.id.imgReview)
        val star1: ImageView = itemView.findViewById(R.id.star1)
        val star2: ImageView = itemView.findViewById(R.id.star2)
        val star3: ImageView = itemView.findViewById(R.id.star3)
        val star4: ImageView = itemView.findViewById(R.id.star4)
        val star5: ImageView = itemView.findViewById(R.id.star5)
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
        holder.img.setImageResource(review.image)
        setRating(holder, review.rating)
    }

    override fun getItemCount(): Int {
        return visibleReviews.size
    }

    fun loadAllReviews() {
        visibleReviews = reviews
        notifyDataSetChanged()
    }
    private fun setRating(holder: ReviewViewHolder, rating: Float) {
        val fullStars = rating.toInt()
        val halfStar = rating - fullStars >= 0.5

        val starImages = arrayOf(holder.star1, holder.star2, holder.star3, holder.star4, holder.star5)

        for (i in starImages.indices) {
            when {
                i < fullStars -> starImages[i].setImageResource(R.drawable.baseline_star_rate_24)
                i == fullStars && halfStar -> starImages[i].setImageResource(R.drawable.baseline_star_half_24)
                else -> starImages[i].setImageResource(R.drawable.baseline_star_outline_24)
            }
        }
    }
}
