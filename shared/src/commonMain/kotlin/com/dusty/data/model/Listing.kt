package com.dusty.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
enum class ListingCondition {
    @SerialName("mint") MINT,
    @SerialName("excellent") EXCELLENT,
    @SerialName("good") GOOD,
    @SerialName("fair") FAIR,
    @SerialName("poor") POOR,
    @SerialName("for_parts") FOR_PARTS;

    val displayName: String
        get() = when (this) {
            MINT -> "Mint"
            EXCELLENT -> "Excellent"
            GOOD -> "Good"
            FAIR -> "Fair"
            POOR -> "Poor"
            FOR_PARTS -> "For Parts"
        }
}

@Serializable
enum class ListingStatus {
    @SerialName("draft") DRAFT,
    @SerialName("pending_review") PENDING_REVIEW,
    @SerialName("active") ACTIVE,
    @SerialName("sold") SOLD,
    @SerialName("archived") ARCHIVED
}

@Serializable
data class Listing(
    val id: String = "",
    @SerialName("seller_id") val sellerId: String = "",
    @SerialName("category_id") val categoryId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val condition: ListingCondition = ListingCondition.GOOD,
    val status: ListingStatus = ListingStatus.PENDING_REVIEW,
    val era: String? = null,
    val material: String? = null,
    @SerialName("dimensions_cm") val dimensionsCm: JsonObject? = null,
    @SerialName("weight_kg") val weightKg: Double? = null,
    @SerialName("location_city") val locationCity: String? = null,
    @SerialName("location_state") val locationState: String? = null,
    @SerialName("is_featured") val isFeatured: Boolean = false,
    @SerialName("views_count") val viewsCount: Int = 0,
    val images: List<String> = emptyList(),
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
