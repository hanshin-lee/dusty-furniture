package com.dusty.presentation.screens.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.Listing
import com.dusty.domain.repository.AuthRepository
import com.dusty.domain.repository.ListingRepository
import com.dusty.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SellerDashboardState(
    val listings: Resource<List<Listing>> = Resource.Loading,
    val activeCount: Int = 0,
    val pendingCount: Int = 0,
    val soldCount: Int = 0
)

class SellerDashboardViewModel(
    private val listingRepository: ListingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SellerDashboardState())
    val state: StateFlow<SellerDashboardState> = _state.asStateFlow()

    init {
        loadListings()
    }

    fun loadListings() {
        val sellerId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(listings = Resource.Loading)
            try {
                val listings = listingRepository.getListingsBySeller(sellerId)
                _state.value = SellerDashboardState(
                    listings = Resource.Success(listings),
                    activeCount = listings.count { it.status == com.dusty.data.model.ListingStatus.ACTIVE },
                    pendingCount = listings.count { it.status == com.dusty.data.model.ListingStatus.PENDING_REVIEW },
                    soldCount = listings.count { it.status == com.dusty.data.model.ListingStatus.SOLD }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    listings = Resource.Error(e.message ?: "Failed to load listings")
                )
            }
        }
    }
}
