package com.dusty.domain.repository

import com.dusty.data.model.Review

interface ReviewRepository {
    suspend fun getReviewsForListing(listingId: String): List<Review>
    suspend fun createReview(listingId: String, reviewerId: String, rating: Int, comment: String?): Review
}
