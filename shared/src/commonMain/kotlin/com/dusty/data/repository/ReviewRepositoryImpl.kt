package com.dusty.data.repository

import com.dusty.data.model.Review
import com.dusty.domain.repository.ReviewRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ReviewRepositoryImpl(
    private val client: SupabaseClient
) : ReviewRepository {

    override suspend fun getReviewsForListing(listingId: String): List<Review> {
        return client.from("reviews").select {
            filter { eq("listing_id", listingId) }
            order("created_at", Order.DESCENDING)
        }.decodeList()
    }

    override suspend fun createReview(listingId: String, reviewerId: String, rating: Int, comment: String?): Review {
        return client.from("reviews").insert(
            ReviewInsert(
                listingId = listingId,
                reviewerId = reviewerId,
                rating = rating,
                comment = comment
            )
        ) {
            select()
        }.decodeSingle()
    }

    @Serializable
    private data class ReviewInsert(
        @SerialName("listing_id") val listingId: String,
        @SerialName("reviewer_id") val reviewerId: String,
        val rating: Int,
        val comment: String? = null
    )
}
