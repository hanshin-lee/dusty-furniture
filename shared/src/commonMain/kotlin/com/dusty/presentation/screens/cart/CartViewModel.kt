package com.dusty.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.CartItem
import com.dusty.domain.repository.AuthRepository
import com.dusty.domain.repository.CartRepository
import com.dusty.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartState(
    val items: Resource<List<CartItem>> = Resource.Loading,
    val total: Double = 0.0
)

class CartViewModel(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(items = Resource.Loading)
            try {
                val items = cartRepository.getCartItems(userId)
                val total = items.sumOf { it.listing?.price ?: 0.0 }
                _state.value = CartState(
                    items = Resource.Success(items),
                    total = total
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    items = Resource.Error(e.message ?: "Failed to load cart")
                )
            }
        }
    }

    fun removeItem(listingId: String) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            try {
                cartRepository.removeFromCart(userId, listingId)
                loadCart()
            } catch (e: Exception) {
                // Reload to show current state
                loadCart()
            }
        }
    }
}
