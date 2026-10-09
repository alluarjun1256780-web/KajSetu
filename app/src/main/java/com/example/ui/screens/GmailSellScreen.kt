package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.GmailSubmissionEntity
import com.example.ui.KajSetuViewModel
import com.example.ui.Screen
import com.example.ui.components.EmptyStateView
import com.example.ui.components.KajSetuTopBar
import com.example.ui.components.formatBdt
import com.example.ui.components.formatDateTime
import com.example.ui.theme.*

@Composable
fun GmailSellScreen(
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit = {},
  onBack: () -> Unit
) {
  val user by viewModel.currentUser.collectAsState()
  val userGmails by viewModel.userGmailSubmissions.collectAsState()
  val context = LocalContext.current

  var gmailAddress by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var recoveryEmail by remember { mutableStateOf("kajsetu.recovery@gmail.com") }
  var notes by remember { mutableStateOf("") }
  var showPassword by remember { mutableStateOf(false) }
  var isSubmitting by remember { mutableStateOf(false) }
  var showPostSubmitLogoutDialog by remember { mutableStateOf(false) }
  var lastSubmittedEmail by remember { mutableStateOf("") }

  val isAccountActive = user?.accountStatus == "active"
  val approvedCount = userGmails.count { it.status == "APPROVED" }
  val totalEarned = userGmails.filter { it.status == "APPROVED" }.sumOf { it.pricePerMail }

  // Post Submission Logout Alert Dialog
  if (showPostSubmitLogoutDialog) {
    AlertDialog(
      onDismissRequest = { showPostSubmitLogoutDialog = false },
      icon = {
        Icon(
          Icons.Default.WarningAmber,
          contentDescription = null,
          tint = AmberGold,
          modifier = Modifier.size(40.dp)
        )
      },
      title = {
        Text(
          text = "⚠️ জিমেইল লগআউট করার জরুরি নোটিশ!",
          fontWeight = FontWeight.Bold,
          color = Color(0xFF92400E),
          style = MaterialTheme.typography.titleMedium
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "আপনার জিমেইল ($lastSubmittedEmail) সফলভাবে সাবমিট হয়েছে।",
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          Text(
            text = "🔴 অত্যন্ত জরুরি: এই জিমেইল অ্যাকাউন্টটি এখনি আপনার ডিভাইস বা ফোন থেকে লগআউট (Remove Account) করে দিন! ডিভাইস থেকে লগআউট না করলে অ্যাডমিন রিভিউ করার সময় এরর দেখাবে এবং আপনার অ্যাকাউন্টে ব্যালেন্স যুক্ত হবে না।",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = RedError,
              fontWeight = FontWeight.Medium,
              lineHeight = 20.sp
            )
          )
          Text(
            text = "লগআউট করার পর অ্যাডমিন অ্যাকাউন্টটি যাচাই করে আপনার ওয়ালেটে ৳১৫.০০ ব্যালেন্স যুক্ত করে দেবেন।",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showPostSubmitLogoutDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("আমি বুঝতে পেরেছি ও লগআউট করবো", fontWeight = FontWeight.Bold)
        }
      }
    )
  }

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "জিমেইল বিক্রয় ও ইনকাম",
        subtitle = "প্রতি ফ্রেশ জিমেইল ৳১৫.০০ তাৎক্ষণিক আয়",
        showBack = true,
        onBackClick = onBack
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(SlateBackground),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Account Active Notice Banner if user is not active
      if (!isAccountActive) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AmberGoldBg),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AmberGold)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = RedError, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "একাউন্ট একটিভ করা আবশ্যক!",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "কোনো ইউজার অ্যাকাউন্ট একটিভ না করে জিমেইল একাউন্ট সেল করতে বা মাইক্রো টাস্কের কাজ করতে পারবেন না। অবিলম্বে আপনার অ্যাকাউন্ট একটিভ করুন।",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F), lineHeight = 19.sp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = { onNavigate(Screen.Activation) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
              ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("একাউন্ট একটিভ করুন", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Video Tutorial Banner for Gmail creation & recovery email
      item {
        ScreenTutorialBanner(
          title = "জিমেইল তৈরি ও সেল দেওয়ার ভিডিও টিউটোরিয়াল",
          subtitle = "ফোন নম্বর ছাড়া জিমেইল খোলা ও রিকভারি ইমেইল যুক্ত করার নিয়ম দেখুন",
          onWatchVideo = {
            viewModel.openExternalUrl(
              context,
              "https://www.youtube.com/results?search_query=how+to+create+unlimited+gmail+without+phone+number"
            )
          },
          onViewAllTutorials = { onNavigate(Screen.Tutorials) }
        )
      }

      // 1. Hero Rate Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEA4335).copy(alpha = 0.12f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Mail,
                    contentDescription = null,
                    tint = Color(0xFFEA4335),
                    modifier = Modifier.size(26.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "বর্তমান ক্রয় রেট",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                  )
                  Text(
                    text = "৳১৫.০০ / প্রতি জিমেইল",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.Bold,
                      color = EmeraldGreenDark
                    )
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(EmeraldGreenBg)
                  .padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text(
                  text = "🔥 দ্রুত পেমেন্ট",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreenDark
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("মোট জমা", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Text("${userGmails.size}টি", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BluePrimary))
              }
              VerticalDivider(modifier = Modifier.height(30.dp), color = CardBorder)
              Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("অনুমোদিত", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Text("${approvedCount}টি", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen))
              }
              VerticalDivider(modifier = Modifier.height(30.dp), color = CardBorder)
              Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("মোট আর্নিং", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Text(formatBdt(totalEarned), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AmberGold))
              }
            }
          }
        }
      }

      // 2. Guidelines Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = BlueLightBg),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(BluePrimary.copy(alpha = 0.2f))
          )
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Info, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "জিমেইল তৈরির গুরুত্বপূর্ণ নিয়মাবলী",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "১. ফোন নম্বর ছাড়া ফ্রেশ নতুন জিমেইল অ্যাকাউন্ট খুলুন।\n" +
                  "২. রিকভারি ইমেইল হিসেবে 'kajsetu.recovery@gmail.com' ব্যবহার করুন।\n" +
                  "৩. পাসওয়ার্ডে বড় হাত, ছোট হাত ও সংখ্যা দিন (যেমন: KajSetu@2026)।\n" +
                  "৪. কোনো অবস্থাতেই ২-স্টেপ ভেরিফিকেশন (2FA) চালু করবেন না।\n" +
                  "৫. অ্যাকাউন্ট জমা দেওয়ার পর অ্যাডমিন ৫-৩০ মিনিটের ভেতর লগইন চেক করে ওয়ালেটে ৳১৫ যোগ করবেন।",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp)
            )
          }
        }
      }

      // 3. Submission Form Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "নতুন তৈরি করা জিমেইল জমা দিন",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Gmail Address Input
            OutlinedTextField(
              value = gmailAddress,
              onValueChange = { gmailAddress = it },
              label = { Text("জিমেইল অ্যাড্রেস *") },
              placeholder = { Text("যেমন: yourname123@gmail.com") },
              leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = BluePrimary) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("gmail_input_address"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Input
            OutlinedTextField(
              value = password,
              onValueChange = { password = it },
              label = { Text("জিমেইলের পাসওয়ার্ড *") },
              placeholder = { Text("লগইন করার পাসওয়ার্ড") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimary) },
              trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                  Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "পাসওয়ার্ড দেখুন"
                  )
                }
              },
              visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("gmail_input_password"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Recovery Email Input
            OutlinedTextField(
              value = recoveryEmail,
              onValueChange = { recoveryEmail = it },
              label = { Text("রিকভারি ইমেইল") },
              placeholder = { Text("kajsetu.recovery@gmail.com") },
              leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldGreen) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("gmail_input_recovery"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Optional Notes
            OutlinedTextField(
              value = notes,
              onValueChange = { notes = it },
              label = { Text("অতিরিক্ত নোট / ব্যাকআপ কোড (ঐচ্ছিক)") },
              leadingIcon = { Icon(Icons.Default.Note, contentDescription = null, tint = TextMuted) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("gmail_input_notes"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (!isAccountActive) {
              Button(
                onClick = { onNavigate(Screen.Activation) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("activate_account_for_gmail_button")
              ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("জিমেইল জমা দিতে একাউন্ট একটিভ করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            } else {
              // Submit Button for Active User
              Button(
                onClick = {
                  if (gmailAddress.isBlank() || password.isBlank()) return@Button
                  val submittedMail = gmailAddress
                  isSubmitting = true
                  viewModel.submitGmailForSale(
                    gmail = gmailAddress,
                    pass = password,
                    recovery = recoveryEmail,
                    notes = notes,
                    onSuccess = {
                      isSubmitting = false
                      lastSubmittedEmail = submittedMail
                      showPostSubmitLogoutDialog = true
                      gmailAddress = ""
                      password = ""
                      notes = ""
                    }
                  )
                },
                enabled = gmailAddress.isNotBlank() && password.isNotBlank() && !isSubmitting,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("submit_gmail_sale_button")
              ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("জিমেইল জমা দিন (৳১৫.০০ রিওয়ার্ড)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
              }
            }
          }
        }
      }

      // 4. Submitted Gmails History
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "আমার জমাকৃত জিমেইল তালিকা (${userGmails.size}টি)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
          )
        }
      }

      if (userGmails.isEmpty()) {
        item {
          EmptyStateView(
            icon = Icons.Default.MarkEmailUnread,
            title = "কোনো জিমেইল জমা দেওয়া হয়নি",
            message = "উপরের ফর্মটি পূরণ করে আপনার তৈরি করা প্রথম ফ্রেশ জিমেইলটি বিক্রি করে আয় করুন।"
          )
        }
      } else {
        items(userGmails) { sub ->
          GmailSubmissionCard(
            sub = sub,
            onCopy = { text ->
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Copied", text))
            }
          )
        }
      }
    }
  }
}

@Composable
private fun GmailSubmissionCard(
  sub: GmailSubmissionEntity,
  onCopy: (String) -> Unit
) {
  val (statusBg, statusTextColor, statusText, statusIcon) = when (sub.status) {
    "APPROVED" -> Quadruple(EmeraldGreenBg, EmeraldGreenDark, "অনুমোদিত (৳১৫ যোগ হয়েছে)", Icons.Default.CheckCircle)
    "REJECTED" -> Quadruple(RedBg, RedError, "বাতিল", Icons.Default.Cancel)
    else -> Quadruple(AmberGoldBg, Color(0xFF92400E), "পেন্ডিং (অ্যাডমিন চেক করছেন)", Icons.Default.HourglassTop)
  }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(statusBg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(statusIcon, contentDescription = null, tint = statusTextColor, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(statusText, color = statusTextColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }

        Text(
          formatDateTime(sub.createdAt),
          style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Gmail Address
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateBackground)
          .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Mail, contentDescription = null, tint = Color(0xFFEA4335), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(sub.gmailAddress, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        }

        IconButton(onClick = { onCopy(sub.gmailAddress) }, modifier = Modifier.size(24.dp)) {
          Icon(Icons.Default.ContentCopy, contentDescription = "কপি করুন", tint = TextMuted, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Password and Recovery row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("পাসওয়ার্ড: ${sub.password}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        if (sub.recoveryEmail.isNotBlank()) {
          Text("রিকভারি: ${sub.recoveryEmail}", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
        }
      }

      if (sub.status == "REJECTED" && sub.rejectionReason.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(RedBg)
            .padding(8.dp)
        ) {
          Text(
            text = "বাতিলের কারণ: ${sub.rejectionReason}",
            style = MaterialTheme.typography.labelSmall.copy(color = RedError, fontWeight = FontWeight.Medium)
          )
        }
      }
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
