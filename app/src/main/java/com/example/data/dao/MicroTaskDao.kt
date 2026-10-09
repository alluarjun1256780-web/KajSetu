package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.MicroTaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for micro-tasks in the KajSetu platform.
 * Provides querying, inserting, updating, and deleting micro-tasks.
 */
@Dao
interface MicroTaskDao {
  @Query("SELECT * FROM micro_tasks ORDER BY createdAt DESC")
  fun getAllMicroTasks(): Flow<List<MicroTaskEntity>>

  @Query("SELECT * FROM micro_tasks WHERE status = 'ACTIVE' ORDER BY deadline ASC")
  fun getActiveMicroTasks(): Flow<List<MicroTaskEntity>>

  @Query("SELECT * FROM micro_tasks WHERE id = :id LIMIT 1")
  suspend fun getMicroTaskById(id: String): MicroTaskEntity?

  @Query("SELECT * FROM micro_tasks WHERE status = 'ACTIVE' AND deadline > :currentTime ORDER BY deadline ASC")
  fun getAvailableTasksBeforeDeadline(currentTime: Long = System.currentTimeMillis()): Flow<List<MicroTaskEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMicroTask(task: MicroTaskEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tasks: List<MicroTaskEntity>)

  @Update
  suspend fun updateMicroTask(task: MicroTaskEntity)

  @Delete
  suspend fun deleteMicroTask(task: MicroTaskEntity)

  @Query("DELETE FROM micro_tasks WHERE id = :id")
  suspend fun deleteMicroTaskById(id: String)

  @Query("UPDATE micro_tasks SET currentSubmissions = currentSubmissions + 1 WHERE id = :id")
  suspend fun incrementSubmissions(id: String)

  @Query("UPDATE micro_tasks SET status = :status WHERE id = :id")
  suspend fun updateTaskStatus(id: String, status: String)

  @Query("SELECT COUNT(*) FROM micro_tasks WHERE status = 'ACTIVE'")
  fun getActiveMicroTaskCount(): Flow<Int>
}
