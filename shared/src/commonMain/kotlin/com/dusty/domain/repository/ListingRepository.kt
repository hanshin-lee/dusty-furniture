package com.dusty.domain.repository

import com.dusty.data.model.Listing
import com.dusty.data.model.ListingStatus

interface ListingRepository {
    suspend fun getActiveListings(categoryId: String? = null, limit: Int = 20, offset: Int = 0): List<Listing>
    suspend fun getFeaturedListings(limit: Int = 10): List<Listing>
    suspend fun searchListings(query: String): List<Listing>
    suspend fun getListingById(id: String): Listing
    suspend fun getListingsBySeller(sellerId: String): List<Listing>
    suspend fun getListingsByStatus(status: ListingStatus): List<Listing>
    suspend fun createListing(listing: Listing): Listing
    suspend fun updateListing(listing: Listing): Listing
    suspend fun uploadImage(listingId: String, fileName: String, data: ByteArray): String
}
