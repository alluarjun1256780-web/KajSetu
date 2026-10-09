package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Query("SELECT * FROM users WHERE email = :identifier OR phone = :identifier OR username = :identifier LIMIT 1")
  suspend fun findByIdentifier(identifier: String): UserEntity?

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  suspend fun findById(id: String): UserEntity?

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  fun observeUserById(id: String): Flow<UserEntity?>

  @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
  suspend fun findByReferralCode(code: String): UserEntity?

  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<UserEntity>>

  @Query("SELECT COUNT(*) FROM users")
  fun getUserCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM users WHERE accountStatus = 'active'")
  fun getActiveUserCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM users WHERE referredBy = :code")
  fun getReferralCount(code: String): Flow<Int>

  @Query("SELECT * FROM users WHERE referredBy = :code ORDER BY createdAt DESC")
  fun getReferrals(code: String): Flow<List<UserEntity>>

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertUser(user: UserEntity)

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("UPDATE users SET accountStatus = :status WHERE id = :id")
  suspend fun updateAccountStatus(id: String, status: String)

  @Query("UPDATE users SET role = :role WHERE id = :id")
  suspend fun updateUserRole(id: String, role: String)
}

@Dao
interface WalletDao {
  @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
  suspend fun getWallet(userId: String): WalletEntity?

  @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
  fun observeWallet(userId: String): Flow<WalletEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWallet(wallet: WalletEntity)

  @Update
  suspend fun updateWallet(wallet: WalletEntity)
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY createdAt DESC")
  fun getTransactionsByUser(userId: String): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM wallet_transactions ORDER BY createdAt DESC LIMIT 100")
  fun getAllTransactions(): Flow<List<TransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity)
}

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
  fun getActiveTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
  suspend fun getTaskById(id: String): TaskEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity)

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTask(id: String)

  @Query("UPDATE tasks SET currentSubmissions = currentSubmissions + 1 WHERE id = :id")
  suspend fun incrementSubmissions(id: String)
}

@Dao
interface TaskSubmissionDao {
  @Query("SELECT * FROM task_submissions WHERE userId = :userId ORDER BY submittedAt DESC")
  fun getSubmissionsByUser(userId: String): Flow<List<TaskSubmissionEntity>>

  @Query("SELECT * FROM task_submissions WHERE status = 'PENDING' ORDER BY submittedAt ASC")
  fun getPendingSubmissions(): Flow<List<TaskSubmissionEntity>>

  @Query("SELECT * FROM task_submissions ORDER BY submittedAt DESC LIMIT 100")
  fun getAllSubmissions(): Flow<List<TaskSubmissionEntity>>

  @Query("SELECT * FROM task_submissions WHERE id = :id LIMIT 1")
  suspend fun getSubmissionById(id: String): TaskSubmissionEntity?

  @Query("SELECT COUNT(*) FROM task_submissions WHERE taskId = :taskId AND userId = :userId")
  suspend fun countSubmissionsForUser(taskId: String, userId: String): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSubmission(submission: TaskSubmissionEntity)

  @Update
  suspend fun updateSubmission(submission: TaskSubmissionEntity)
}

@Dao
interface DepositDao {
  @Query("SELECT * FROM deposits WHERE userId = :userId ORDER BY createdAt DESC")
  fun getDepositsByUser(userId: String): Flow<List<DepositEntity>>

  @Query("SELECT * FROM deposits WHERE status = 'PENDING' ORDER BY createdAt ASC")
  fun getPendingDeposits(): Flow<List<DepositEntity>>

  @Query("SELECT * FROM deposits ORDER BY createdAt DESC LIMIT 100")
  fun getAllDeposits(): Flow<List<DepositEntity>>

  @Query("SELECT * FROM deposits WHERE id = :id LIMIT 1")
  suspend fun getDepositById(id: String): DepositEntity?

  @Query("SELECT * FROM deposits WHERE transactionId = :trxId LIMIT 1")
  suspend fun findByTrxId(trxId: String): DepositEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDeposit(deposit: DepositEntity)

  @Update
  suspend fun updateDeposit(deposit: DepositEntity)
}

@Dao
interface WithdrawalDao {
  @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY createdAt DESC")
  fun getWithdrawalsByUser(userId: String): Flow<List<WithdrawalEntity>>

  @Query("SELECT * FROM withdrawals WHERE status = 'PENDING' ORDER BY createdAt ASC")
  fun getPendingWithdrawals(): Flow<List<WithdrawalEntity>>

  @Query("SELECT * FROM withdrawals ORDER BY createdAt DESC LIMIT 100")
  fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

  @Query("SELECT * FROM withdrawals WHERE id = :id LIMIT 1")
  suspend fun getWithdrawalById(id: String): WithdrawalEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWithdrawal(withdrawal: WithdrawalEntity)

  @Update
  suspend fun updateWithdrawal(withdrawal: WithdrawalEntity)
}

@Dao
interface ActivationRequestDao {
  @Query("SELECT * FROM activation_requests WHERE userId = :userId ORDER BY createdAt DESC")
  fun getRequestsByUser(userId: String): Flow<List<ActivationRequestEntity>>

  @Query("SELECT * FROM activation_requests WHERE status = 'PENDING' ORDER BY createdAt ASC")
  fun getPendingRequests(): Flow<List<ActivationRequestEntity>>

  @Query("SELECT * FROM activation_requests WHERE id = :id LIMIT 1")
  suspend fun getRequestById(id: String): ActivationRequestEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRequest(request: ActivationRequestEntity)

  @Update
  suspend fun updateRequest(request: ActivationRequestEntity)
}

@Dao
interface GmailSubmissionDao {
  @Query("SELECT * FROM gmail_submissions WHERE userId = :userId ORDER BY createdAt DESC")
  fun getSubmissionsByUser(userId: String): Flow<List<GmailSubmissionEntity>>

  @Query("SELECT * FROM gmail_submissions ORDER BY createdAt DESC")
  fun getAllSubmissions(): Flow<List<GmailSubmissionEntity>>

  @Query("SELECT * FROM gmail_submissions WHERE status = 'PENDING' ORDER BY createdAt ASC")
  fun getPendingSubmissions(): Flow<List<GmailSubmissionEntity>>

  @Query("SELECT * FROM gmail_submissions WHERE id = :id LIMIT 1")
  suspend fun getSubmissionById(id: String): GmailSubmissionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSubmission(submission: GmailSubmissionEntity)

  @Update
  suspend fun updateSubmission(submission: GmailSubmissionEntity)

  @Query("SELECT COUNT(*) FROM gmail_submissions WHERE userId = :userId AND status = 'APPROVED'")
  suspend fun countApprovedByUser(userId: String): Int
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 'ALL' ORDER BY createdAt DESC")
  fun getNotifications(userId: String): Flow<List<NotificationEntity>>

  @Query("SELECT COUNT(*) FROM notifications WHERE (userId = :userId OR userId = 'ALL') AND isRead = 0")
  fun getUnreadCount(userId: String): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity)

  @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId OR userId = 'ALL'")
  suspend fun markAllAsRead(userId: String)

  @Query("DELETE FROM notifications WHERE id = :id")
  suspend fun deleteNotification(id: String)
}

@Dao
interface SupportTicketDao {
  @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY createdAt DESC")
  fun getTicketsByUser(userId: String): Flow<List<SupportTicketEntity>>

  @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
  fun getAllTickets(): Flow<List<SupportTicketEntity>>

  @Query("SELECT * FROM support_tickets WHERE id = :id LIMIT 1")
  suspend fun getTicketById(id: String): SupportTicketEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTicket(ticket: SupportTicketEntity)

  @Update
  suspend fun updateTicket(ticket: SupportTicketEntity)
}

@Dao
interface AuditLogDao {
  @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC LIMIT 200")
  fun getAllLogs(): Flow<List<AuditLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: AuditLogEntity)
}

@Dao
interface SiteSettingsDao {
  @Query("SELECT * FROM site_settings WHERE id = 'default_settings' LIMIT 1")
  fun getSettingsFlow(): Flow<SiteSettingsEntity?>

  @Query("SELECT * FROM site_settings WHERE id = 'default_settings' LIMIT 1")
  suspend fun getSettings(): SiteSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateSettings(settings: SiteSettingsEntity)
}

@Dao
interface MarketplaceServiceDao {
  @Query("SELECT * FROM marketplace_services WHERE isActive = 1")
  fun getActiveServices(): Flow<List<MarketplaceServiceEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertService(service: MarketplaceServiceEntity)
}

@Dao
interface AdvertisementDao {
  @Query("SELECT * FROM advertisements ORDER BY priority DESC, createdAt DESC")
  fun getAllAds(): Flow<List<AdvertisementEntity>>

  @Query("SELECT * FROM advertisements WHERE isActive = 1 AND (endDate >= :currentTime OR endDate = 0) ORDER BY priority DESC, createdAt DESC")
  fun getActiveAds(currentTime: Long = System.currentTimeMillis()): Flow<List<AdvertisementEntity>>

  @Query("SELECT * FROM advertisements WHERE isActive = 1 AND position = :pos AND (endDate >= :currentTime OR endDate = 0) ORDER BY priority DESC, createdAt DESC")
  fun getActiveAdsByPosition(pos: String, currentTime: Long = System.currentTimeMillis()): Flow<List<AdvertisementEntity>>

  @Query("SELECT * FROM advertisements WHERE id = :id LIMIT 1")
  suspend fun getAdById(id: String): AdvertisementEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAd(ad: AdvertisementEntity)

  @Update
  suspend fun updateAd(ad: AdvertisementEntity)

  @Query("DELETE FROM advertisements WHERE id = :id")
  suspend fun deleteAd(id: String)

  @Query("UPDATE advertisements SET isActive = :isActive, status = :status WHERE id = :id")
  suspend fun setAdActive(id: String, isActive: Boolean, status: String = if (isActive) "ACTIVE" else "PAUSED")

  @Query("UPDATE advertisements SET clickCount = clickCount + 1 WHERE id = :id")
  suspend fun incrementClickCount(id: String)

  @Query("UPDATE advertisements SET impressionCount = impressionCount + 1 WHERE id = :id")
  suspend fun incrementImpressionCount(id: String)

  @Query("UPDATE advertisements SET status = 'EXPIRED', isActive = 0 WHERE endDate < :now AND status = 'ACTIVE'")
  suspend fun expireOldAds(now: Long = System.currentTimeMillis())
}
