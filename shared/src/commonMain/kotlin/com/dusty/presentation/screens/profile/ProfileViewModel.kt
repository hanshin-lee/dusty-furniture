package com.dusty.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.Order
import com.dusty.data.model.Profile
import com.dusty.data.model.UserRole
import com.dusty.domain.repository.AuthRepository
import com.dusty.domain.repository.OrderRepository
import com.dusty.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val profile: Resource<Profile> = Resource.Loading,
    val orders: Resource<List<Order>> = Resource.Loading,
    val isSigningOut: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
        loadOrders()
    }

    fun signOut() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSigningOut = true)
            try {
                authRepository.signOut()
            } catch (_: Exception) {}
            _state.value = _state.value.copy(isSigningOut = false)
        }
    }

    val isSeller: Boolean
        get() {
            val profile = (_state.value.profile as? Resource.Success)?.data
            return profile?.role == UserRole.SELLER || profile?.role == UserRole.ADMIN
        }

    val isAdmin: Boolean
        get() {
            val profile = (_state.value.profile as? Resource.Success)?.data
            return profile?.role == UserRole.ADMIN
        }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val profile = authRepository.getCurrentProfile()
                if (profile != null) {
                    _state.value = _state.value.copy(profile = Resource.Success(profile))
                } else {
                    _state.value = _state.value.copy(
                        profile = Resource.Error("Not logged in")
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    profile = Resource.Error(e.message ?: "Failed to load profile")
                )
            }
        }
    }

    private fun loadOrders() {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            try {
                val orders = orderRepository.getOrders(userId)
                _state.value = _state.value.copy(orders = Resource.Success(orders))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    orders = Resource.Error(e.message ?: "Failed to load orders")
                )
            }
        }
    }
}
