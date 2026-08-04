package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteEntity
import com.example.data.model.RestaurantEntity
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.GoldPrimary

@Composable
fun FavoritesReviewsScreen(
    favorites: List<FavoriteEntity>,
    restaurants: List<RestaurantEntity>,
    onToggleFavorite: (String) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onBookClick: (RestaurantEntity) -> Unit
) {
    val favRestaurants = restaurants.filter { rest -> favorites.any { it.restaurantId == rest.id } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Saved", tint = GoldPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SAVED RESTAURANTS (${favRestaurants.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(favRestaurants) { restaurant ->
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
