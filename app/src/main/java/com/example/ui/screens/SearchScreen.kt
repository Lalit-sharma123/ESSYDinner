package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteEntity
import com.example.data.model.RestaurantEntity
import com.example.data.remote.api.MapsGroundedResponse
import com.example.ui.components.GoogleMapsGroundedCard
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldOnPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.SortOption

@Composable
fun SearchScreen(
    searchQuery: String,
    activeFilters: Set<String>,
    selectedSort: SortOption,
    restaurants: List<RestaurantEntity>,
    favorites: List<FavoriteEntity>,
    selectedCity: String = "New York",
    mapsGroundedResponse: MapsGroundedResponse? = null,
    isMapsSearching: Boolean = false,
    onSearchQueryChange: (String) -> Unit,
    onToggleFilter: (String) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onClearFilters: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onBookClick: (RestaurantEntity) -> Unit,
    onMapsGroundedSearch: ((String) -> Unit)? = null,
    onClearMapsGroundedResponse: (() -> Unit)? = null
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedSearchTab by remember { mutableStateOf(0) } // 0: Standard DB Search, 1: Google Maps Grounded AI Search

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Mode Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selectedSearchTab == 0) GoldPrimary else Color(0xFF20202A),
                border = if (selectedSearchTab == 0) null else androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable { selectedSearchTab = 0 }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = if (selectedSearchTab == 0) GoldOnPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.height(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Standard DB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedSearchTab == 0) GoldOnPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selectedSearchTab == 1) GoldPrimary else Color(0xFF20202A),
                border = if (selectedSearchTab == 1) null else androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .weight(1.2f)
                    .clickable { selectedSearchTab = 1 }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍 Maps AI Grounded",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedSearchTab == 1) GoldOnPrimary else GoldPrimary
                    )
                }
            }
        }

        if (selectedSearchTab == 1) {
            // Google Maps Grounded AI Search View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                GoogleMapsGroundedCard(
                    currentCity = selectedCity,
                    mapsGroundedResponse = mapsGroundedResponse,
                    isSearching = isMapsSearching,
                    onSearch = { prompt -> onMapsGroundedSearch?.invoke(prompt) },
                    onClear = { onClearMapsGroundedResponse?.invoke() }
                )
            }
        } else {
            // Standard Search Input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search restaurant name, cuisine, area...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GoldPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips Horizontal Scroll
                val filterOptions = listOf(
                    "Pure Veg", "Rooftop", "Fine Dining", "Buffet", "Outdoor", "Pet Friendly", "Kid Friendly", "Parking"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { filter ->
                        val isSelected = activeFilters.contains(filter)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) GoldPrimary else DarkSurface,
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier.clickable { onToggleFilter(filter) }
                        ) {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GoldOnPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Header Row: Count & Sort Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${restaurants.size} Restaurants Found",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (activeFilters.isNotEmpty() || searchQuery.isNotEmpty()) {
                            Text(
                                text = "Clear All",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .clickable { onClearFilters() }
                                    .padding(end = 12.dp)
                            )
                        }

                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable { showSortMenu = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = "Sort",
                                        tint = GoldPrimary,
                                        modifier = Modifier.height(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when (selectedSort) {
                                            SortOption.RATING -> "Sort: Rating"
                                            SortOption.DISCOUNT -> "Sort: Discount"
                                            SortOption.PRICE_LOW_TO_HIGH -> "Cost: Low to High"
                                            SortOption.PRICE_HIGH_TO_LOW -> "Cost: High to Low"
                                            SortOption.DISTANCE -> "Sort: Distance"
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                SortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (option) {
                                                    SortOption.RATING -> "Highest Rating"
                                                    SortOption.DISCOUNT -> "Max Discount"
                                                    SortOption.PRICE_LOW_TO_HIGH -> "Cost: Low to High"
                                                    SortOption.PRICE_HIGH_TO_LOW -> "Cost: High to Low"
                                                    SortOption.DISTANCE -> "Distance / Name"
                                                }
                                            )
                                        },
                                        onClick = {
                                            onSortChange(option)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Restaurant Results List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    // Embed Google Maps Grounding Card option at top of standard results as well
                    GoogleMapsGroundedCard(
                        currentCity = selectedCity,
                        mapsGroundedResponse = mapsGroundedResponse,
                        isSearching = isMapsSearching,
                        onSearch = { prompt -> onMapsGroundedSearch?.invoke(prompt) },
                        onClear = { onClearMapsGroundedResponse?.invoke() }
                    )
                }

                items(restaurants) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        favoritesList = favorites,
                        onToggleFavorite = onToggleFavorite,
                        onCardClick = { onRestaurantClick(restaurant.id) },
                        onBookClick = { onBookClick(restaurant) }
                    )
                }
            }
        }
    }
}

