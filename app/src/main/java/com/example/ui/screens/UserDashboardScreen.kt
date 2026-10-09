package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.*
import com.example.ui.KajSetuViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun UserDashboardScreen(
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit
) {
  val user by viewModel.currentUser.collectAsState()
  val wallet by viewModel.currentWallet.collectAsState()
  val settings by viewModel.siteSettings.collectAsState()
  val transactions by viewModel.userTransactions.collectAsState()
  val submissions by viewModel.userSubmissions.collectAsState()
  val activeTasks by viewModel.activeTasks.collectAsState()
  val unreadCount by viewModel.unreadNotificationCount.collectAsState()
  val activeAds by viewModel.activeAds.collectAsState()
  val adSettings by viewModel.adContactSettings.collectAsState()
  val context = androidx.compose.ui.platform.LocalContext.current

  var selectedTaskForPopup by remember { mutableStateOf<TaskEntity?>(null) }
  val submittedTaskIds = remember(submissions) { submissions.map { it.taskId }.toSet() }

  val dashboardAds = activeAds.filter { it.position == "HEADER" || it.position == "DASHBOARD" }

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "কাজসেতু ড্যাশবোর্ড",
        subtitle = "স্বাগতম, ${user?.fullName ?: "ব্যবহারকারী"}",
        unreadCount = unreadCount,
        onNotificationClick = { onNavigate(Screen.Notifications) },
        onProfileClick = { onNavigate(Screen.Profile) },
        actions = {
          if (user?.role == "admin") {
            Button(
              onClick = { onNavigate(Screen.AdminPortal) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier.padding(end = 4.dp).testTag("admin_portal_button")
            ) {
              Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
              Spacer(modifier = Modifier.width(4.dp))
              Text("অ্যাডমিন প্যানেল", fontSize = 11.sp, color = Color.White)
            }
          }
        }
      )
    },
    bottomBar = {
      UserBottomNav(
        currentScreen = Screen.Dashboard,
        onNavigate = onNavigate
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
      // 0. Admin Quick Action Banner (Strictly visible ONLY to admin role)
      if (user?.role == "admin") {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier.fillMaxWidth().testTag("admin_dashboard_card")
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = Color(0xFF1E293B),
                  modifier = Modifier.size(40.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AmberGold, modifier = Modifier.size(22.dp))
                  }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text("অ্যাডমিন কন্ট্রোল সেন্টার", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text("মাইক্রো-টাস্ক ও সিস্টেম পরিচালনা", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
              }
              Button(
                onClick = { onNavigate(Screen.AdminPortal) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text("প্যানেল খুলুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 1. Notice Banner (from Admin Settings)
      if (!settings?.noticeBanner.isNullOrBlank()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BlueLightBg),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Campaign, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = settings?.noticeBanner ?: "",
                style = MaterialTheme.typography.bodySmall.copy(color = BluePrimaryDark, fontWeight = FontWeight.Medium)
              )
            }
          }
        }
      }

      // Dynamic Sponsored Ads Banner (Top / Header Position)
      val topAd = dashboardAds.firstOrNull { it.position == "HEADER" } ?: dashboardAds.firstOrNull()
      if (topAd != null) {
        item {
          SponsoredAdBanner(
            ad = topAd,
            onAdClick = { viewModel.openAdTarget(context, topAd) },
            onImpression = { viewModel.recordAdImpression(topAd.id) },
            labelText = adSettings?.adLabelText ?: "Sponsored"
          )
        }
      }

      // 2. Status Card (Active vs Inactive)
      item {
        val isActive = user?.accountStatus == "active"
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = if (isActive) EmeraldGreenBg else AmberGoldBg),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(Screen.Activation) }
        ) {
          Row(
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(if (isActive) EmeraldGreen else AmberGold),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isActive) Icons.Default.Verified else Icons.Default.Warning,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = if (isActive) "অ্যাকাউন্ট ভেরিফাইড ও সক্রিয়" else "আপনার অ্যাকাউন্ট এখনো সক্রিয় নয়",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) EmeraldGreenDark else Color(0xFF92400E)
                  )
                )
                Text(
                  text = if (isActive) "অ্যাক্টিভেশন বিস্তারিত ও সুবিধা দেখতে ট্যাপ করুন" else "টাস্ক সম্পন্ন ও টাকা তুলতে অ্যাকাউন্ট সক্রিয় করুন",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isActive) EmeraldGreenDark.copy(alpha = 0.8f) else Color(0xFFB45309)
                  )
                )
              }
            }

            Button(
              onClick = { onNavigate(Screen.Activation) },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isActive) EmeraldGreen else BluePrimary
              ),
              modifier = Modifier.testTag("dashboard_activate_button")
            ) {
              Text(
                text = if (isActive) "স্ট্যাটাস দেখুন" else "সক্রিয় করুন",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // 2.5 Gmail Sell Hot Promotion Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(Screen.GmailSell) }
        ) {
          Row(
            modifier = Modifier
              .padding(14.dp)
              .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFFEA4335).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Mail,
                  contentDescription = null,
                  tint = Color(0xFFEA4335),
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "জিমেইল বিক্রয় ও ইনকাম",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = TextPrimary
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Color(0xFFEA4335))
                      .padding(horizontal = 5.dp, vertical = 2.dp)
                  ) {
                    Text("HOT 🔥", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "প্রতি ফ্রেশ জিমেইল ৳১৫.০০ তাৎক্ষণিক ওয়ালেট আয়!",
                  style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
              }
            }

            Button(
              onClick = { onNavigate(Screen.GmailSell) },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text("সেল করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 3. Balance Hero Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "বর্তমান ওয়ালেট ব্যালেন্স",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
              )
              Text(
                text = "ইউজার আইডি: ${user?.username ?: ""}",
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = formatBdt(wallet?.balance ?: 0.0),
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = BluePrimaryDark
              )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = { onNavigate(Screen.Deposit) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("dashboard_deposit_button")
              ) {
                Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ডিপোজিট", fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = { onNavigate(Screen.Withdraw) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldGreenDark),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldGreen)),
                modifier = Modifier
                  .weight(1f)
                  .height(44.dp)
                  .testTag("dashboard_withdraw_button")
              ) {
                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp), tint = EmeraldGreenDark)
                Spacer(modifier = Modifier.width(6.dp))
                Text("উইথড্র", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 4. Financial Statistics Grid
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
              title = "টাস্ক আয়",
              value = formatBdt(wallet?.taskEarnings ?: 0.0),
              icon = Icons.Default.TaskAlt,
              iconColor = EmeraldGreen,
              modifier = Modifier.weight(1f)
            )
            StatCard(
              title = "রেফারেল আয়",
              value = formatBdt(wallet?.referralEarnings ?: 0.0),
              icon = Icons.Default.Share,
              iconColor = AmberGold,
              modifier = Modifier.weight(1f)
            )
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
              title = "মোট উত্তোলন",
              value = formatBdt(wallet?.totalWithdrawal ?: 0.0),
              icon = Icons.Default.AccountBalance,
              iconColor = Color(0xFF6366F1),
              modifier = Modifier.weight(1f)
            )
            StatCard(
              title = "পেন্ডিং উত্তোলন",
              value = formatBdt(wallet?.pendingWithdrawal ?: 0.0),
              icon = Icons.Default.Pending,
              iconColor = RedError,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 5. Quick Actions
      item {
        Column {
          Text(
            text = "কুইক অ্যাকশন",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            QuickActionIcon(
              icon = Icons.Default.VerifiedUser,
              label = "অ্যাকাউন্ট একটিভ",
              color = if (user?.accountStatus == "active") EmeraldGreen else RedError
            ) { onNavigate(Screen.Activation) }
            QuickActionIcon(
              icon = Icons.Default.Mail,
              label = "জিমেইল সেল",
              color = Color(0xFFEA4335)
            ) { onNavigate(Screen.GmailSell) }
            QuickActionIcon(
              icon = Icons.Default.ListAlt,
              label = "মাইক্রো টাস্ক",
              color = BluePrimary
            ) { onNavigate(Screen.Tasks) }
            QuickActionIcon(
              icon = Icons.Default.CheckCircleOutline,
              label = "আমার কাজ",
              color = EmeraldGreen
            ) { onNavigate(Screen.MyTasks) }
          }
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            QuickActionIcon(
              icon = Icons.Default.CardGiftcard,
              label = "রিডিম বোনাস",
              color = Color(0xFFD97706)
            ) { onNavigate(Screen.RedeemBonus) }
            QuickActionIcon(
              icon = Icons.Default.AccountBalanceWallet,
              label = "ডিপোজিট",
              color = Color(0xFF6366F1)
            ) { onNavigate(Screen.Deposit) }
            QuickActionIcon(
              icon = Icons.Default.Payments,
              label = "টাকা তোলা",
              color = AmberGold
            ) { onNavigate(Screen.Withdraw) }
            QuickActionIcon(
              icon = Icons.Default.GroupAdd,
              label = "রেফারেল",
              color = Color(0xFF10B981)
            ) { onNavigate(Screen.Referral) }
          }
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
          ) {
            QuickActionIcon(
              icon = Icons.Default.PlayCircle,
              label = "টিউটোরিয়াল",
              color = Color(0xFFEF4444)
            ) { onNavigate(Screen.Tutorials) }
            Spacer(modifier = Modifier.width(16.dp))
            QuickActionIcon(
              icon = Icons.Default.HeadsetMic,
              label = "সাপোর্ট",
              color = BluePrimaryDark
            ) { onNavigate(Screen.Support) }
          }
        }
      }

      // Tutorial Academy Highlights Banner
      item {
        ScreenTutorialBanner(
          title = "কীভাবে কাজ করবেন? ভিডিও দেখে শিখুন",
          subtitle = "জিমেইল বিক্রি, ডিপোজিট, উইথড্র ও টাস্ক সম্পন্ন করার সম্পূর্ণ গাইড",
          onWatchVideo = {
            viewModel.openExternalUrl(
              context,
              "https://www.youtube.com/results?search_query=how+to+earn+money+online+bangla+tutorial"
            )
          },
          onViewAllTutorials = { onNavigate(Screen.Tutorials) }
        )
      }

      // 5.5 টাস্ক ও ইনকাম সেকশন (Tasks & Income Section)
      item {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "টাস্ক ও ইনকাম",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            TextButton(onClick = { onNavigate(Screen.Tasks) }) {
              Text("সকল কাজ দেখুন (${activeTasks.size})", color = BluePrimary, style = MaterialTheme.typography.labelMedium)
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          if (activeTasks.isEmpty()) {
            Text(
              text = "বর্তমানে কোনো সক্রিয় কাজ নেই।",
              style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
              modifier = Modifier.padding(vertical = 8.dp)
            )
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              activeTasks.take(2).forEach { task ->
                val isSubmitted = submittedTaskIds.contains(task.id)
                val isSlotFull = task.currentSubmissions >= task.maxSubmissions
                TaskCard(
                  task = task,
                  isSubmitted = isSubmitted,
                  isSlotFull = isSlotFull,
                  onActionClick = { selectedTaskForPopup = task }
                )
              }
            }
          }
        }
      }

      // 6. Recent Transactions Ledger
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "সাম্প্রতিক লেনদেন লেজার",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )
              TextButton(onClick = { onNavigate(Screen.Wallet) }) {
                Text("সব দেখুন", color = BluePrimary, style = MaterialTheme.typography.labelMedium)
              }
            }

            if (transactions.isEmpty()) {
              Text(
                text = "আপনার কোনো লেনদেন এখনো সম্পন্ন হয়নি।",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                modifier = Modifier.padding(vertical = 12.dp)
              )
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                transactions.take(4).forEach { tx ->
                  TransactionRow(tx)
                }
              }
            }
          }
        }
      }

      // 7. Advertise With Us / Sponsorship Card
      item {
        AdvertiseWithUsCard(
          onContactWhatsApp = { viewModel.contactForSponsorship(context, viaEmail = false) },
          onContactEmail = { viewModel.contactForSponsorship(context, viaEmail = true) },
          onContactTelegram = { viewModel.contactViaTelegram(context) },
          onViewPackages = { onNavigate(Screen.Advertise) }
        )
      }
    }
  }

  // Task Details & Proof Submission Popup Modal
  selectedTaskForPopup?.let { task ->
    val isSubmitted = submittedTaskIds.contains(task.id)
    val isSlotFull = task.currentSubmissions >= task.maxSubmissions
    TaskDetailsModal(
      task = task,
      isSubmitted = isSubmitted,
      isSlotFull = isSlotFull,
      userAccountStatus = user?.accountStatus ?: "inactive",
      onDismiss = { selectedTaskForPopup = null },
      onVisitLink = {
        try {
          val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(task.taskUrl))
          intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
          context.startActivity(intent)
        } catch (e: Exception) {
          viewModel.showMessage("লিংক ওপেন করা সম্ভব হয়নি: ${task.taskUrl}")
        }
      },
      onSubmitProof = { proofText, screenshotUri ->
        viewModel.submitTaskProof(
          taskId = task.id,
          proof = proofText,
          screenshotUri = screenshotUri,
          onComplete = {
            selectedTaskForPopup = null
          }
        )
      },
      onNavigateToActivation = {
        selectedTaskForPopup = null
        onNavigate(Screen.Activation)
      }
    )
  }
}

@Composable
fun QuickActionIcon(
  icon: ImageVector,
  label: String,
  color: Color,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(color.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary, fontWeight = FontWeight.Medium),
      maxLines = 1
    )
  }
}

@Composable
fun TransactionRow(tx: TransactionEntity) {
  val isPositive = tx.type in listOf("DEPOSIT", "TASK_REWARD", "REFERRAL_REWARD", "ADJUSTMENT")
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (isPositive) EmeraldGreenBg else RedBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isPositive) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
          contentDescription = null,
          tint = if (isPositive) EmeraldGreenDark else RedError,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = tx.description,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = TextPrimary),
          maxLines = 1
        )
        Text(
          text = formatDateTime(tx.createdAt),
          style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )
      }
    }

    Text(
      text = (if (isPositive) "+ " else "- ") + formatBdt(tx.amount),
      style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Bold,
        color = if (isPositive) EmeraldGreenDark else RedError
      )
    )
  }
}

@Composable
fun UserBottomNav(
  currentScreen: Screen,
  onNavigate: (Screen) -> Unit
) {
  NavigationBar(
    containerColor = SurfaceCard,
    tonalElevation = 4.dp
  ) {
    NavigationBarItem(
      selected = currentScreen is Screen.Dashboard,
      onClick = { onNavigate(Screen.Dashboard) },
      icon = { Icon(Icons.Default.Dashboard, contentDescription = "ড্যাশবোর্ড") },
      label = { Text("ড্যাশবোর্ড") }
    )
    NavigationBarItem(
      selected = currentScreen is Screen.Tasks,
      onClick = { onNavigate(Screen.Tasks) },
      icon = { Icon(Icons.Default.Task, contentDescription = "কাজসমূহ") },
      label = { Text("কাজসমূহ") }
    )
    NavigationBarItem(
      selected = currentScreen is Screen.Wallet,
      onClick = { onNavigate(Screen.Wallet) },
      icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "ওয়ালেট") },
      label = { Text("ওয়ালেট") }
    )
    NavigationBarItem(
      selected = currentScreen is Screen.Referral,
      onClick = { onNavigate(Screen.Referral) },
      icon = { Icon(Icons.Default.Share, contentDescription = "রেফার") },
      label = { Text("রেফার") }
    )
    NavigationBarItem(
      selected = currentScreen is Screen.Profile,
      onClick = { onNavigate(Screen.Profile) },
      icon = { Icon(Icons.Default.Person, contentDescription = "প্রোফাইল") },
      label = { Text("প্রোফাইল") }
    )
  }
}
