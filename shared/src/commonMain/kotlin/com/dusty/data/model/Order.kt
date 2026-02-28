package com.dusty.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
enum class OrderStatus {
    @SerialName("pending") PENDING,
    @SerialName("confirmed") CONFIRMED,
    @SerialName("shipped") SHIPPED,
    @SerialName("delivered") DELIVERED,
    @SerialName("cancelled") CANCELLED,
    @SerialName("refunded") REFUNDED
}

@Serializable
data class ShippingAddress(
    @SerialName("line1") val line1: String = "",
    @SerialName("line2") val line2: String? = null,
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val country: String = "US"
)

@Serializable
data class Order(
    val id: String = "",
    @SerialName("buyer_id") val buyerId: String = "",
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    @SerialName("shipping_address") val shippingAddress: JsonObject? = null,
    @SerialName("payment_intent_id") val paymentIntentId: String? = null,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    // Joined
    val items: List<OrderItem>? = null
)

@Serializable
data class OrderItem(
    val id: String = "",
    @SerialName("order_id") val orderId: String = "",
    @SerialName("listing_id") val listingId: String = "",
    @SerialName("price_at_purchase") val priceAtPurchase: Double = 0.0,
    @SerialName("seller_id") val sellerId: String = "",
    // Joined
    val listing: Listing? = null
)
