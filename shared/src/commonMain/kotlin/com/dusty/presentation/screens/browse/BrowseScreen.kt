package com.dusty.presentation.screens.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dusty.data.model.Category
import com.dusty.presentation.components.CategoryChip
import com.dusty.presentation.components.DustySearchBar
import com.dusty.presentation.components.ListingCard
import com.dusty.presentation.routes.ListingDetailRoute
import com.dusty.util.Resource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    navController: NavController,
    initialCategoryId: String? = null
) {
    val viewModel = koinViewModel<BrowseViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(initialCategoryId) {
        if (initialCategoryId != null) {
            viewModel.setCategoryFilter(initialCategoryId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Search
            item {
                DustySearchBar(
                    query = state.searchQuery,
                    onQueryChanged = viewModel::setSearchQuery,
                    onSearch = { viewModel.search() },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Category chips
            item {
                when (val categories = state.categories) {
                    is Resource.Success -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            item {
                                CategoryChip(
                                    category = Category(id = "", name = "All", slug = "all"),
                                    isSelected = state.selectedCategoryId == null,
                                    onClick = { viewModel.setCategoryFilter(null) }
                                )
                            }
                            items(categories.data) { category ->
                                CategoryChip(
                                    category = category,
                                    isSelected = state.selectedCategoryId == category.id,
                                    onClick = { viewModel.setCategoryFilter(category.id) }
                                )
                            }
                        }
                    }
                    else -> {}
                }
            }

            // Sort
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SortOption.entries.forEach { sort ->
                        FilterChip(
                            selected = state.sortBy == sort,
                            onClick = { viewModel.setSortOption(sort) },
                            label = { Text(sort.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Listings grid
            when (val listings = state.listings) {
                is Resource.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is Resource.Success -> {
                    if (listings.data.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No items found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(listings.data.chunked(2)) { rowItems ->
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
                }
                is Resource.Error -> {
                    item {
                        Text(
                            text = listings.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
