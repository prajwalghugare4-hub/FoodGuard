package com.example.ui.screens.rescue

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RescueRequestEntity
import com.example.data.model.RescueStatus
import com.example.ui.theme.AlertRed
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen
import com.example.ui.theme.RescueOrangeContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescueListScreen(
  viewModel: RescueViewModel,
  onCreateRescueClick: () -> Unit
) {
  val context = LocalContext.current
  val rescues by viewModel.providerRescues.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }
  var verifyingRescue by remember { mutableStateOf<RescueRequestEntity?>(null) }
  var messagingRescue by remember { mutableStateOf<RescueRequestEntity?>(null) }

  val activeRescues = rescues.filter {
    it.status != RescueStatus.COMPLETED && it.status != RescueStatus.CANCELLED && it.status != RescueStatus.EXPIRED
  }
  val completedRescues = rescues.filter {
    it.status == RescueStatus.COMPLETED || it.status == RescueStatus.CANCELLED || it.status == RescueStatus.EXPIRED
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Food Rescues",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onCreateRescueClick,
        containerColor = EcoGreenPrimary,
        contentColor = OnEcoGreen,
        modifier = Modifier.testTag("fab_create_rescue")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Create Rescue")
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Active (${activeRescues.size})", fontWeight = FontWeight.SemiBold) },
          modifier = Modifier.testTag("tab_active_rescues")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("History (${completedRescues.size})", fontWeight = FontWeight.SemiBold) },
          modifier = Modifier.testTag("tab_history_rescues")
        )
      }

      val displayList = if (selectedTab == 0) activeRescues else completedRescues

      if (displayList.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.VolunteerActivism,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outlineVariant,
              modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = if (selectedTab == 0) "No active rescue listings" else "No past rescues recorded yet",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (selectedTab == 0) "Tap the + button to declare surplus food for NGO recovery." else "Completed and verified rescues will appear here.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(displayList) { rescue ->
            RescueItemCard(
              rescue = rescue,
              onVerifyPinClick = { verifyingRescue = rescue },
              onDonationMessageClick = { messagingRescue = rescue }
            )
          }
        }
      }
    }
  }

  // Verification Dialog
  verifyingRescue?.let { rescue ->
    VerificationPinDialog(
      rescue = rescue,
      onDismiss = { verifyingRescue = null },
      onVerify = { enteredPin, rescued, wasted ->
        viewModel.verifyPickupPin(rescue.rescueId, enteredPin, rescued, wasted) { result ->
          result.fold(
            onSuccess = {
              Toast.makeText(context, "Pickup verified successfully!", Toast.LENGTH_LONG).show()
              verifyingRescue = null
            },
            onFailure = { error ->
              Toast.makeText(context, error.message ?: "Verification failed", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }
    )
  }

  // Donation Message Dialog
  messagingRescue?.let { rescue ->
    val msg = viewModel.formatStructuredDonationMessage(
      rescue = rescue,
      providerName = currentUser?.establishmentName ?: "Provider Kitchen",
      providerPhone = currentUser?.phoneNumber ?: "+91 98765 43210"
    )
    DonationMessageDialog(
      rescue = rescue,
      ngo = null,
      messageContent = msg,
      onDismiss = { messagingRescue = null }
    )
  }
}

@Composable
fun RescueItemCard(
  rescue: RescueRequestEntity,
  onVerifyPinClick: () -> Unit,
  onDonationMessageClick: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Food items & Status badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = rescue.quantity + " • " + rescue.mealType,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = rescue.foodItems,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
          )
        }

        // Status Badge
        val (badgeBg, badgeFg) = when (rescue.status) {
          RescueStatus.OPEN -> Pair(EcoGreenPrimary.copy(alpha = 0.15f), EcoGreenPrimary)
          RescueStatus.CONTACTED -> Pair(RescueOrangeContainer.copy(alpha = 0.2f), RescueOrangeContainer)
          RescueStatus.ACCEPTED -> Pair(Color(0xFFE8F5E9), EcoGreenPrimary)
          RescueStatus.PICKUP_SCHEDULED -> Pair(Color(0xFFE3F2FD), Color(0xFF1976D2))
          RescueStatus.COMPLETED -> Pair(Color(0xFFE8F5E9), EcoGreenPrimary)
          RescueStatus.EXPIRED -> Pair(MaterialTheme.colorScheme.errorContainer, AlertRed)
          else -> Pair(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = rescue.status.name.replace("_", " "),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = badgeFg,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Deadline & Location
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Schedule,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Deadline: ${rescue.pickupDeadline}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Place,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = rescue.pickupAddress,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1
        )
      }

      // If active, show PIN badge & Actions
      if (rescue.status != RescueStatus.COMPLETED && rescue.status != RescueStatus.EXPIRED) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Lock,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Pickup PIN: ",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = rescue.verificationPin,
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Text(
            text = "Keep Secure",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.outline
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDonationMessageClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Send Alert", style = MaterialTheme.typography.labelSmall)
          }

          Button(
            onClick = onVerifyPinClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = EcoGreenPrimary,
              contentColor = OnEcoGreen
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Verify PIN", style = MaterialTheme.typography.labelSmall)
          }
        }
      } else if (rescue.status == RescueStatus.COMPLETED) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE8F5E9))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Successfully rescued ${rescue.actualRescuedQuantity?.toInt() ?: rescue.quantityNumeric.toInt()} plates",
            style = MaterialTheme.typography.labelSmall,
            color = EcoGreenPrimary,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
