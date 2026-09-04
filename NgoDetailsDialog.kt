package com.example.ui.screens.ngos

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun NgoDetailsDialog(
  ngo: NgoEntity,
  onDismiss: () -> Unit,
  onDonateClick: (NgoEntity) -> Unit
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  AlertDialog(
    onDismissRequest = onDismiss,
    title = null,
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Top Banner Image
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
          val imgRes = if (ngo.name.contains("Robin", ignoreCase = true)) R.drawable.img_ngo_shelter else R.drawable.img_ngo_pantry
          Image(
            painter = painterResource(id = imgRes),
            contentDescription = ngo.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          // Verified Pill
          Box(
            modifier = Modifier
              .padding(10.dp)
              .align(Alignment.TopEnd)
              .clip(RoundedCornerShape(6.dp))
              .background(if (ngo.verificationStatus == VerificationStatus.VERIFIED) EcoGreenPrimary else MaterialTheme.colorScheme.tertiary)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = ngo.verificationStatus.name,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Title & Description
        Text(
          text = ngo.name,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = ngo.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HorizontalDivider()

        // Logistics & Capabilities
        Text(
          text = "RESCUE CAPABILITIES & ACCEPTANCE",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          CapabilityChip(label = "Cooked Food", enabled = ngo.acceptsCookedFood)
          CapabilityChip(label = "Packaged", enabled = ngo.acceptsPackedFood)
          CapabilityChip(label = "Raw Grains", enabled = ngo.acceptsRawFood)
        }

        // Food Acceptance & Pickup Availability Status Rows
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Food Acceptance:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = ngo.foodAcceptanceStatus,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (ngo.foodAcceptanceStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.tertiary else EcoGreenPrimary
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Pickup Availability:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = ngo.pickupAvailabilityStatus,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (ngo.pickupAvailabilityStatus.contains("Contact", ignoreCase = true)) MaterialTheme.colorScheme.tertiary else EcoGreenPrimary
            )
          }
        }

        // Full Address & Coordinates
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = if (ngo.address.isNotEmpty()) ngo.address else "${ngo.city}, ${ngo.state}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
              )
              if (ngo.latitude != 0.0 && ngo.longitude != 0.0) {
                Text(
                  text = "Coordinates: ${ngo.latitude}, ${ngo.longitude} (${ngo.distanceKm} km away)",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              } else {
                Text(
                  text = "${ngo.distanceKm} km away",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Text(
          text = "Pickup Window: ${ngo.pickupWindow}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Verification & Audit Notes Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
          shape = RoundedCornerShape(8.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Verification Audit (${ngo.lastVerifiedAt})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = EcoGreenPrimary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = ngo.verificationNotes,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Contact Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${ngo.phone}"))
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "Dialer unavailable", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Call", style = MaterialTheme.typography.labelSmall)
          }

          OutlinedButton(
            onClick = {
              try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ngo.website))
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "Web browser unavailable", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Website", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onDismiss()
          onDonateClick(ngo)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = EcoGreenPrimary,
          contentColor = OnEcoGreen
        ),
        modifier = Modifier.testTag("btn_ngo_donate_now")
      ) {
        Text("Donate Food to ${ngo.name.take(12)}")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Close")
      }
    }
  )
}

@Composable
fun CapabilityChip(label: String, enabled: Boolean) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (enabled) EcoGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceContainerHigh)
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
      color = if (enabled) EcoGreenPrimary else MaterialTheme.colorScheme.outline,
      fontWeight = if (enabled) FontWeight.Bold else FontWeight.Normal
    )
  }
}
