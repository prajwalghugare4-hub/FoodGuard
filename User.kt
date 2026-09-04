package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
  PROVIDER,
  RECEIVER
}

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val userId: String,
  val fullName: String,
  val email: String,
  val phoneNumber: String,
  val passwordHash: String = "",
  val role: UserRole,
  val address: String,
  val city: String,
  val state: String,
  val pinCode: String,
  val establishmentName: String? = null,
  val establishmentType: String? = null, // "Mess", "Canteen", "Hotel", "Restaurant", "Other"
  val profileImageUri: String? = null,
  val isGoogleAccount: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)
