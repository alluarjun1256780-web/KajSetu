package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val fullName: String,
  val username: String,
  val email: String,
  val phone: String,
  val passwordHash: String,
  val role: String = "user", // "user" or "admin"
  val accountStatus: String = "inactive", // "active", "inactive", "suspended"
  val referralCode: String,
  val referredBy: String? = null,
  val profilePic: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallets")
data class WalletEntity(
  @PrimaryKey val userId: String,
  val balance: Double = 0.0,
  val taskEarnings: Double = 0.0,
  val referralEarnings: Double = 0.0,
  val totalDeposit: Double = 0.0,
  val totalWithdrawal: Double = 0.0,
  val pendingWithdrawal: Double = 0.0,
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallet_transactions")
data class TransactionEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val userId: String,
  val type: String, // "DEPOSIT", "TASK_REWARD", "REFERRAL_REWARD", "WITHDRAWAL", "ACTIVATION_FEE", "ADJUSTMENT"
  val amount: Double,
  val balanceBefore: Double,
  val balanceAfter: Double,
  val referenceId: String = "",
  val status: String = "COMPLETED", // "PENDING", "APPROVED", "REJECTED", "COMPLETED"
  val description: String,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val title: String,
  val description: String,
  val instructions: String,
  val rewardAmount: Double,
  val category: String, // "সোশ্যাল মিডিয়া", "রিভিউ ও রেটিং", "অ্যাপ ইনস্টল", "কন্টেন্ট শেয়ার", "জরিপ"
  val taskUrl: String = "https://youtube.com", // কাজের মূল লিংক
  val maxSubmissions: Int = 100,
  val currentSubmissions: Int = 0,
  val proofType: String = "স্ক্রিনশট লিঙ্ক ও ইউজারনেম",
  val status: String = "ACTIVE", // "ACTIVE", "PAUSED", "COMPLETED"
  val deadlineDays: Int = 3,
  val deadline: Long = System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000),
  val isActivationRequired: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_submissions")
data class TaskSubmissionEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val taskId: String,
  val userId: String,
  val userFullName: String,
  val username: String,
  val taskTitle: String,
  val rewardAmount: Double,
  val proofData: String,
  val screenshotUri: String = "", // স্ক্রিনশট ফাইল পাথ বা ইউআরআই
  val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
  val rejectionReason: String = "",
  val submittedAt: Long = System.currentTimeMillis(),
  val reviewedAt: Long? = null
)

@Entity(tableName = "deposits")
data class DepositEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val userId: String,
  val username: String,
  val userPhone: String,
  val method: String, // "BKASH", "NAGAD", "ROCKET", "BANK"
  val senderNumber: String,
  val transactionId: String,
  val amount: Double,
  val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
  val adminNote: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val reviewedAt: Long? = null
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val userId: String,
  val username: String,
  val method: String, // "BKASH", "NAGAD", "ROCKET", "BANK"
  val receiverNumber: String,
  val amount: Double,
  val fee: Double = 0.0,
  val netAmount: Double,
  val status: String = "PENDING", // "PENDING", "PROCESSING", "COMPLETED", "REJECTED"
  val note: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val reviewedAt: Long? = null
)

@Entity(tableName = "activation_requests")
data class ActivationRequestEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val userId: String,
  val username: String,
  val method: String,
  val senderNumber: String,
  val transactionId: String,
  val fee: Double,
  val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
  val createdAt: Long = System.currentTimeMillis(),
  val reviewedAt: Long? = null
)

@Entity(tableName = "gmail_submissions")
data class GmailSubmissionEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val userId: String,
  val username: String,
  val gmailAddress: String,
  val password: String,
  val recoveryEmail: String = "",
  val pricePerMail: Double = 15.0,
  val notes: String = "",
  val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
  val rejectionReason: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val reviewedAt: Long? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val userId: String, // specific userId or "ALL"
  val title: String,
  val message: String,
  val type: String = "INFO", // "INFO", "SUCCESS", "WARNING", "PAYMENT"
  val isRead: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val userId: String,
  val username: String,
  val subject: String,
  val category: String, // "পেমেন্ট", "টাস্ক", "অ্যাকাউন্ট", "অন্যান্য"
  val message: String,
  val adminReply: String = "",
  val status: String = "OPEN", // "OPEN", "RESOLVED", "CLOSED"
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_audit_logs")
data class AuditLogEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val adminId: String,
  val adminUsername: String,
  val action: String,
  val targetType: String,
  val targetId: String,
  val details: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "site_settings")
data class SiteSettingsEntity(
  @PrimaryKey val id: String = "default_settings",
  val siteName: String = "কাজসেতু (KajSetu)",
  val tagline: String = "কাজ করুন • আয় করুন • নিরাপদে এগিয়ে যান",
  val activationFee: Double = 100.0,
  val minDeposit: Double = 100.0,
  val maxDeposit: Double = 25000.0,
  val minWithdrawal: Double = 200.0,
  val maxWithdrawal: Double = 10000.0,
  val withdrawalFeePercent: Double = 2.0,
  val referralReward: Double = 25.0,
  val bkashNumber: String = "01712-345678",
  val nagadNumber: String = "01912-345678",
  val rocketNumber: String = "01812-345678",
  val bankDetails: String = "ইসলামী ব্যাংক, একাউন্ট নং: ২০৫০১২৩৪৫৬৭৮৯০",
  val maintenanceMode: Boolean = false,
  val noticeBanner: String = "কাজসেতুতে আপনাকে স্বাগতম! নিয়মিত মাইক্রো টাস্ক সম্পন্ন করে প্রতিদিন বিকাশ/নগদে আয় নিন।",
  val adContactWhatsapp: String = "8801712345678",
  val adContactEmail: String = "sponsor@kajsetu.com",
  val telegramChannelUrl: String = "https://t.me/KajSetuOfficial"
)

@Entity(tableName = "marketplace_services")
data class MarketplaceServiceEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val title: String,
  val category: String,
  val price: Double,
  val turnaroundTime: String,
  val description: String,
  val providerName: String,
  val rating: Double = 4.9,
  val isActive: Boolean = true
)

@Entity(tableName = "advertisements")
data class AdvertisementEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val title: String,
  val subtitle: String = "",
  val description: String = "",
  val advertiserId: String = "",
  val advertiserName: String = "নিজস্ব প্রমোশন",
  val targetUrl: String, // e.g. "https://t.me/KajSetuBot" or "https://website.com"
  val bannerImageUrl: String = "",
  val position: String = "DASHBOARD", // "HEADER", "HOMEPAGE", "SIDEBAR", "BETWEEN_PRODUCTS", "BETWEEN_TASKS", "DASHBOARD", "MARKETPLACE", "FOOTER", "MOBILE_BANNER", "DESKTOP_BANNER"
  val campaignType: String = "BANNER", // "BANNER", "TELEGRAM_BOT", "SPONSORED_POST", "EXTERNAL_LINK"
  val pricingType: String = "FIXED", // "FIXED", "CPM", "CPC"
  val price: Double = 0.0,
  val priority: Int = 1,
  val buttonText: String = "ভিজিট করুন",
  val clickCount: Int = 0,
  val impressionCount: Int = 0,
  val startDate: Long = System.currentTimeMillis(),
  val endDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
  val status: String = "ACTIVE", // "ACTIVE", "PAUSED", "EXPIRED", "ARCHIVED"
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)
