package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.entity.TaskEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreTaskService(private val context: Context) {
  private val TAG = "FirestoreTaskService"

  private val firestore: FirebaseFirestore by lazy {
    val dbId = context.getString(R.string.firestore_database_id)
    FirebaseFirestore.getInstance(FirebaseApp.getInstance(), dbId)
  }

  private val tasksCollection by lazy {
    firestore.collection("tasks")
  }

  /**
   * Observe micro-tasks stored in Firestore in real time.
   */
  fun observeFirestoreTasks(): Flow<List<TaskEntity>> = callbackFlow {
    var registration: ListenerRegistration? = null
    try {
      registration = tasksCollection
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w(TAG, "Listen failed for tasks", error)
            return@addSnapshotListener
          }

          if (snapshot != null) {
            val list = mutableListOf<TaskEntity>()
            for (doc in snapshot.documents) {
              try {
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: ""
                val description = doc.getString("description") ?: ""
                val instructions = doc.getString("instructions") ?: ""
                val reward = doc.getDouble("rewardAmount") ?: 0.0
                val category = doc.getString("category") ?: "অন্যান্য"
                val taskUrl = doc.getString("taskUrl") ?: "https://youtube.com"
                val maxSubs = doc.getLong("maxSubmissions")?.toInt() ?: 100
                val currentSubs = doc.getLong("currentSubmissions")?.toInt() ?: 0
                val proofType = doc.getString("proofType") ?: "স্ক্রিনশট"
                val status = doc.getString("status") ?: "ACTIVE"
                val deadline = doc.getLong("deadline") ?: (System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000)
                val createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()

                list.add(
                  TaskEntity(
                    id = id,
                    title = title,
                    description = description,
                    instructions = instructions,
                    rewardAmount = reward,
                    category = category,
                    taskUrl = taskUrl,
                    maxSubmissions = maxSubs,
                    currentSubmissions = currentSubs,
                    proofType = proofType,
                    status = status,
                    deadline = deadline,
                    createdAt = createdAt
                  )
                )
              } catch (e: Exception) {
                Log.e(TAG, "Error mapping doc ${doc.id}", e)
              }
            }
            trySend(list)
          }
        }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to attach snapshot listener", e)
    }

    awaitClose {
      registration?.remove()
    }
  }

  /**
   * Create a new micro-task in Firestore
   */
  suspend fun createTask(task: TaskEntity, createdBy: String): Result<Unit> = runCatching {
    val data = hashMapOf(
      "id" to task.id,
      "title" to task.title,
      "description" to task.description,
      "instructions" to task.instructions,
      "rewardAmount" to task.rewardAmount,
      "category" to task.category,
      "taskUrl" to task.taskUrl,
      "maxSubmissions" to task.maxSubmissions,
      "currentSubmissions" to task.currentSubmissions,
      "proofType" to task.proofType,
      "status" to task.status,
      "deadline" to task.deadline.toString(),
      "createdBy" to createdBy,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    tasksCollection.document(task.id).set(data).await()
    Log.d(TAG, "Created task ${task.id} in Firestore successfully")
  }

  /**
   * Update an existing micro-task in Firestore
   */
  suspend fun updateTask(task: TaskEntity, updatedBy: String): Result<Unit> = runCatching {
    val data = hashMapOf<String, Any>(
      "title" to task.title,
      "description" to task.description,
      "instructions" to task.instructions,
      "rewardAmount" to task.rewardAmount,
      "category" to task.category,
      "taskUrl" to task.taskUrl,
      "maxSubmissions" to task.maxSubmissions,
      "proofType" to task.proofType,
      "status" to task.status,
      "deadline" to task.deadline.toString(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    tasksCollection.document(task.id).set(data, SetOptions.merge()).await()
    Log.d(TAG, "Updated task ${task.id} in Firestore successfully")
  }

  /**
   * Delete a micro-task from Firestore
   */
  suspend fun deleteTask(taskId: String): Result<Unit> = runCatching {
    tasksCollection.document(taskId).delete().await()
    Log.d(TAG, "Deleted task $taskId from Firestore successfully")
  }

  /**
   * Toggle task status (ACTIVE, PAUSED, COMPLETED)
   */
  suspend fun toggleTaskStatus(taskId: String, newStatus: String): Result<Unit> = runCatching {
    val data = hashMapOf<String, Any>(
      "status" to newStatus,
      "updatedAt" to FieldValue.serverTimestamp()
    )
    tasksCollection.document(taskId).set(data, SetOptions.merge()).await()
    Log.d(TAG, "Toggled task $taskId to $newStatus in Firestore")
  }
}
