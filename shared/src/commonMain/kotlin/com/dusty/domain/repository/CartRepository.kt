package com.dusty.domain.repository

import com.dusty.data.model.CartItem

interface CartRepository {
    suspend fun getCartItems(userId: String): List<CartItem>
    suspend fun addToCart(userId: String, listingId: String)
    suspend fun removeFromCart(userId: String, listingId: String)
    suspend fun clearCart(userId: String)
    suspend fun getCartCount(userId: String): Int
}
