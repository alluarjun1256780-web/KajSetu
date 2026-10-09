package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "advertisers")
data class AdvertiserEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val companyName: String,
  val contactPerson: String = "",
  val phone: String = "", // WhatsApp
  val email: String = "",
  val telegramUsername: String = "",
  val website: String = "",
  val notes: String = "",
  val status: String = "ACTIVE", // "ACTIVE", "INACTIVE"
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ad_packages")
data class AdPackageEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val name: String, // "বেসিক প্যাক (Basic)", "স্ট্যান্ডার্ড প্যাক (Standard)", "প্রিমিয়াম প্যাক (Premium)"
  val durationDays: Int, // 7, 15, 30
  val price: Double, // 500.0, 900.0, 1500.0
  val position: String = "DASHBOARD",
  val description: String = "",
  val features: String = "ব্যানার বিজ্ঞাপন, ক্লিক ট্র্যাকিং, টার্গেটেড ইউজার",
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ad_campaigns")
data class AdCampaignEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val campaignName: String,
  val advertiserId: String = "",
  val advertiserName: String,
  val advertisementId: String = "",
  val packageId: String = "",
  val packageName: String = "",
  val price: Double,
  val startDate: Long = System.currentTimeMillis(),
  val endDate: Long = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000),
  val position: String = "DASHBOARD",
  val pricingType: String = "FIXED", // "FIXED", "CPM", "CPC"
  val status: String = "ACTIVE", // "ACTIVE", "PAUSED", "COMPLETED", "CANCELLED", "EXPIRED"
  val paymentStatus: String = "PAID", // "UNPAID", "PENDING", "PAID", "REFUNDED", "CANCELLED"
  val paymentMethod: String = "MANUAL", // "BKASH", "NAGAD", "ROCKET", "BANK", "MANUAL"
  val transactionId: String = "",
  val paymentNotes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ad_contact_settings")
data class AdContactSettingsEntity(
  @PrimaryKey val id: String = "default_ad_settings",
  val adsEnabled: Boolean = true,
  val whatsappNumber: String = "+8801712345678",
  val whatsappMessage: String = "হ্যালো, আমি কাজসেতু প্ল্যাটফর্মে বিজ্ঞাপন ও স্পনসরশিপ দিতে আগ্রহী।",
  val email: String = "sponsor@kajsetu.com",
  val emailSubject: String = "বিজ্ঞাপন প্রচার সংক্রান্ত অনুসন্ধান - কাজসেতু",
  val telegramLink: String = "https://t.me/KajSetuOfficial",
  val defaultAdPosition: String = "DASHBOARD",
  val rotationMode: String = "PRIORITY", // "PRIORITY", "RANDOM", "WEIGHTED"
  val impressionTrackingEnabled: Boolean = true,
  val clickTrackingEnabled: Boolean = true,
  val adLabelText: String = "Sponsored", // "Sponsored", "বিজ্ঞাপন"
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ad_events")
data class AdEventEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val adId: String,
  val eventType: String, // "IMPRESSION" or "CLICK"
  val timestamp: Long = System.currentTimeMillis(),
  val isValid: Boolean = true,
  val deviceSession: String = ""
)

@Entity(tableName = "ad_audit_logs")
data class AdAuditLogEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val adminId: String,
  val adminUsername: String,
  val action: String, // "AD_CREATED", "AD_EDITED", "AD_ENABLED", "AD_DISABLED", "CAMPAIGN_CREATED", "PAYMENT_MARKED_PAID", "SETTINGS_CHANGED"
  val target: String,
  val oldValue: String = "",
  val newValue: String = "",
  val timestamp: Long = System.currentTimeMillis()
)
