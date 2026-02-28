package com.dusty.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: String = "",
    @SerialName("listing_id") val listingId: String = "",
    @SerialName("reviewer_id") val reviewerId: String = "",
    val rating: Int = 0,
    val comment: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    // Joined
    val reviewer: Profile? = null
)
