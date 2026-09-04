package com.example.ui.screens.ngos

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NgoEntity
import com.example.data.model.VerificationStatus
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NgoListScreen(
  viewModel: NgoViewModel,
  onDonateToNgo: (NgoEntity) -> Unit
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()
  var viewingNgo by remember { mutableStateOf<NgoEntity?>(null) }
  val filterScrollState = rememberScrollState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "FOODGUARD",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Nearby NGOs",
          style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Find verified organizations to donate surplus food.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Search Bar
        OutlinedTextField(
          value = uiState.searchQuery,
          onValueChange = { viewModel.onSearchQueryChange(it) },
          placeholder = { Text("Search by organization or city...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
          },
          trailingIcon = {
            if (uiState.searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_search_ngos"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Filter Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(filterScrollState),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = uiState.selectedFilter == NgoFilter.ALL,
            onClick = { viewModel.onFilterSelect(NgoFilter.ALL) },
            label = { Text("All Types") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EcoGreenPrimary,
              selectedLabelColor = OnEcoGreen
            )
          )
          FilterChip(
            selected = uiState.selectedFilter == NgoFilter.COOKED,
            onClick = { viewModel.onFilterSelect(NgoFilter.COOKED) },
            label = { Text("Cooked Food") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EcoGreenPrimary,
              selectedLabelColor = OnEcoGreen
            )
          )
          FilterChip(
            selected = uiState.selectedFilter == NgoFilter.RAW,
            onClick = { viewModel.onFilterSelect(NgoFilter.RAW) },
            label = { Text("Raw Ingredients") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EcoGreenPrimary,
              selectedLabelColor = OnEcoGreen
            )
          )
          FilterChip(
            selected = uiState.selectedFilter == NgoFilter.PACKAGED,
            onClick = { viewModel.onFilterSelect(NgoFilter.PACKAGED) },
            label = { Text("Packaged Goods") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EcoGreenPrimary,
              selectedLabelColor = OnEcoGreen
            )
          )
          FilterChip(
            selected = uiState.selectedFilter == NgoFilter.PICKUP_ONLY,
            onClick = { viewModel.onFilterSelect(NgoFilter.PICKUP_ONLY) },
            label = { Text("Pickup Available") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = EcoGreenPrimary,
              selectedLabelColor = OnEcoGreen
            )
          )
        }
      }

      // NGO Cards
      items(uiState.filteredNgos) { ngo ->
        NgoDirectoryCard(
          ngo = ngo,
          onViewDetails = { viewingNgo = ngo },
          onDonate = { onDonateToNgo(ngo) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  viewingNgo?.let { ngo ->
    NgoDetailsDialog(
      ngo = ngo,
      onDismiss = { viewingNgo = null },
      onDonateClick = { onDonateToNgo(it) }
    )
  }
}

@Composable
fun NgoDirectoryCard(
  ngo: NgoEntity,
  onViewDetails: () -> Unit,
  onDonate: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
    border = CardDefaults.outlinedCardBorder(),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column {
      // Header Image / Banner with Distance badge
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
      ) {
        val imageRes = if (ngo.name.contains("Robin", ignoreCase = true)) {
          R.drawable.img_ngo_shelter
        } else {
          R.drawable.img_ngo_pantry
        }

        Image(
          painter = painterResource(id = imageRes),
          contentDescription = ngo.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Distance Tag
        Box(
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(6.dp))
            .background(EcoGreenPrimary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${ngo.distanceKm} km away",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Body Info
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = ngo.name,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            if (ngo.verificationStatus == VerificationStatus.VERIFIED) {
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified NGO",
                tint = EcoGreenPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = ngo.capacityBadge,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = ngo.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (ngo.address.isNotEmpty()) "📍 ${ngo.address}" else "📍 ${ngo.serviceArea}",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (ngo.foodAcceptanceStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "Accepts: ${ngo.foodAcceptanceStatus}",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = if (ngo.foodAcceptanceStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.primary
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(if (ngo.pickupAvailabilityStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.tertiaryContainer else EcoGreenPrimary.copy(alpha = 0.12f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = ngo.pickupAvailabilityStatus,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
              color = if (ngo.pickupAvailabilityStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.onTertiaryContainer else EcoGreenPrimary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onViewDetails,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("VIEW DETAILS", style = MaterialTheme.typography.labelSmall)
          }

          Button(
            onClick = onDonate,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = EcoGreenPrimary,
              contentColor = OnEcoGreen
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("DONATE FOOD", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          }
        }
      }
    }
  }
}
