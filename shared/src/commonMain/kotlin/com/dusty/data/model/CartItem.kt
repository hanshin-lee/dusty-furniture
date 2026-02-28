package com.dusty.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("listing_id") val listingId: String = "",
    @SerialName("added_at") val addedAt: String? = null,
    // Joined from listings table when fetching cart
    val listing: Listing? = null
)
