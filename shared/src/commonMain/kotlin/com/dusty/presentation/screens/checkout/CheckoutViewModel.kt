package com.dusty.presentation.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.Order
import com.dusty.data.model.ShippingAddress
import com.dusty.domain.repository.AuthRepository
import com.dusty.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckoutState(
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "US",
    val notes: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderPlaced: Order? = null
)

class CheckoutViewModel(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state: StateFlow<CheckoutState> = _state.asStateFlow()

    fun onAddressLine1Changed(value: String) {
        _state.value = _state.value.copy(addressLine1 = value, error = null)
    }

    fun onAddressLine2Changed(value: String) {
        _state.value = _state.value.copy(addressLine2 = value)
    }

    fun onCityChanged(value: String) {
        _state.value = _state.value.copy(city = value, error = null)
    }

    fun onStateChanged(value: String) {
        _state.value = _state.value.copy(state = value, error = null)
    }

    fun onZipCodeChanged(value: String) {
        _state.value = _state.value.copy(zipCode = value, error = null)
    }

    fun onNotesChanged(value: String) {
        _state.value = _state.value.copy(notes = value)
    }

    fun placeOrder() {
        val current = _state.value
        if (current.addressLine1.isBlank() || current.city.isBlank() ||
            current.state.isBlank() || current.zipCode.isBlank()
        ) {
            _state.value = current.copy(error = "Please fill in all required address fields")
            return
        }

        val userId = authRepository.currentUserId ?: return

        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null)
            try {
                val address = ShippingAddress(
                    line1 = current.addressLine1.trim(),
                    line2 = current.addressLine2.trim().ifBlank { null },
                    city = current.city.trim(),
                    state = current.state.trim(),
                    zip = current.zipCode.trim(),
                    country = current.country
                )
                val order = orderRepository.placeOrder(
                    userId = userId,
                    shippingAddress = address,
                    notes = current.notes.trim().ifBlank { null }
                )
                _state.value = _state.value.copy(isLoading = false, orderPlaced = order)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to place order"
                )
            }
        }
    }
}
