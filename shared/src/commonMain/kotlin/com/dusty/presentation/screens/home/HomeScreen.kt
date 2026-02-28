package com.dusty.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dusty.data.model.Category
import com.dusty.data.model.Listing
import com.dusty.presentation.components.CategoryChip
import com.dusty.presentation.components.DustySearchBar
import com.dusty.presentation.components.ListingCard
import com.dusty.presentation.routes.BrowseCategoryRoute
import com.dusty.presentation.routes.BrowseRoute
import com.dusty.presentation.routes.ListingDetailRoute
import com.dusty.util.Resource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier.padding(top = 48.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Dusty",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = "Discover Timeless Treasures",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Search
        item {
            DustySearchBar(
                query = state.searchQuery,
                onQueryChanged = viewModel::onSearchQueryChanged,
                onSearch = { navController.navigate(BrowseRoute) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Categories
        item {
            SectionHeader(title = "Categories", modifier = Modifier.padding(horizontal = 16.dp))
        }

        item {
            when (val categories = state.categories) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                is Resource.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(categories.data) { category ->
                            CategoryChip(
                                category = category,
                                onClick = {
                                    navController.navigate(BrowseCategoryRoute(category.id))
                                }
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = categories.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Featured Listings
        item {
            SectionHeader(
                title = "Featured",
                actionText = "See All",
                onAction = { navController.navigate(BrowseRoute) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            when (val featured = state.featuredListings) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featured.data) { listing ->
                            ListingCard(
                                listing = listing,
                                onClick = {
                                    navController.navigate(ListingDetailRoute(listing.id))
                                },
                                modifier = Modifier.width(220.dp)
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = featured.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // Recent Listings
        item {
            SectionHeader(
                title = "Recently Added",
                actionText = "Browse All",
                onAction = { navController.navigate(BrowseRoute) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        when (val recent = state.recentListings) {
            is Resource.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is Resource.Success -> {
                items(recent.data.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (listing in rowItems) {
                            ListingCard(
                                listing = listing,
                                onClick = {
                                    navController.navigate(ListingDetailRoute(listing.id))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            is Resource.Error -> {
                item {
                    Text(
                        text = recent.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(text = actionText)
            }
        }
    }
}
