package com.example.shopify.productdetails.model

import com.example.shopify.R

data class Review(
    val title: String,
    val rating: Float,
    val content: String,
    val author: String,
    val image : Int
)

val staticReviews = listOf(
    Review(
        title = "Great product!",
        rating = 4.5f,
        content = "I absolutely love this product. It exceeded my expectations and works flawlessly.",
        author = "HappyCustomer123",
        image = R.drawable.img2jpg
    ),
    Review(
        title = "Not worth it",
        rating = 2.0f,
        content = "This product didn't meet my expectations. It feels cheaply made and broke within a week.",
        author = "DisappointedBuyer",
        image = R.drawable.img333
    ),
    Review(
        title = "Highly recommended!",
        rating = 5.0f,
        content = "I've been using this product for months now and it's still as good as new. Definitely worth the investment.",
        author = "SatisfiedCustomer",
        image = R.drawable.img555
    ),
    Review(
        title = "Great product!",
        rating = 4.5f,
        content = "I absolutely love this product. It exceeded my expectations and works flawlessly.",
        author = "HappyCustomer123",
        image = R.drawable.img66
    ),
    Review(
        title = "Highly recommended!",
        rating = 5.0f,
        content = "I've been using this product for months now and it's still as good as new. Definitely worth the investment.",
        author = "SatisfiedCustomer",
        image = R.drawable.im1jpg
    ),
    Review(
        title = "Impressive Product!",
        rating = 4.8f,
        content = "I'm extremely satisfied with this product. It works perfectly and is worth every penny.",
        author = "HappyCustomer456",
        image = R.drawable.img1
    ),
    Review(
        title = "Disappointing",
        rating = 2.5f,
        content = "This product didn't meet my expectations. It's not as durable as advertised.",
        author = "UnsatisfiedUser789",
        image = R.drawable.img3
    ),
    // Add more reviews here
)

fun getRandomReview(): Review {

    return staticReviews.random()
}
fun getRandomlyShuffledReviews(): List<Review> {
    return staticReviews.shuffled()
}