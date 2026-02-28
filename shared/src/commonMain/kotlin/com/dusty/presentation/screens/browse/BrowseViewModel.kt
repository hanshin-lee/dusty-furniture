package com.dusty.presentation.screens.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.Category
import com.dusty.data.model.Listing
import com.dusty.data.model.ListingCondition
import com.dusty.domain.repository.CategoryRepository
import com.dusty.domain.repository.ListingRepository
import com.dusty.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BrowseState(
    val listings: Resource<List<Listing>> = Resource.Loading,
    val categories: Resource<List<Category>> = Resource.Loading,
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val selectedCondition: ListingCondition? = null,
    val sortBy: SortOption = SortOption.NEWEST
)

enum class SortOption(val label: String) {
    NEWEST("Newest"),
    PRICE_LOW("Price: Low to High"),
    PRICE_HIGH("Price: High to Low")
}

class BrowseViewModel(
    private val listingRepository: ListingRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BrowseState())
    val state: StateFlow<BrowseState> = _state.asStateFlow()

    init {
        loadCategories()
        loadListings()
    }

    fun setCategoryFilter(categoryId: String?) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId)
        loadListings()
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun search() {
        loadListings()
    }

    fun setConditionFilter(condition: ListingCondition?) {
        _state.value = _state.value.copy(selectedCondition = condition)
        loadListings()
    }

    fun setSortOption(sort: SortOption) {
        _state.value = _state.value.copy(sortBy = sort)
        loadListings()
    }

    private fun loadCategories() {
        viewModelScope.launch {
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

    private fun loadListings() {
        viewModelScope.launch {
            _state.value = _state.value.copy(listings = Resource.Loading)
            try {
                val current = _state.value
                var listings = if (current.searchQuery.isNotBlank()) {
                    listingRepository.searchListings(current.searchQuery)
                } else {
                    listingRepository.getActiveListings(categoryId = current.selectedCategoryId)
                }

                // Client-side condition filter
                if (current.selectedCondition != null) {
                    listings = listings.filter { it.condition == current.selectedCondition }
                }

                // Client-side sort
                listings = when (current.sortBy) {
                    SortOption.NEWEST -> listings
                    SortOption.PRICE_LOW -> listings.sortedBy { it.price }
                    SortOption.PRICE_HIGH -> listings.sortedByDescending { it.price }
                }

                _state.value = _state.value.copy(listings = Resource.Success(listings))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    listings = Resource.Error(e.message ?: "Failed to load listings")
                )
            }
        }
    }
}
