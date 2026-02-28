package com.dusty.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.Listing
import com.dusty.data.model.Review
import com.dusty.domain.repository.AuthRepository
import com.dusty.domain.repository.CartRepository
import com.dusty.domain.repository.ListingRepository
import com.dusty.domain.repository.ReviewRepository
import com.dusty.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ListingDetailState(
    val listing: Resource<Listing> = Resource.Loading,
    val reviews: Resource<List<Review>> = Resource.Loading,
    val addedToCart: Boolean = false,
    val isAddingToCart: Boolean = false,
    val cartError: String? = null
)

class ListingDetailViewModel(
    private val listingId: String,
    private val listingRepository: ListingRepository,
    private val cartRepository: CartRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ListingDetailState())
    val state: StateFlow<ListingDetailState> = _state.asStateFlow()

    init {
        loadListing()
        loadReviews()
    }

    fun addToCart(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAddingToCart = true, cartError = null)
            try {
                cartRepository.addToCart(userId, listingId)
                _state.value = _state.value.copy(isAddingToCart = false, addedToCart = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isAddingToCart = false,
                    cartError = e.message ?: "Failed to add to cart"
                )
            }
        }
    }

    private fun loadListing() {
        viewModelScope.launch {
            try {
                val listing = listingRepository.getListingById(listingId)
                _state.value = _state.value.copy(listing = Resource.Success(listing))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    listing = Resource.Error(e.message ?: "Failed to load listing")
                )
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            try {
                val reviews = reviewRepository.getReviewsForListing(listingId)
                _state.value = _state.value.copy(reviews = Resource.Success(reviews))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    reviews = Resource.Error(e.message ?: "Failed to load reviews")
                )
            }
        }
    }
}
