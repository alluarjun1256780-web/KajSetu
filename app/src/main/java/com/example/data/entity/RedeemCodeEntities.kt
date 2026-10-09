package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "redeem_codes")
data class RedeemCodeEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val code: String, // Upper-cased unique code e.g. "BONUS100", "TGDAILY50"
  val bonusAmount: Double, // Amount to be added to user's wallet
  val maxUses: Int = 50, // Maximum total uses allowed
  val usedCount: Int = 0, // Current total times redeemed
  val isActive: Boolean = true, // Active/inactive toggle
  val createdBy: String = "admin",
  val createdAt: Long = System.currentTimeMillis(),
  val expiryDate: Long = 0L // 0 means no expiration
)

@Entity(
  tableName = "redeem_code_usages",
  primaryKeys = ["codeId", "userId"]
)
data class RedeemCodeUsageEntity(
  val codeId: String,
  val userId: String,
  val code: String,
  val bonusAmount: Double,
  val redeemedAt: Long = System.currentTimeMillis()
)
