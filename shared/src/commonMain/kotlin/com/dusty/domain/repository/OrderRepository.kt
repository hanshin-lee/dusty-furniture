package com.dusty.domain.repository

import com.dusty.data.model.Order
import com.dusty.data.model.ShippingAddress

interface OrderRepository {
    suspend fun placeOrder(userId: String, shippingAddress: ShippingAddress, notes: String? = null): Order
    suspend fun getOrders(userId: String): List<Order>
    suspend fun getOrderById(id: String): Order
    suspend fun getAllOrders(): List<Order>
    suspend fun updateOrderStatus(orderId: String, status: String)
}
