package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AdvertiserDao {
  @Query("SELECT * FROM advertisers ORDER BY createdAt DESC")
  fun getAllAdvertisers(): Flow<List<AdvertiserEntity>>

  @Query("SELECT * FROM advertisers WHERE status = 'ACTIVE' ORDER BY companyName ASC")
  fun getActiveAdvertisers(): Flow<List<AdvertiserEntity>>

  @Query("SELECT * FROM advertisers WHERE id = :id LIMIT 1")
  suspend fun getAdvertiserById(id: String): AdvertiserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAdvertiser(advertiser: AdvertiserEntity)

  @Update
  suspend fun updateAdvertiser(advertiser: AdvertiserEntity)

  @Query("DELETE FROM advertisers WHERE id = :id")
  suspend fun deleteAdvertiser(id: String)
}

@Dao
interface AdPackageDao {
  @Query("SELECT * FROM ad_packages ORDER BY price ASC")
  fun getAllPackages(): Flow<List<AdPackageEntity>>

  @Query("SELECT * FROM ad_packages WHERE isActive = 1 ORDER BY price ASC")
  fun getActivePackages(): Flow<List<AdPackageEntity>>

  @Query("SELECT * FROM ad_packages WHERE id = :id LIMIT 1")
  suspend fun getPackageById(id: String): AdPackageEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPackage(pkg: AdPackageEntity)

  @Update
  suspend fun updatePackage(pkg: AdPackageEntity)

  @Query("DELETE FROM ad_packages WHERE id = :id")
  suspend fun deletePackage(id: String)

  @Query("UPDATE ad_packages SET isActive = :isActive WHERE id = :id")
  suspend fun setPackageActive(id: String, isActive: Boolean)
}

@Dao
interface AdCampaignDao {
  @Query("SELECT * FROM ad_campaigns ORDER BY createdAt DESC")
  fun getAllCampaigns(): Flow<List<AdCampaignEntity>>

  @Query("SELECT * FROM ad_campaigns WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
  fun getActiveCampaigns(): Flow<List<AdCampaignEntity>>

  @Query("SELECT * FROM ad_campaigns WHERE id = :id LIMIT 1")
  suspend fun getCampaignById(id: String): AdCampaignEntity?

  @Query("SELECT * FROM ad_campaigns WHERE advertiserId = :advertiserId ORDER BY createdAt DESC")
  fun getCampaignsByAdvertiser(advertiserId: String): Flow<List<AdCampaignEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCampaign(campaign: AdCampaignEntity)

  @Update
  suspend fun updateCampaign(campaign: AdCampaignEntity)

  @Query("UPDATE ad_campaigns SET status = :status WHERE id = :id")
  suspend fun updateCampaignStatus(id: String, status: String)

  @Query("UPDATE ad_campaigns SET paymentStatus = :paymentStatus, status = 'ACTIVE' WHERE id = :id")
  suspend fun markPaymentPaid(id: String, paymentStatus: String)

  @Query("DELETE FROM ad_campaigns WHERE id = :id")
  suspend fun deleteCampaign(id: String)
}

@Dao
interface AdContactSettingsDao {
  @Query("SELECT * FROM ad_contact_settings WHERE id = 'default_ad_settings' LIMIT 1")
  fun getAdContactSettingsFlow(): Flow<AdContactSettingsEntity?>

  @Query("SELECT * FROM ad_contact_settings WHERE id = 'default_ad_settings' LIMIT 1")
  suspend fun getAdContactSettings(): AdContactSettingsEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateSettings(settings: AdContactSettingsEntity)
}

@Dao
interface AdEventDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: AdEventEntity)

  @Query("SELECT COUNT(*) FROM ad_events WHERE adId = :adId AND eventType = 'IMPRESSION' AND isValid = 1")
  suspend fun getImpressionCount(adId: String): Int

  @Query("SELECT COUNT(*) FROM ad_events WHERE adId = :adId AND eventType = 'CLICK' AND isValid = 1")
  suspend fun getClickCount(adId: String): Int

  @Query("SELECT COUNT(*) FROM ad_events WHERE adId = :adId AND eventType = 'CLICK' AND timestamp > :sinceTime AND deviceSession = :session")
  suspend fun getRecentClickCountForSession(adId: String, sinceTime: Long, session: String): Int

  @Query("SELECT COUNT(*) FROM ad_events WHERE eventType = 'IMPRESSION' AND timestamp >= :startTime AND timestamp <= :endTime")
  suspend fun getImpressionsBetween(startTime: Long, endTime: Long): Int

  @Query("SELECT COUNT(*) FROM ad_events WHERE eventType = 'CLICK' AND timestamp >= :startTime AND timestamp <= :endTime")
  suspend fun getClicksBetween(startTime: Long, endTime: Long): Int
}

@Dao
interface AdAuditLogDao {
  @Query("SELECT * FROM ad_audit_logs ORDER BY timestamp DESC LIMIT 100")
  fun getAllAdAuditLogs(): Flow<List<AdAuditLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: AdAuditLogEntity)
}
