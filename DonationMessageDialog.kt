package com.example.ui.screens.rescue

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NgoEntity
import com.example.data.model.RescueRequestEntity
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen
import com.example.ui.util.SmsAlertDispatcher

@Composable
fun DonationMessageDialog(
  rescue: RescueRequestEntity,
  ngo: NgoEntity?,
  messageContent: String,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var dispatchStatus by remember { mutableStateOf<String?>("Message Ready to Send") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Message,
          contentDescription = null,
          tint = EcoGreenPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (ngo != null) "Alert: ${ngo.name}" else "Food Rescue Alert",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Status Badge: Message Ready to Send
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(EcoGreenPrimary.copy(alpha = 0.12f))
            .border(1.dp, EcoGreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = EcoGreenPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = dispatchStatus ?: "Message Ready to Send",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = EcoGreenPrimary
            )
          }
        }

        if (ngo != null) {
          Text(
            text = "Recipient: ${ngo.phone} • ${ngo.name}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        // Formatted Message Container Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Text(
            text = messageContent,
            style = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              lineHeight = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        if (ngo != null) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Helpline: ${ngo.phone}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
              onClick = {
                try {
                  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${ngo.phone}"))
                  context.startActivity(intent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Dialer unavailable", Toast.LENGTH_SHORT).show()
                }
              },
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Call NGO", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val phone = ngo?.phone ?: ""
          val result = SmsAlertDispatcher.openSmsApp(context, phone, messageContent)
          dispatchStatus = result.statusMessage
          Toast.makeText(context, result.statusMessage, Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = EcoGreenPrimary,
          contentColor = OnEcoGreen
        ),
        modifier = Modifier.testTag("btn_open_sms_app")
      ) {
        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Open SMS App")
      }
    },
    dismissButton = {
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Food Rescue Alert", messageContent)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Alert copied to clipboard!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.testTag("btn_copy_alert")
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Copy")
        }

        OutlinedButton(
          onClick = onDismiss
        ) {
          Text("Close")
        }
      }
    }
  )
}
