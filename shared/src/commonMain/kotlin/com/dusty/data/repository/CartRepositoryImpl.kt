package com.dusty.data.repository

import com.dusty.data.model.CartItem
import com.dusty.domain.repository.CartRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class CartRepositoryImpl(
    private val client: SupabaseClient
) : CartRepository {

    override suspend fun getCartItems(userId: String): List<CartItem> {
        return client.from("cart_items").select(
            Columns.raw("*, listing:listings(*)")
        ) {
            filter { eq("user_id", userId) }
        }.decodeList()
    }

    override suspend fun addToCart(userId: String, listingId: String) {
        client.from("cart_items").insert(
            CartInsert(userId = userId, listingId = listingId)
        )
    }

    override suspend fun removeFromCart(userId: String, listingId: String) {
        client.from("cart_items").delete {
            filter {
                eq("user_id", userId)
                eq("listing_id", listingId)
            }
        }
    }

    override suspend fun clearCart(userId: String) {
        client.from("cart_items").delete {
            filter { eq("user_id", userId) }
        }
    }

    override suspend fun getCartCount(userId: String): Int {
        return client.from("cart_items").select {
            filter { eq("user_id", userId) }
            count(io.github.jan.supabase.postgrest.query.Count.EXACT)
        }.countOrNull()?.toInt() ?: 0
    }

    @Serializable
    private data class CartInsert(
        @SerialName("user_id") val userId: String,
        @SerialName("listing_id") val listingId: String
    )
}
