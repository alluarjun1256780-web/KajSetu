package com.example.data.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing a micro-task within the KajSetu platform.
 * Contains core fields: task title, description, reward amount, deadline,
 * as well as platform-specific metadata (category, instructions, limits, status).
 */
@Entity(tableName = "micro_tasks")
data class MicroTaskEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val title: String,
  val description: String,
  val rewardAmount: Double,
  val deadline: Long = System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000), // timestamp in milliseconds
  val category: String = "সোশ্যাল মিডিয়া",
  val instructions: String = "",
  val taskUrl: String = "https://kajsetu.com",
  val maxSubmissions: Int = 100,
  val currentSubmissions: Int = 0,
  val proofType: String = "স্ক্রিনশট ও প্রমাণ",
  val status: String = "ACTIVE", // "ACTIVE", "PAUSED", "COMPLETED", "EXPIRED"
  val isCompleted: Boolean = false,
  val isActivationRequired: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
) {
  @Ignore
  val taskTitle: String = title

  @Ignore
  val reward: Double = rewardAmount
}

typealias MicroTask = MicroTaskEntity
