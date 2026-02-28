package com.dusty.presentation.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.Listing
import com.dusty.data.model.ListingStatus
import com.dusty.data.model.Profile
import com.dusty.domain.repository.ListingRepository
import com.dusty.domain.repository.CategoryRepository
import com.dusty.domain.repository.UserRepository
import com.dusty.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminDashboardState(
    val pendingListings: Resource<List<Listing>> = Resource.Loading,
    val users: Resource<List<Profile>> = Resource.Loading,
    val selectedTab: AdminTab = AdminTab.LISTINGS
)

enum class AdminTab(val label: String) {
    LISTINGS("Pending Listings"),
    USERS("Users")
}

class AdminDashboardViewModel(
    private val listingRepository: ListingRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminDashboardState())
    val state: StateFlow<AdminDashboardState> = _state.asStateFlow()

    init {
        loadPendingListings()
        loadUsers()
    }

    fun selectTab(tab: AdminTab) {
        _state.value = _state.value.copy(selectedTab = tab)
    }

    fun approveListing(listingId: String) {
        viewModelScope.launch {
            try {
                val listing = listingRepository.getListingById(listingId)
                listingRepository.updateListing(listing.copy(status = ListingStatus.ACTIVE))
                loadPendingListings()
            } catch (_: Exception) {}
        }
    }

    fun rejectListing(listingId: String) {
        viewModelScope.launch {
            try {
                val listing = listingRepository.getListingById(listingId)
                listingRepository.updateListing(listing.copy(status = ListingStatus.ARCHIVED))
                loadPendingListings()
            } catch (_: Exception) {}
        }
    }

    fun toggleSellerVerified(userId: String, verified: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.setSellerVerified(userId, verified)
                loadUsers()
            } catch (_: Exception) {}
        }
    }

    private fun loadPendingListings() {
        viewModelScope.launch {
            _state.value = _state.value.copy(pendingListings = Resource.Loading)
            try {
                val listings = listingRepository.getListingsByStatus(ListingStatus.PENDING_REVIEW)
                _state.value = _state.value.copy(pendingListings = Resource.Success(listings))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    pendingListings = Resource.Error(e.message ?: "Failed to load")
                )
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(users = Resource.Loading)
            try {
                val users = userRepository.getAllUsers()
                _state.value = _state.value.copy(users = Resource.Success(users))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    users = Resource.Error(e.message ?: "Failed to load users")
                )
            }
        }
    }
}
