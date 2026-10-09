package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KajSetuViewModel
import com.example.ui.theme.*

@Composable
fun AuthScreen(
  viewModel: KajSetuViewModel,
  onBack: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register, 2: Forgot

  // Login states
  var loginIdentifier by remember { mutableStateOf("") }
  var loginPassword by remember { mutableStateOf("") }
  var showLoginPassword by remember { mutableStateOf(false) }

  // Register states
  var regName by remember { mutableStateOf("") }
  var regUsername by remember { mutableStateOf("") }
  var regEmail by remember { mutableStateOf("") }
  var regPhone by remember { mutableStateOf("") }
  var regPassword by remember { mutableStateOf("") }
  var regConfirmPassword by remember { mutableStateOf("") }
  var regReferral by remember { mutableStateOf("") }
  var showRegPassword by remember { mutableStateOf(false) }

  // Forgot password
  var forgotIdentifier by remember { mutableStateOf("") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
  ) {
    // Top Bar
    Surface(color = SurfaceCard, shadowElevation = 1.dp) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("auth_back_button")) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান", tint = TextPrimary)
        }
        Text(
          text = if (selectedTab == 0) "লগইন" else if (selectedTab == 1) "নতুন রেজিস্ট্রেশন" else "পাসওয়ার্ড পুনরুদ্ধার",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // Branding Header
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(BlueLightBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Lock, contentDescription = "Security", tint = BluePrimary, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "কাজসেতু সিকিউর পোর্টাল",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
        )
        Text(
          text = "নিরাপদে আপনার অ্যাকাউন্টে প্রবেশ করুন",
          style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
      }

      // Security Banner
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BlueLightBg),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "কাজসেতুতে এন্ড-টু-এন্ড এনক্রিপশন ও উচ্চতর সুরক্ষায় আপনার তথ্য সম্পূর্ণ নিরাপদ।",
            style = MaterialTheme.typography.bodySmall.copy(color = BluePrimaryDark)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Tab switcher
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = SurfaceCard,
        contentColor = BluePrimary,
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("লগইন", fontWeight = FontWeight.SemiBold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("রেজিস্ট্রেশন", fontWeight = FontWeight.SemiBold) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("রিসেট", fontWeight = FontWeight.SemiBold) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          if (selectedTab == 0) {
            // LOGIN FORM
            OutlinedTextField(
              value = loginIdentifier,
              onValueChange = { loginIdentifier = it },
              label = { Text("ইমেইল / মোবাইল / ইউজারনেম") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_identifier_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
              value = loginPassword,
              onValueChange = { loginPassword = it },
              label = { Text("পাসওয়ার্ড") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimary) },
              trailingIcon = {
                IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                  Icon(
                    if (showLoginPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                  )
                }
              },
              visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.End
            ) {
              TextButton(onClick = { selectedTab = 2 }) {
                Text("পাসওয়ার্ড ভুলে গেছেন?", color = BluePrimary, style = MaterialTheme.typography.bodySmall)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = {
                viewModel.login(loginIdentifier, loginPassword) {}
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_login_button")
            ) {
              Text("লগইন করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
          } else if (selectedTab == 1) {
            // REGISTRATION FORM
            OutlinedTextField(
              value = regName,
              onValueChange = { regName = it },
              label = { Text("পুরো নাম") },
              leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_name_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = regUsername,
              onValueChange = { regUsername = it },
              label = { Text("ইউজারনেম (ইংরেজি)") },
              leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_username_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = regEmail,
              onValueChange = { regEmail = it },
              label = { Text("ইমেইল ঠিকানা") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_email_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = regPhone,
              onValueChange = { regPhone = it },
              label = { Text("মোবাইল নম্বর (যেমন: 017xxxxxxxx)") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_phone_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = regPassword,
              onValueChange = { regPassword = it },
              label = { Text("পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর)") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
              trailingIcon = {
                IconButton(onClick = { showRegPassword = !showRegPassword }) {
                  Icon(if (showRegPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                }
              },
              visualTransformation = if (showRegPassword) VisualTransformation.None else PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_pass_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = regConfirmPassword,
              onValueChange = { regConfirmPassword = it },
              label = { Text("পাসওয়ার্ড নিশ্চিত করুন") },
              leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
              visualTransformation = PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_confirm_pass_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = regReferral,
              onValueChange = { regReferral = it },
              label = { Text("রেফারেল কোড (ঐচ্ছিক)") },
              leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_ref_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                viewModel.register(
                  regName,
                  regUsername,
                  regEmail,
                  regPhone,
                  regPassword,
                  regConfirmPassword,
                  regReferral
                ) {}
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_register_button")
            ) {
              Text("রেজিস্ট্রেশন সম্পন্ন করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
          } else {
            // FORGOT PASSWORD
            Text(
              text = "আপনার অ্যাকাউন্টের নিবন্ধিত ইমেইল বা ফোন নম্বর দিন। একটি ওটিপি ভেরিফিকেশন পাঠানো হবে।",
              style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
              value = forgotIdentifier,
              onValueChange = { forgotIdentifier = it },
              label = { Text("ইমেইল অথবা মোবাইল নম্বর") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = {
                if (forgotIdentifier.isNotBlank()) {
                  viewModel.showMessage("পাসওয়ার্ড রিসেট নির্দেশনা আপনার $forgotIdentifier ঠিকানায় পাঠানো হয়েছে।")
                  selectedTab = 0
                } else {
                  viewModel.showMessage("ইমেইল বা ফোন নম্বর দিন।")
                }
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
            ) {
              Text("রিসেট লিংক পাঠান")
            }
          }
        }
      }
    }
  }
}
