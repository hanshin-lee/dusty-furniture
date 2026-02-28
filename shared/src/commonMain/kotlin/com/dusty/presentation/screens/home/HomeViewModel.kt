package com.dusty.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.Category
import com.dusty.data.model.Listing
import com.dusty.domain.repository.CategoryRepository
import com.dusty.domain.repository.ListingRepository
import com.dusty.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val featuredListings: Resource<List<Listing>> = Resource.Loading,
    val recentListings: Resource<List<Listing>> = Resource.Loading,
    val categories: Resource<List<Category>> = Resource.Loading,
    val searchQuery: String = ""
)

class HomeViewModel(
    private val listingRepository: ListingRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        loadFeatured()
        loadRecent()
        loadCategories()
    }

    fun onSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    private fun loadFeatured() {
        viewModelScope.launch {
            _state.value = _state.value.copy(featuredListings = Resource.Loading)
            try {
                val listings = listingRepository.getFeaturedListings(6)
                _state.value = _state.value.copy(featuredListings = Resource.Success(listings))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    featuredListings = Resource.Error(e.message ?: "Failed to load featured")
                )
            }
        }
    }

    private fun loadRecent() {
        viewModelScope.launch {
            _state.value = _state.value.copy(recentListings = Resource.Loading)
            try {
                val listings = listingRepository.getActiveListings(limit = 10)
                _state.value = _state.value.copy(recentListings = Resource.Success(listings))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    recentListings = Resource.Error(e.message ?: "Failed to load listings")
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(categories = Resource.Loading)
            try {
                val categories = categoryRepository.getCategories()
                _state.value = _state.value.copy(categories = Resource.Success(categories))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    categories = Resource.Error(e.message ?: "Failed to load categories")
                )
            }
        }
    }
}
