package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VerificationStatus {
  VERIFIED,
  UNVERIFIED,
  NEEDS_REVIEW
}

@Entity(tableName = "ngos")
data class NgoEntity(
  @PrimaryKey val ngoId: String,
  val name: String,
  val description: String,
  val address: String = "",
  val city: String,
  val state: String,
  val pincode: String = "",
  val latitude: Double = 0.0,
  val longitude: Double = 0.0,
  val serviceArea: String,
  val contactMethod: String,
  val phone: String,
  val email: String,
  val website: String,
  val sourceUrl: String = "",
  val acceptsCookedFood: Boolean = true,
  val acceptsPackedFood: Boolean = true,
  val acceptsRawFood: Boolean = false,
  val foodAcceptanceInfo: String = "Accepts Cooked & Packaged Food",
  val foodAcceptanceStatus: String = "Accepts Cooked & Packaged Food",
  val pickupAvailable: Boolean = true,
  val pickupAvailabilityStatus: String = "Pickup Available",
  val pickupWindow: String = "2-hour window",
  val capacityBadge: String = "HIGH DEMAND", // e.g. "HIGH DEMAND", "CAPACITY: 50KG+", "CONTACT TO CONFIRM"
  val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
  val lastVerifiedAt: String = "August 2026",
  val verificationNotes: String = "Official food rescue partner with verified volunteer network.",
  val distanceKm: Double = 2.4,
  val imageUrl: String? = null
)
