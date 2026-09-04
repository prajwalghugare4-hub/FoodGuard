package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RescueStatus {
  OPEN,
  CONTACTED,
  ACCEPTED,
  PICKUP_SCHEDULED,
  PICKED_UP,
  COMPLETED,
  CANCELLED,
  EXPIRED
}

@Entity(tableName = "rescue_requests")
data class RescueRequestEntity(
  @PrimaryKey val rescueId: String,
  val providerId: String,
  val providerName: String,
  val providerPhone: String,
  val foodItems: String, // "Rice, Dal Tadka, Matar Paneer, Chapati"
  val mealType: String, // "Lunch", "Dinner", "Breakfast", "Snack"
  val quantity: String, // "18 Plates"
  val quantityNumeric: Double = 18.0,
  val preparationTime: String = "Today, fresh",
  val pickupDeadline: String, // e.g. "04:33 PM, 25 Aug"
  val pickupDeadlineTimestamp: Long,
  val pickupAddress: String,
  val additionalInstructions: String? = null,
  val verificationPin: String, // 4-digit cryptographically random PIN
  val status: RescueStatus = RescueStatus.OPEN,
  val ngoId: String? = null,
  val ngoName: String? = null,
  val ngoPhone: String? = null,
  val ngoResponse: String? = null, // "ACCEPT", "DECLINE", "REQUEST_INFO"
  val pickupTime: String? = null,
  val completedAt: Long? = null,
  val actualRescuedQuantity: Double? = null,
  val actualWastedQuantity: Double? = null,
  val createdAt: Long = System.currentTimeMillis()
)
