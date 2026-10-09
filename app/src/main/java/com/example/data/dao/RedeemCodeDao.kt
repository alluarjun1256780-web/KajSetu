package com.example.data.dao

import androidx.room.*
import com.example.data.entity.RedeemCodeEntity
import com.example.data.entity.RedeemCodeUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RedeemCodeDao {
  @Query("SELECT * FROM redeem_codes ORDER BY createdAt DESC")
  fun getAllRedeemCodes(): Flow<List<RedeemCodeEntity>>

  @Query("SELECT * FROM redeem_codes WHERE code = :code COLLATE NOCASE LIMIT 1")
  suspend fun findByCode(code: String): RedeemCodeEntity?

  @Query("SELECT * FROM redeem_codes WHERE id = :id LIMIT 1")
  suspend fun findById(id: String): RedeemCodeEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCode(code: RedeemCodeEntity)

  @Update
  suspend fun updateCode(code: RedeemCodeEntity)

  @Query("UPDATE redeem_codes SET isActive = :isActive WHERE id = :id")
  suspend fun setCodeActive(id: String, isActive: Boolean)

  @Query("UPDATE redeem_codes SET usedCount = usedCount + 1 WHERE id = :id")
  suspend fun incrementUsedCount(id: String)

  @Query("DELETE FROM redeem_codes WHERE id = :id")
  suspend fun deleteCode(id: String)

  // Usage queries
  @Query("SELECT * FROM redeem_code_usages WHERE codeId = :codeId AND userId = :userId LIMIT 1")
  suspend fun findUsage(codeId: String, userId: String): RedeemCodeUsageEntity?

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertUsage(usage: RedeemCodeUsageEntity)

  @Query("SELECT * FROM redeem_code_usages WHERE userId = :userId ORDER BY redeemedAt DESC")
  fun getUserUsages(userId: String): Flow<List<RedeemCodeUsageEntity>>
}
