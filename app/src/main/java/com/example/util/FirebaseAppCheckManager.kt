package com.example.util

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

object FirebaseAppCheckManager {
  private const val TAG = "FirebaseAppCheck"
  private var isInitialized = false

  fun init(context: Context) {
    if (isInitialized) return
    try {
      if (FirebaseApp.getApps(context).isEmpty()) {
        FirebaseApp.initializeApp(context)
      }

      val firebaseAppCheck = FirebaseAppCheck.getInstance()

      if (BuildConfig.DEBUG) {
        // Use Debug provider in debug builds to allow local testing and automated verification
        Log.d(TAG, "Initializing Firebase App Check with DebugAppCheckProviderFactory")
        firebaseAppCheck.installAppCheckProviderFactory(
          DebugAppCheckProviderFactory.getInstance()
        )
      } else {
        // Use Play Integrity provider in release builds to attest genuine app authenticity
        Log.d(TAG, "Initializing Firebase App Check with PlayIntegrityAppCheckProviderFactory")
        firebaseAppCheck.installAppCheckProviderFactory(
          PlayIntegrityAppCheckProviderFactory.getInstance()
        )
      }

      isInitialized = true
      Log.i(TAG, "Firebase App Check successfully initialized")
    } catch (e: Exception) {
      Log.e(TAG, "Failed to initialize Firebase App Check: ${e.message}", e)
    }
  }
}
