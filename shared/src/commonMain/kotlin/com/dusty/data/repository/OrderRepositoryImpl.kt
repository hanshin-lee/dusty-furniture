package com.dusty.data.repository

import com.dusty.data.model.CartItem
import com.dusty.data.model.Order
import com.dusty.data.model.OrderItem
import com.dusty.data.model.ShippingAddress
import com.dusty.domain.repository.CartRepository
import com.dusty.domain.repository.OrderRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order as QueryOrder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement

class OrderRepositoryImpl(
    private val client: SupabaseClient,
    private val cartRepository: CartRepository
) : OrderRepository {

    override suspend fun placeOrder(userId: String, shippingAddress: ShippingAddress, notes: String?): Order {
        val cartItems = cartRepository.getCartItems(userId)
        if (cartItems.isEmpty()) error("Cart is empty")

        val totalAmount = cartItems.sumOf { it.listing?.price ?: 0.0 }
        val addressJson = Json.encodeToJsonElement(shippingAddress) as JsonObject

        val order = client.from("orders").insert(
            OrderInsert(
                buyerId = userId,
                totalAmount = totalAmount,
                shippingAddress = addressJson,
                notes = notes
            )
        ) {
            select()
        }.decodeSingle<Order>()

        // Create order items
        val orderItems = cartItems.mapNotNull { cartItem ->
            val listing = cartItem.listing ?: return@mapNotNull null
            OrderItemInsert(
                orderId = order.id,
                listingId = cartItem.listingId,
                priceAtPurchase = listing.price,
                sellerId = listing.sellerId
            )
        }
        client.from("order_items").insert(orderItems)

        // Mark listings as sold
        for (cartItem in cartItems) {
            client.from("listings").update({
                set("status", "sold")
            }) {
                filter { eq("id", cartItem.listingId) }
            }
        }

        // Clear cart
        cartRepository.clearCart(userId)

        return order
    }

    override suspend fun getOrders(userId: String): List<Order> {
        return client.from("orders").select(
            Columns.raw("*, items:order_items(*, listing:listings(*))")
        ) {
            filter { eq("buyer_id", userId) }
            order("created_at", QueryOrder.DESCENDING)
        }.decodeList()
    }

    override suspend fun getOrderById(id: String): Order {
        return client.from("orders").select(
            Columns.raw("*, items:order_items(*, listing:listings(*))")
        ) {
            filter { eq("id", id) }
        }.decodeSingle()
    }

    override suspend fun getAllOrders(): List<Order> {
        return client.from("orders").select(
            Columns.raw("*, items:order_items(*, listing:listings(*))")
        ) {
            order("created_at", QueryOrder.DESCENDING)
        }.decodeList()
    }

    override suspend fun updateOrderStatus(orderId: String, status: String) {
        client.from("orders").update({
            set("status", status)
        }) {
            filter { eq("id", orderId) }
        }
    }

    @Serializable
    private data class OrderInsert(
        @SerialName("buyer_id") val buyerId: String,
        @SerialName("total_amount") val totalAmount: Double,
        @SerialName("shipping_address") val shippingAddress: JsonObject,
        val notes: String? = null
    )

    @Serializable
    private data class OrderItemInsert(
        @SerialName("order_id") val orderId: String,
        @SerialName("listing_id") val listingId: String,
        @SerialName("price_at_purchase") val priceAtPurchase: Double,
        @SerialName("seller_id") val sellerId: String
    )
}
