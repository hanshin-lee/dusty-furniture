package com.dusty.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    @SerialName("buyer") BUYER,
    @SerialName("seller") SELLER,
    @SerialName("admin") ADMIN
}

@Serializable
data class Profile(
    val id: String,
    val email: String,
    @SerialName("display_name") val displayName: String = "",
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val role: UserRole = UserRole.BUYER,
    val phone: String? = null,
    @SerialName("address_line1") val addressLine1: String? = null,
    @SerialName("address_line2") val addressLine2: String? = null,
    val city: String? = null,
    val state: String? = null,
    @SerialName("zip_code") val zipCode: String? = null,
    val country: String? = "US",
    @SerialName("seller_verified") val sellerVerified: Boolean = false,
    @SerialName("seller_bio") val sellerBio: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
