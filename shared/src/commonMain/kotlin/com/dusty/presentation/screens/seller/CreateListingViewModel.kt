package com.dusty.presentation.screens.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.data.model.Category
import com.dusty.data.model.Listing
import com.dusty.data.model.ListingCondition
import com.dusty.data.model.ListingStatus
import com.dusty.domain.repository.AuthRepository
import com.dusty.domain.repository.CategoryRepository
import com.dusty.domain.repository.ListingRepository
import com.dusty.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateListingState(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val condition: ListingCondition = ListingCondition.GOOD,
    val categoryId: String = "",
    val era: String = "",
    val material: String = "",
    val locationCity: String = "",
    val locationState: String = "",
    val categories: Resource<List<Category>> = Resource.Loading,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false
)

class CreateListingViewModel(
    private val editListingId: String?,
    private val listingRepository: ListingRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateListingState(isEditing = editListingId != null))
    val state: StateFlow<CreateListingState> = _state.asStateFlow()

    init {
        loadCategories()
        if (editListingId != null) loadExisting(editListingId)
    }

    fun onTitleChanged(v: String) { _state.value = _state.value.copy(title = v, error = null) }
    fun onDescriptionChanged(v: String) { _state.value = _state.value.copy(description = v, error = null) }
    fun onPriceChanged(v: String) { _state.value = _state.value.copy(price = v, error = null) }
    fun onConditionChanged(v: ListingCondition) { _state.value = _state.value.copy(condition = v) }
    fun onCategoryChanged(v: String) { _state.value = _state.value.copy(categoryId = v) }
    fun onEraChanged(v: String) { _state.value = _state.value.copy(era = v) }
    fun onMaterialChanged(v: String) { _state.value = _state.value.copy(material = v) }
    fun onLocationCityChanged(v: String) { _state.value = _state.value.copy(locationCity = v) }
    fun onLocationStateChanged(v: String) { _state.value = _state.value.copy(locationState = v) }

    fun save() {
        val current = _state.value
        val price = current.price.toDoubleOrNull()
        when {
            current.title.isBlank() -> {
                _state.value = current.copy(error = "Title is required")
                return
            }
            current.description.isBlank() -> {
                _state.value = current.copy(error = "Description is required")
                return
            }
            price == null || price <= 0 -> {
                _state.value = current.copy(error = "Please enter a valid price")
                return
            }
            current.categoryId.isBlank() -> {
                _state.value = current.copy(error = "Please select a category")
                return
            }
        }

        val sellerId = authRepository.currentUserId ?: return

        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null)
            try {
                val listing = Listing(
                    id = editListingId ?: "",
                    sellerId = sellerId,
                    categoryId = current.categoryId,
                    title = current.title.trim(),
                    description = current.description.trim(),
                    price = price!!,
                    condition = current.condition,
                    status = ListingStatus.PENDING_REVIEW,
                    era = current.era.trim().ifBlank { null },
                    material = current.material.trim().ifBlank { null },
                    locationCity = current.locationCity.trim().ifBlank { null },
                    locationState = current.locationState.trim().ifBlank { null }
                )

                if (editListingId != null) {
                    listingRepository.updateListing(listing)
                } else {
                    listingRepository.createListing(listing)
                }
                _state.value = _state.value.copy(isLoading = false, isSaved = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to save listing"
                )
            }
        }
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

    private fun loadExisting(id: String) {
        viewModelScope.launch {
            try {
                val listing = listingRepository.getListingById(id)
                _state.value = _state.value.copy(
                    title = listing.title,
                    description = listing.description,
                    price = listing.price.toString(),
                    condition = listing.condition,
                    categoryId = listing.categoryId,
                    era = listing.era ?: "",
                    material = listing.material ?: "",
                    locationCity = listing.locationCity ?: "",
                    locationState = listing.locationState ?: ""
                )
            } catch (_: Exception) {}
        }
    }
}
