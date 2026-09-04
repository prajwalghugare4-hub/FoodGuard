package com.example.ui.screens.rescue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RescueRequestEntity
import com.example.ui.theme.AlertRed
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.OnEcoGreen

@Composable
fun VerificationPinDialog(
  rescue: RescueRequestEntity,
  onDismiss: () -> Unit,
  onVerify: (enteredPin: String, actualRescued: Double, actualWasted: Double) -> Unit
) {
  var enteredPin by remember { mutableStateOf("") }
  var actualRescuedText by remember { mutableStateOf(rescue.quantityNumeric.toInt().toString()) }
  var actualWastedText by remember { mutableStateOf("0") }
  var errorText by remember { mutableStateOf<String?>(null) }
  var isSubmitting by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          tint = EcoGreenPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Verify Pickup PIN",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Enter the 4-digit PIN provided by the NGO collector to verify physical handover and complete this rescue.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (errorText != null) {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = AlertRed, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = errorText ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
            }
          }
        }

        OutlinedTextField(
          value = enteredPin,
          onValueChange = {
            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
              enteredPin = it
              errorText = null
            }
          },
          label = { Text("4-Digit Pickup PIN *") },
          placeholder = { Text("e.g. 1700") },
          textStyle = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Monospace,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Bold
          ),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_verification_pin"),
          shape = RoundedCornerShape(10.dp)
        )

        Text(
          text = "RECORD OUTCOME (FEEDBACK LOOP)",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(top = 4.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = actualRescuedText,
            onValueChange = { actualRescuedText = it },
            label = { Text("Rescued (Plates)") },
            modifier = Modifier
              .weight(1f)
              .testTag("input_actual_rescued"),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )

          OutlinedTextField(
            value = actualWastedText,
            onValueChange = { actualWastedText = it },
            label = { Text("Wasted (Plates)") },
            modifier = Modifier
              .weight(1f)
              .testTag("input_actual_wasted"),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (enteredPin.length != 4) {
            errorText = "Please enter complete 4-digit PIN."
            return@Button
          }
          val rescued = actualRescuedText.toDoubleOrNull() ?: rescue.quantityNumeric
          val wasted = actualWastedText.toDoubleOrNull() ?: 0.0
          isSubmitting = true
          onVerify(enteredPin, rescued, wasted)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = EcoGreenPrimary,
          contentColor = OnEcoGreen
        ),
        modifier = Modifier.testTag("btn_confirm_verify_pin")
      ) {
        if (isSubmitting) {
          CircularProgressIndicator(modifier = Modifier.size(18.dp), color = OnEcoGreen)
        } else {
          Text("Verify & Complete")
        }
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
