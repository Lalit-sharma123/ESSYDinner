package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FavoriteEntity
import com.example.data.model.OfferEntity
import com.example.data.model.RestaurantEntity
import com.example.ui.components.RestaurantCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldOnPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.CustomerScreen

@Composable
fun HomeScreen(
    restaurants: List<RestaurantEntity>,
    offers: List<OfferEntity>,
    favorites: List<FavoriteEntity>,
    membershipTier: String,
    rewardPoints: Int,
    selectedCategoryTag: String?,
    onCategoryTagSelect: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onBookClick: (RestaurantEntity) -> Unit,
    onSearchClick: () -> Unit,
    onOffersHubClick: () -> Unit,
    onMembershipClick: () -> Unit,
    onWaitlistClick: () -> Unit = {},
    onQrDiningClick: () -> Unit = {},
    onCorporateClick: () -> Unit = {},
    onCrmClick: () -> Unit = {},
    onMenuBuilderClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Search Trigger Bar
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .clickable { onSearchClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search cuisines, restaurants...",
                    color = Color(0xFF4B5563),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Pills Horizontal Row
        val categories = listOf("All", "Italian", "Japanese", "French", "Fine Dining", "Rooftop", "Buffet", "Pure Veg")

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { category ->
                val isSelected = (selectedCategoryTag == category) || (category == "All" && selectedCategoryTag == null)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) GoldPrimary else DarkSurface,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.clickable {
                        if (category == "All") onCategoryTagSelect("") else onCategoryTagSelect(category)
                    }
                ) {
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.Black else Color(0xFF9E9E9E),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Enterprise Features Quick Navigation Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onWaitlistClick() }
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⏳ Waitlist", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Text("Live Queue", fontSize = 9.sp, color = Color.Gray)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onQrDiningClick() }
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📱 QR Dining", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Text("Table Order", fontSize = 9.sp, color = Color.Gray)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCorporateClick() }
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏢 Corporate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Text("Business Pay", fontSize = 9.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gold Member Benefit Banner
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .border(1.dp, GoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .clickable { onMembershipClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GoldPrimary, Color(0xFF926F3D))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Gold Member Benefit",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Get a complimentary glass of wine with any main course today.",
                            fontSize = 11.sp,
                            color = Color(0xFF9E9E9E),
                            maxLines = 2
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Membership",
                    tint = GoldPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Restaurants Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Restaurants",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "View All",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                modifier = Modifier.clickable { onSearchClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val featured = restaurants.filter { it.isFeatured }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(featured) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    favoritesList = favorites,
                    onToggleFavorite = onToggleFavorite,
                    onCardClick = { onRestaurantClick(restaurant.id) },
                    onBookClick = { onBookClick(restaurant) },
                    modifier = Modifier.width(280.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dining Offers Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = "Offers",
                    tint = GoldPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Exclusive Dining Offers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "All Offers",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                modifier = Modifier.clickable { onOffersHubClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(offers) { offer ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier
                        .width(260.dp)
                        .clickable { onOffersHubClick() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = offer.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = offer.terms,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nearby Restaurants
        Text(
            text = "Popular & Nearby",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            restaurants.forEach { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    favoritesList = favorites,
                    onToggleFavorite = onToggleFavorite,
                    onCardClick = { onRestaurantClick(restaurant.id) },
                    onBookClick = { onBookClick(restaurant) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
