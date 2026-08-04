package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldOnPrimary
import com.example.ui.theme.GoldPrimary

@Composable
fun MembershipScreen(
    currentTier: String,
    rewardPoints: Int,
    onUpgradeTier: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Active Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2C1E08), Color(0xFF5E3F0F), Color(0xFF2C1E08))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "VIP", tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ACTIVE MEMBERSHIP: $currentTier", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("DineReserve Reward Balance: $rewardPoints Points", fontSize = 14.sp, color = Color.White)
                Text("Valid until Dec 31, 2026 • Unlimited Dining Benefits", fontSize = 12.sp, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("MEMBERSHIP PLANS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)

        Spacer(modifier = Modifier.height(12.dp))

        val plans = listOf(
            Triple("Silver VIP", "$19 / yr", listOf("Up to 30% OFF Dining Bills", "Standard Slot Reservations", "1x Reward Points")),
            Triple("Gold VIP", "$49 / yr", listOf("Up to 50% OFF Dining Bills", "Priority Peak Slot Reservations", "2x Reward Points", "Free Birthday Dessert")),
            Triple("Platinum VIP", "$99 / yr", listOf("Flat 50% OFF Guaranteed", "Dedicated Concierge Table Pass", "3x Reward Points", "Valet Parking Voucher", "Exclusive Chef Tastings"))
        )

        plans.forEach { (title, price, perks) ->
            val isCurrent = currentTier == title

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(if (isCurrent) 2.dp else 1.dp, if (isCurrent) GoldPrimary else DarkCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Text(price, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }

                        if (isCurrent) {
                            Surface(color = GoldPrimary, shape = RoundedCornerShape(12.dp)) {
                                Text("Current Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldOnPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    perks.forEach { perk ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Perk", tint = GoldPrimary, modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(perk, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (!isCurrent) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onUpgradeTier(title) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upgrade to $title", color = GoldOnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
