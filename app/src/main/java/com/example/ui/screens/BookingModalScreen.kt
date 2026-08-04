package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldOnPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.BookingDraft

@Composable
fun BookingModalScreen(
    draft: BookingDraft,
    onDraftUpdate: (guests: Int?, date: String?, slot: String?, occasion: String?, seating: String?, special: String?) -> Unit,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = GoldPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Reserve a Table",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = draft.restaurantName,
                    fontSize = 13.sp,
                    color = GoldPrimary
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Number of Guests
            Text(
                text = "NUMBER OF GUESTS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            val guestCounts = listOf(1, 2, 3, 4, 5, 6, 8, 10)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(guestCounts) { count ->
                    val isSelected = draft.guestCount == count
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) GoldPrimary else DarkSurface)
                            .border(1.dp, if (isSelected) GoldPrimary else DarkCardBorder, CircleShape)
                            .clickable { onDraftUpdate(count, null, null, null, null, null) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) GoldOnPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Booking Date Picker
            Text(
                text = "SELECT DATE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            val dates = listOf(
                "2026-08-01" to "Today, Aug 1",
                "2026-08-02" to "Tomorrow, Aug 2",
                "2026-08-03" to "Mon, Aug 3",
                "2026-08-04" to "Tue, Aug 4"
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(dates) { (dateCode, label) ->
                    val isSelected = draft.bookingDate == dateCode
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) GoldPrimary else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GoldPrimary else DarkCardBorder),
                        modifier = Modifier.clickable { onDraftUpdate(null, dateCode, null, null, null, null) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldOnPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Available Time Slots
            Text(
                text = "SELECT TIME SLOT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            val timeSlots = listOf(
                "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM",
                "07:00 PM", "07:30 PM", "08:00 PM", "08:30 PM", "09:00 PM"
            )

            Column {
                timeSlots.chunked(3).forEach { chunk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chunk.forEach { slot ->
                            val isSelected = draft.bookingTimeSlot == slot
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GoldPrimary else DarkSurface)
                                    .border(1.dp, if (isSelected) GoldPrimary else DarkCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { onDraftUpdate(null, null, slot, null, null, null) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = slot,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GoldOnPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Occasion & Seating Preference
            Text(
                text = "DINING OCCASION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            val occasions = listOf("Casual Dining", "Birthday", "Anniversary", "Date Night", "Business Meeting")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(occasions) { occ ->
                    val isSelected = draft.occasion == occ
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) GoldPrimary else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.clickable { onDraftUpdate(null, null, null, occ, null, null) }
                    ) {
                        Text(
                            text = occ,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldOnPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Seating Preference
            Text(
                text = "SEATING PREFERENCE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            val seatings = listOf("Standard Table", "Rooftop Window", "Outdoor Patio", "Private Booth")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(seatings) { seat ->
                    val isSelected = draft.seatingPref == seat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) GoldPrimary else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.clickable { onDraftUpdate(null, null, null, null, seat, null) }
                    ) {
                        Text(
                            text = seat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldOnPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Special Requests Input
            Text(
                text = "SPECIAL INSTRUCTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = draft.specialRequests,
                onValueChange = { onDraftUpdate(null, null, null, null, null, it) },
                placeholder = { Text("e.g. Quiet corner, high chair, birthday candle...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_special_requests")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Applied Offer Summary Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = "Applied Discount",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = draft.selectedOfferTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val rawEst = draft.estimatedCostForTwo
                    val discAmt = rawEst * (draft.discountPercent / 100.0)
                    val netPayable = rawEst - discAmt

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Est. Dining Bill:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("$$rawEst", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Offer Discount (${draft.discountPercent}%):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("-$$discAmt", fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Net Estimated Payable:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("$$netPayable", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = GoldPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Confirm Reservation Button
            Button(
                onClick = onConfirmClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_confirm_reservation")
            ) {
                Text(
                    text = "Confirm Reservation & Claim Offer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldOnPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
