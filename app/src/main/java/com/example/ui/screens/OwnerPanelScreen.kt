package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.data.model.BookingEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.RestaurantEntity
import com.example.data.model.ReviewEntity
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldOnPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VegGreen

@Composable
fun OwnerPanelScreen(
    restaurant: RestaurantEntity,
    menuItems: List<MenuItemEntity>,
    bookings: List<BookingEntity>,
    reviews: List<ReviewEntity>,
    onAddMenuItem: (name: String, desc: String, category: String, price: Double, isVeg: Boolean) -> Unit,
    onDeleteMenuItem: (String) -> Unit,
    onCreateOffer: (title: String, type: String, discountPct: Int, maxDiscount: Double) -> Unit,
    onOwnerReply: (reviewId: String, reply: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Reservations", "Menu Manager", "Offer Rules", "Analytics")

    // Form states
    var newDishName by remember { mutableStateOf("") }
    var newDishDesc by remember { mutableStateOf("") }
    var newDishPrice by remember { mutableStateOf("18.0") }
    var newDishCategory by remember { mutableStateOf("Main Course") }
    var newDishIsVeg by remember { mutableStateOf(true) }

    var offerTitleInput by remember { mutableStateOf("HAPPY HOURS 40% OFF") }
    var offerPctInput by remember { mutableStateOf("40") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Owner Header Banner
        Surface(color = DarkSurface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Store, contentDescription = "Owner", tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RESTAURANT OWNER CONSOLE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = restaurant.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == idx) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Reservations
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { booking ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(booking.bookingCode, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text(booking.status, fontSize = 12.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${booking.guestCount} Guests • ${booking.bookingTimeSlot} on ${booking.bookingDate}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Seating: ${booking.seatingPref} • Occasion: ${booking.occasion}", fontSize = 12.sp, color = Color.Gray)
                                if (booking.specialRequests.isNotBlank()) {
                                    Text("Note: ${booking.specialRequests}", fontSize = 11.sp, color = GoldPrimary)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Menu Manager
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Add New Menu Item", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newDishName,
                                onValueChange = { newDishName = it },
                                label = { Text("Dish Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_dish_name")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newDishDesc,
                                onValueChange = { newDishDesc = it },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                OutlinedTextField(
                                    value = newDishPrice,
                                    onValueChange = { newDishPrice = it },
                                    label = { Text("Price ($)") },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = newDishCategory,
                                    onValueChange = { newDishCategory = it },
                                    label = { Text("Category") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (newDishName.isNotBlank()) {
                                        onAddMenuItem(newDishName, newDishDesc, newDishCategory, newDishPrice.toDoubleOrNull() ?: 18.0, newDishIsVeg)
                                        newDishName = ""
                                        newDishDesc = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Dish to Menu", color = GoldOnPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("CURRENT MENU DINES", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)

                    Spacer(modifier = Modifier.height(10.dp))

                    menuItems.forEach { item ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text("$${item.price} • ${item.category}", fontSize = 12.sp, color = GoldPrimary)
                                }

                                IconButton(onClick = { onDeleteMenuItem(item.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Offer Rules
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Create Custom Dining Discount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = offerTitleInput,
                                onValueChange = { offerTitleInput = it },
                                label = { Text("Offer Title") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = offerPctInput,
                                onValueChange = { offerPctInput = it },
                                label = { Text("Discount Percentage (%)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onCreateOffer(offerTitleInput, "PERCENTAGE", offerPctInput.toIntOrNull() ?: 30, 50.0)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Publish Offer to Platform", color = GoldOnPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            else -> {
                // Analytics
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("THIS MONTH PERFORMANCE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Total Table Bookings: 148", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Net Revenue Generated: $12,450.00", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                            Text("Avg Rating: 4.9 ★", fontSize = 14.sp, color = GoldPrimary)
                        }
                    }
                }
            }
        }
    }
}
