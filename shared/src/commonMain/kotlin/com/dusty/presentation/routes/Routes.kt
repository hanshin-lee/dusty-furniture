package com.dusty.presentation.routes

import kotlinx.serialization.Serializable

@Serializable object HomeRoute
@Serializable object BrowseRoute
@Serializable data class BrowseCategoryRoute(val categoryId: String)
@Serializable data class ListingDetailRoute(val listingId: String)
@Serializable object CartRoute
@Serializable object CheckoutRoute
@Serializable object LoginRoute
@Serializable object RegisterRoute
@Serializable object ProfileRoute
@Serializable object SellerDashboardRoute
@Serializable data class CreateListingRoute(val editListingId: String? = null)
@Serializable object AdminDashboardRoute
