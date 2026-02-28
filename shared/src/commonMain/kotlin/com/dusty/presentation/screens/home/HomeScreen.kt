package com.dusty.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero header with gradient
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(top = 56.dp, bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var titleVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { titleVisible = true }

                    val titleAlpha by animateFloatAsState(
                        targetValue = if (titleVisible) 1f else 0f,
                        animationSpec = tween(600, easing = EaseOutCubic)
                    )
                    val titleOffset by animateFloatAsState(
                        targetValue = if (titleVisible) 0f else 20f,
                        animationSpec = tween(600, easing = EaseOutCubic)
                    )

                    Text(
                        text = "Dusty",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = titleAlpha
                                translationY = titleOffset
                            }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Discover Timeless Treasures",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = titleAlpha
                                translationY = titleOffset * 0.5f
                            }
                    )
                }
            }
        }

        // Search
        item {
            DustySearchBar(
                query = state.searchQuery,
                onQueryChanged = viewModel::onSearchQueryChanged,
                onSearch = { navController.navigate(BrowseRoute) },
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        // Categories
        item {
            SectionHeader(
                title = "Categories",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            when (val categories = state.categories) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
                is Resource.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(bottom = 20.dp)
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
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
                is Resource.Success -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(featured.data) { index, listing ->
                            var appeared by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { appeared = true }
                            val slideOffset by animateFloatAsState(
                                targetValue = if (appeared) 0f else 40f,
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 60,
                                    easing = EaseOutCubic
                                )
                            )

                            ListingCard(
                                listing = listing,
                                onClick = {
                                    navController.navigate(ListingDetailRoute(listing.id))
                                },
                                modifier = Modifier
                                    .width(220.dp)
                                    .graphicsLayer { translationX = slideOffset }
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        when (val recent = state.recentListings) {
            is Resource.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
            is Resource.Success -> {
                itemsIndexed(recent.data.chunked(2)) { index, rowItems ->
                    var appeared by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { appeared = true }
                    val rowAlpha by animateFloatAsState(
                        targetValue = if (appeared) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 350,
                            delayMillis = index * 80,
                            easing = EaseOutCubic
                        )
                    )
                    val rowOffset by animateFloatAsState(
                        targetValue = if (appeared) 0f else 24f,
                        animationSpec = tween(
                            durationMillis = 350,
                            delayMillis = index * 80,
                            easing = EaseOutCubic
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .graphicsLayer {
                                alpha = rowAlpha
                                translationY = rowOffset
                            },
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
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
