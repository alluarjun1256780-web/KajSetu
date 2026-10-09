package com.example.ui.screens

import androidx.activity.compose.BackHandler
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.*
import com.example.ui.KajSetuViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalScreen(
  viewModel: KajSetuViewModel,
  onBack: () -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()

  // Security Check: Role must be admin
  if (currentUser?.role != "admin") {
    BackHandler { onBack() }
    LaunchedEffect(Unit) {
      viewModel.showMessage("অ্যাক্সেস ডিনাইড: অ্যাডমিন প্যানেল শুধুমাত্র প্রধান অ্যাডমিনের জন্য সংরক্ষিত।")
      onBack()
    }
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("প্রবেশাধিকার সংরক্ষিত") },
          navigationIcon = {
            IconButton(onClick = onBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
            }
          }
        )
      }
    ) { padding ->
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = RedError, modifier = Modifier.size(64.dp))
          Spacer(modifier = Modifier.height(16.dp))
          Text("অ্যাক্সেস ডিনাইড!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
          Text("এই প্যানেলে শুধুমাত্র অনুমোদিত অ্যাডমিনের প্রবেশাধিকার রয়েছে।", color = TextSecondary)
          Spacer(modifier = Modifier.height(16.dp))
          Button(onClick = onBack) { Text("ফিরে যান") }
        }
      }
    }
    return
  }

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("ওভারভিউ", "ইউজার", "টাস্ক", "জিমেইল রিভিউ", "রিডিম কোড", "ডিপোজিট", "উইথড্র", "অ্যাক্টিভেশন", "বিজ্ঞাপন এডমিন", "সেটিংস", "সাপোর্ট", "অডিট লগ")

  Scaffold(
    topBar = {
      Surface(color = Color(0xFF0F172A), tonalElevation = 4.dp) {
        Column {
          TopAppBar(
            title = {
              Column {
                Text("কাজসেতু অ্যাডমিন কন্ট্রোল", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("অ্যাডমিন: ${currentUser?.fullName}", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodySmall)
              }
            },
            navigationIcon = {
              IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান", tint = Color.White)
              }
            },
            actions = {
              IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                Icon(Icons.Default.Dashboard, contentDescription = "ইউজার ড্যাশবোর্ড", tint = Color.White)
              }
              IconButton(onClick = { viewModel.logout() }) {
                Icon(Icons.Default.Logout, contentDescription = "লগআউট", tint = Color.White)
              }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
          )

          ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1E293B),
            contentColor = EmeraldGreen,
            edgePadding = 8.dp
          ) {
            tabs.forEachIndexed { index, title ->
              Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = {
                  Text(
                    title,
                    color = if (selectedTab == index) EmeraldGreen else Color(0xFFCBD5E1),
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                  )
                }
              )
            }
          }
        }
      }
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(SlateBackground)
    ) {
      when (selectedTab) {
        0 -> AdminOverviewTab(viewModel)
        1 -> AdminUsersTab(viewModel)
        2 -> AdminTasksTab(viewModel)
        3 -> AdminGmailReviewsTab(viewModel)
        4 -> AdminRedeemCodeTab(viewModel)
        5 -> AdminDepositsTab(viewModel)
        6 -> AdminWithdrawalsTab(viewModel)
        7 -> AdminActivationsTab(viewModel)
        8 -> AdminAdsManagerScreen(viewModel)
        9 -> AdminSettingsTab(viewModel)
        10 -> AdminSupportTab(viewModel)
        11 -> AdminAuditLogsTab(viewModel)
      }
    }
  }
}

@Composable
fun AdminOverviewTab(viewModel: KajSetuViewModel) {
  val totalUsers by viewModel.totalUsersCount.collectAsState()
  val activeUsers by viewModel.activeUsersCount.collectAsState()
  val pendingSubmissions by viewModel.pendingSubmissions.collectAsState()
  val pendingDeposits by viewModel.pendingDeposits.collectAsState()
  val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsState()
  val pendingActivations by viewModel.pendingActivations.collectAsState()

  var broadcastTitle by remember { mutableStateOf("") }
  var broadcastMsg by remember { mutableStateOf("") }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text("সিস্টেম পরিসংখ্যান ও লাইভ ডাটা", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }

    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
          title = "মোট ইউজার",
          value = "$totalUsers জন",
          icon = Icons.Default.People,
          iconColor = BluePrimary,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "সক্রিয় ইউজার",
          value = "$activeUsers জন",
          icon = Icons.Default.Verified,
          iconColor = EmeraldGreen,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
          title = "পেন্ডিং ডিপোজিট",
          value = "${pendingDeposits.size}টি",
          icon = Icons.Default.AddCard,
          iconColor = AmberGold,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "পেন্ডিং উইথড্র",
          value = "${pendingWithdrawals.size}টি",
          icon = Icons.Default.Payments,
          iconColor = RedError,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
          title = "টাস্ক সাবমিশন",
          value = "${pendingSubmissions.size}টি রিভিউ বাকি",
          icon = Icons.Default.Task,
          iconColor = Color(0xFF8B5CF6),
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "পেন্ডিং অ্যাক্টিভেশন",
          value = "${pendingActivations.size}টি",
          icon = Icons.Default.HowToReg,
          iconColor = BluePrimary,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Broadcast Announcement Section
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("📢 সকল ব্যবহারকারীকে নোটিফিকেশন পাঠান", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = broadcastTitle,
            onValueChange = { broadcastTitle = it },
            label = { Text("শিরোনাম") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = broadcastMsg,
            onValueChange = { broadcastMsg = it },
            label = { Text("বার্তা বিবরণ") },
            modifier = Modifier.fillMaxWidth().height(80.dp),
            shape = RoundedCornerShape(10.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = {
              viewModel.adminBroadcast(broadcastTitle, broadcastMsg)
              broadcastTitle = ""
              broadcastMsg = ""
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
          ) {
            Text("ব্রডকাস্ট পাঠান")
          }
        }
      }
    }
  }
}

@Composable
fun AdminUsersTab(viewModel: KajSetuViewModel) {
  val users by viewModel.allUsers.collectAsState()
  var searchQuery by remember { mutableStateOf("") }

  val filteredUsers = users.filter {
    it.fullName.contains(searchQuery, ignoreCase = true) ||
        it.username.contains(searchQuery, ignoreCase = true) ||
        it.email.contains(searchQuery, ignoreCase = true) ||
        it.phone.contains(searchQuery)
  }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        label = { Text("ইউজার খুঁজুন (নাম, ইমেইল, ফোন)") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )
    }

    items(filteredUsers) { user ->
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
            Column {
              Text(user.fullName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
              Text("@${user.username} • ${user.phone}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
              Text(user.email, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
            }
            StatusPill(status = user.accountStatus)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (user.accountStatus != "active") {
              Button(
                onClick = { viewModel.adminUpdateUserStatus(user.id, "active") },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1f)
              ) {
                Text("সক্রিয় করুন", fontSize = 11.sp)
              }
            } else {
              OutlinedButton(
                onClick = { viewModel.adminUpdateUserStatus(user.id, "inactive") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("নিষ্ক্রিয় করুন", fontSize = 11.sp)
              }
            }

            Button(
              onClick = {
                val nextStatus = if (user.accountStatus == "suspended") "inactive" else "suspended"
                viewModel.adminUpdateUserStatus(user.id, nextStatus)
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = if (user.accountStatus == "suspended") BluePrimary else RedError),
              modifier = Modifier.weight(1f)
            ) {
              Text(if (user.accountStatus == "suspended") "আন-সাসপেন্ড" else "সাসপেন্ড", fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTasksTab(viewModel: KajSetuViewModel) {
  val tasks by viewModel.allTasks.collectAsState()
  val pendingSubmissions by viewModel.pendingSubmissions.collectAsState()
  val context = androidx.compose.ui.platform.LocalContext.current

  // Filter & Search states
  var searchQuery by remember { mutableStateOf("") }
  var selectedStatusFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "PAUSED"

  // Dialog states
  var showCreateDialog by remember { mutableStateOf(false) }
  var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
  var deletingTaskId by remember { mutableStateOf<String?>(null) }

  var rejectingSubmissionId by remember { mutableStateOf<String?>(null) }
  var rejectionReasonText by remember { mutableStateOf("") }

  // Task creation state
  var title by remember { mutableStateOf("") }
  var desc by remember { mutableStateOf("") }
  var instructions by remember { mutableStateOf("") }
  var taskUrl by remember { mutableStateOf("https://youtube.com") }
  var rewardText by remember { mutableStateOf("20") }
  var category by remember { mutableStateOf("সোশ্যাল মিডিয়া") }
  var maxSubsText by remember { mutableStateOf("100") }
  var proofType by remember { mutableStateOf("স্ক্রিনশট ও ইউজারনেম") }
  var deadlineDaysText by remember { mutableStateOf("3") }

  // Filtered task list
  val filteredTasks = remember(tasks, searchQuery, selectedStatusFilter) {
    tasks.filter { t ->
      val matchesQuery = t.title.contains(searchQuery, ignoreCase = true) ||
          t.category.contains(searchQuery, ignoreCase = true) ||
          t.description.contains(searchQuery, ignoreCase = true)
      val matchesStatus = when (selectedStatusFilter) {
        "ACTIVE" -> t.status == "ACTIVE"
        "PAUSED" -> t.status == "PAUSED"
        else -> true
      }
      matchesQuery && matchesStatus
    }
  }

  val activeCount = tasks.count { it.status == "ACTIVE" }
  val pausedCount = tasks.count { it.status == "PAUSED" }
  val totalBudget = tasks.sumOf { it.rewardAmount * it.maxSubmissions }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header & Cloud Status
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                "মাইক্রো-টাস্ক ও ফায়ারস্টোর ম্যানেজমেন্ট",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
              )
              Text(
                "ক্লাউড ডেটাবেস ও স্থানীয় স্টোরেজ লাইভ সিঙ্ক",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
              )
            }
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Color(0xFF065F46)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.CloudDone, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Firestore সিঙ্কড", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Stats Metrics Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("মোট টাস্ক", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Text("${tasks.size}টি", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              }
            }
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("সক্রিয় কাজ", color = EmeraldGreen, fontSize = 11.sp)
                Text("${activeCount}টি", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              }
            }
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("মোট রিওয়ার্ড পুল", color = AmberGold, fontSize = 11.sp)
                Text(formatBdt(totalBudget), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
          }
        }
      }
    }

    // Action Header & Add Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("টাস্ক ডেটাবেস (${filteredTasks.size}টি)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Button(
          onClick = { showCreateDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
          modifier = Modifier.testTag("admin_add_task_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("নতুন টাস্ক তৈরি")
        }
      }
    }

    // Search and Filter row
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("টাস্ক খুঁজুন (শিরোনাম বা ক্যাটাগরি)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                }
              }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = selectedStatusFilter == "ALL",
              onClick = { selectedStatusFilter = "ALL" },
              label = { Text("সবগুলো (${tasks.size})") }
            )
            FilterChip(
              selected = selectedStatusFilter == "ACTIVE",
              onClick = { selectedStatusFilter = "ACTIVE" },
              label = { Text("সক্রিয় (${activeCount})") }
            )
            FilterChip(
              selected = selectedStatusFilter == "PAUSED",
              onClick = { selectedStatusFilter = "PAUSED" },
              label = { Text("পজড (${pausedCount})") }
            )
          }
        }
      }
    }

    // Pending Submissions Section
    if (pendingSubmissions.isNotEmpty()) {
      item {
        Text(
          "পেন্ডিং সাবমিশন রিভিউ (${pendingSubmissions.size}টি)",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
        )
      }

      items(pendingSubmissions) { sub ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Column {
                Text(sub.userFullName + " (@" + sub.username + ")", fontWeight = FontWeight.Bold)
                Text("সময়: " + formatDateTime(sub.submittedAt), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
              }
              Text(formatBdt(sub.rewardAmount), color = EmeraldGreenDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("কাজ: " + sub.taskTitle, style = MaterialTheme.typography.bodySmall.copy(color = BluePrimary, fontWeight = FontWeight.SemiBold))

            Spacer(modifier = Modifier.height(6.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateBackground)
                .padding(10.dp)
            ) {
              Column {
                Text("জমা দেওয়া প্রমাণ বিবরণ:", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Text(sub.proofData.ifBlank { "বিবরণ প্রদান করা হয়নি" }, style = MaterialTheme.typography.bodySmall)
              }
            }

            if (sub.screenshotUri.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(EmeraldGreenBg)
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Image, contentDescription = null, tint = EmeraldGreenDark, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("স্ক্রিনশট সংযুক্ত রয়েছে", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreenDark, fontWeight = FontWeight.Bold))
                }
                TextButton(
                  onClick = {
                    try {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sub.screenshotUri)).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                      }
                      context.startActivity(intent)
                    } catch (e: Exception) {
                      viewModel.showMessage("স্ক্রিনশট ওপেন করা যায়নি: ${sub.screenshotUri}")
                    }
                  },
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text("স্ক্রিনশট দেখুন", fontSize = 11.sp, color = BluePrimaryDark)
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { viewModel.adminReviewSubmission(sub.id, true) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Approve & রিওয়ার্ড প্রদান", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              Button(
                onClick = {
                  rejectingSubmissionId = sub.id
                  rejectionReasonText = ""
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedError),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("বাতিল করুন", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // Task List Header
    item {
      Text(
        "ফায়ারস্টোর মাইক্রো-টাস্ক তালিকা (${filteredTasks.size}টি)",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
      )
    }

    if (filteredTasks.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
              Spacer(modifier = Modifier.height(8.dp))
              Text("কোনো মাইক্রো-টাস্ক পাওয়া যায়নি।", color = TextSecondary)
            }
          }
        }
      }
    } else {
      items(filteredTasks) { t ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Title & Reward Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
              Column(modifier = Modifier.weight(1f)) {
                Text(t.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BlueLightBg
                  ) {
                    Text(t.category, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, color = BluePrimary, fontWeight = FontWeight.SemiBold)
                  }
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (t.status == "ACTIVE") Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                  ) {
                    Text(
                      if (t.status == "ACTIVE") "সক্রিয়" else "পজড",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      fontSize = 11.sp,
                      color = if (t.status == "ACTIVE") EmeraldGreenDark else AmberGold,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEFF6FF)
              ) {
                Text(
                  formatBdt(t.rewardAmount),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  fontWeight = FontWeight.Bold,
                  color = BluePrimary,
                  fontSize = 16.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (t.description.isNotBlank()) {
              Text(t.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), maxLines = 2)
              Spacer(modifier = Modifier.height(6.dp))
            }

            // Info rows
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("স্লট: ${t.currentSubmissions}/${t.maxSubmissions} সম্পন্ন", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
              Text("আইডি: ${t.id.take(8)}...", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Status Toggle, Edit, Delete
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Toggle Status
              Button(
                onClick = {
                  val nextSt = if (t.status == "ACTIVE") "PAUSED" else "ACTIVE"
                  viewModel.adminToggleTask(t.id, nextSt)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (t.status == "ACTIVE") AmberGold else EmeraldGreen),
                modifier = Modifier.weight(1.2f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Text(if (t.status == "ACTIVE") "পজ করুন" else "চালু করুন", fontSize = 11.sp)
              }

              // Edit Button
              OutlinedButton(
                onClick = { editingTask = t },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("এডিট", fontSize = 11.sp)
              }

              // Delete Button
              OutlinedButton(
                onClick = { deletingTaskId = t.id },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedError),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(14.dp), tint = RedError)
                Spacer(modifier = Modifier.width(4.dp))
                Text("ডিলিট", fontSize = 11.sp, color = RedError)
              }
            }
          }
        }
      }
    }
  }

  // Edit Task Dialog
  if (editingTask != null) {
    val currentT = editingTask!!
    var editTitle by remember(currentT) { mutableStateOf(currentT.title) }
    var editDesc by remember(currentT) { mutableStateOf(currentT.description) }
    var editInstructions by remember(currentT) { mutableStateOf(currentT.instructions) }
    var editTaskUrl by remember(currentT) { mutableStateOf(currentT.taskUrl) }
    var editRewardText by remember(currentT) { mutableStateOf(currentT.rewardAmount.toString()) }
    var editCategory by remember(currentT) { mutableStateOf(currentT.category) }
    var editMaxSubsText by remember(currentT) { mutableStateOf(currentT.maxSubmissions.toString()) }
    var editProofType by remember(currentT) { mutableStateOf(currentT.proofType) }

    AlertDialog(
      onDismissRequest = { editingTask = null },
      title = { Text("টাস্ক এডিট করুন (Firestore)", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("কাজের শিরোনাম") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = editDesc, onValueChange = { editDesc = it }, label = { Text("সংক্ষিপ্ত বিবরণ") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = editTaskUrl, onValueChange = { editTaskUrl = it }, label = { Text("কাজের লিংক (URL)") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = editInstructions, onValueChange = { editInstructions = it }, label = { Text("ধাপভিত্তিক নির্দেশনা") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = editRewardText, onValueChange = { editRewardText = it }, label = { Text("রিওয়ার্ড (টাকা)") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = editCategory, onValueChange = { editCategory = it }, label = { Text("ক্যাটাগরি") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = editMaxSubsText, onValueChange = { editMaxSubsText = it }, label = { Text("সর্বোচ্চ স্লট") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = editProofType, onValueChange = { editProofType = it }, label = { Text("প্রমাণের বিবরণ") }, modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val rew = editRewardText.toDoubleOrNull() ?: currentT.rewardAmount
            val maxS = editMaxSubsText.toIntOrNull() ?: currentT.maxSubmissions
            val updated = currentT.copy(
              title = editTitle.trim(),
              description = editDesc.trim(),
              instructions = editInstructions.trim(),
              taskUrl = editTaskUrl.trim(),
              rewardAmount = rew,
              category = editCategory.trim(),
              maxSubmissions = maxS,
              proofType = editProofType.trim()
            )
            viewModel.adminUpdateTask(updated)
            editingTask = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
          Text("সংরক্ষণ করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingTask = null }) { Text("বাতিল") }
      }
    )
  }

  // Delete Confirmation Dialog
  if (deletingTaskId != null) {
    val taskIdToDelete = deletingTaskId!!
    val taskToDelete = tasks.find { it.id == taskIdToDelete }

    AlertDialog(
      onDismissRequest = { deletingTaskId = null },
      title = { Text("টাস্ক ডিলিট নিশ্চিত করুন", fontWeight = FontWeight.Bold, color = RedError) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("আপনি কি নিশ্চিতভাবে এই মাইক্রো-টাস্কটি মুছে ফেলতে চান?")
          if (taskToDelete != null) {
            Text("\"${taskToDelete.title}\"", fontWeight = FontWeight.Bold)
          }
          Text(
            "এটি ফায়ারস্টোর ক্লাউড ডেটাবেস এবং লোকাল স্টোরেজ উভয় স্থান থেকেই স্থায়ীভাবে মুছে ফেলা হবে।",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.adminDeleteTask(taskIdToDelete)
            deletingTaskId = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = RedError)
        ) {
          Text("ডিলিট করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { deletingTaskId = null }) { Text("বাতিল") }
      }
    )
  }

  // Rejection Dialog
  if (rejectingSubmissionId != null) {
    AlertDialog(
      onDismissRequest = { rejectingSubmissionId = null },
      title = { Text("টাস্ক সাবমিশন বাতিলের কারণ", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("গ্রাহককে জানানোর জন্য কারণ লিখুন:", style = MaterialTheme.typography.bodySmall)
          OutlinedTextField(
            value = rejectionReasonText,
            onValueChange = { rejectionReasonText = it },
            label = { Text("বাতিলের কারণ") },
            placeholder = { Text("যেমন: স্ক্রিনশট স্পষ্ট নয় বা কাজ সম্পূর্ণ করা হয়নি।") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            rejectingSubmissionId?.let { subId ->
              viewModel.adminReviewSubmission(
                subId = subId,
                isApproved = false,
                reason = rejectionReasonText.ifBlank { "সঠিক নিয়ম মেনে কাজ সম্পন্ন করা হয়নি।" }
              )
            }
            rejectingSubmissionId = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = RedError)
        ) {
          Text("বাতিল নিশ্চিত করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { rejectingSubmissionId = null }) { Text("ফিরে যান") }
      }
    )
  }

  // Create Task Dialog
  if (showCreateDialog) {
    AlertDialog(
      onDismissRequest = { showCreateDialog = false },
      title = { Text("নতুন মাইক্রো টাস্ক তৈরি (Firestore)", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("কাজের শিরোনাম") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("সংক্ষিপ্ত বিবরণ") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = taskUrl, onValueChange = { taskUrl = it }, label = { Text("কাজের লিংক (URL)") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("ধাপভিত্তিক নির্দেশনা") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = rewardText, onValueChange = { rewardText = it }, label = { Text("রিওয়ার্ড (টাকা)") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("ক্যাটাগরি") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = maxSubsText, onValueChange = { maxSubsText = it }, label = { Text("সর্বোচ্চ স্লট") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = proofType, onValueChange = { proofType = it }, label = { Text("প্রমাণের বিবরণ") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = deadlineDaysText, onValueChange = { deadlineDaysText = it }, label = { Text("সময়সীমা (দিন)") }, modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val rew = rewardText.toDoubleOrNull() ?: 10.0
            val maxS = maxSubsText.toIntOrNull() ?: 100
            val days = deadlineDaysText.toIntOrNull() ?: 3
            val deadlineTime = System.currentTimeMillis() + (days.toLong() * 24 * 60 * 60 * 1000)

            viewModel.adminCreateTask(
              TaskEntity(
                title = title.trim(),
                description = desc.trim(),
                instructions = instructions.trim(),
                taskUrl = taskUrl.trim(),
                rewardAmount = rew,
                category = category.trim(),
                maxSubmissions = maxS,
                proofType = proofType.trim(),
                deadlineDays = days,
                deadline = deadlineTime
              )
            )
            showCreateDialog = false
            title = ""
            desc = ""
          },
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
          Text("Firestore-এ তৈরি করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateDialog = false }) { Text("বাতিল") }
      }
    )
  }
}

@Composable
fun AdminDepositsTab(viewModel: KajSetuViewModel) {
  val pendingDeposits by viewModel.pendingDeposits.collectAsState()

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text("পেন্ডিং ডিপোজিট অনুমোদন (${pendingDeposits.size}টি)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }

    if (pendingDeposits.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.CheckCircle,
          title = "সব ডিপোজিট নিষ্পত্তি করা হয়েছে",
          message = "এই মুহূর্তে কোনো পেন্ডিং ডিপোজিট রিকোয়েস্ট নেই।"
        )
      }
    } else {
      items(pendingDeposits) { dep ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(dep.username + " (" + dep.userPhone + ")", fontWeight = FontWeight.Bold)
              Text(formatBdt(dep.amount), fontWeight = FontWeight.Bold, color = BluePrimaryDark, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("মাধ্যম: ${dep.method} | প্রেরক নম্বর: ${dep.senderNumber}", style = MaterialTheme.typography.bodySmall)
            Text("TrxID: ${dep.transactionId}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark))
            Text("আবেদনের সময়: " + formatDateTime(dep.createdAt), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { viewModel.adminApproveDeposit(dep.id) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1f)
              ) {
                Text("অনুমোদন করুন")
              }
              Button(
                onClick = { viewModel.adminRejectDeposit(dep.id, "ভুল TrxID বা টাকা পাওয়া যায়নি।") },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedError),
                modifier = Modifier.weight(1f)
              ) {
                Text("বাতিল করুন")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminWithdrawalsTab(viewModel: KajSetuViewModel) {
  val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsState()

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text("পেন্ডিং উইথড্র রিকোয়েস্ট (${pendingWithdrawals.size}টি)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }

    if (pendingWithdrawals.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.CheckCircle,
          title = "সব উইথড্র সম্পন্ন হয়েছে",
          message = "এই মুহূর্তে কোনো পেন্ডিং উইথড্র নেই।"
        )
      }
    } else {
      items(pendingWithdrawals) { w ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("ইউজার: ${w.username}", fontWeight = FontWeight.Bold)
              Text("নেট প্রদেয়: " + formatBdt(w.netAmount), fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("মাধ্যম: ${w.method} | নম্বর: ${w.receiverNumber}", style = MaterialTheme.typography.bodySmall)
            Text("মোট তোলা: ${formatBdt(w.amount)} (ফি: ${formatBdt(w.fee)})", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            Text("সময়: " + formatDateTime(w.createdAt), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { viewModel.adminApproveWithdrawal(w.id) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1f)
              ) {
                Text("টাকা পাঠানো হয়েছে")
              }
              Button(
                onClick = { viewModel.adminRejectWithdrawal(w.id, "অ্যাকাউন্ট তথ্যে ভুল") },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedError),
                modifier = Modifier.weight(1f)
              ) {
                Text("বাতিল ও রিফান্ড")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminActivationsTab(viewModel: KajSetuViewModel) {
  val pendingActivations by viewModel.pendingActivations.collectAsState()

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text("অ্যাকাউন্ট অ্যাক্টিভেশন রিকোয়েস্ট (${pendingActivations.size}টি)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }

    if (pendingActivations.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.Verified,
          title = "সব অ্যাক্টিভেশন পর্যালোচনা সম্পন্ন",
          message = "এই মুহূর্তে কোনো পেন্ডিং অ্যাক্টিভেশন পেমেন্ট নেই।"
        )
      }
    } else {
      items(pendingActivations) { req ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("ইউজার: ${req.username}", fontWeight = FontWeight.Bold)
              Text(formatBdt(req.fee), fontWeight = FontWeight.Bold, color = BluePrimaryDark)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("মাধ্যম: ${req.method} | প্রেরক: ${req.senderNumber}", style = MaterialTheme.typography.bodySmall)
            Text("TrxID: ${req.transactionId}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark))

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { viewModel.adminApproveActivation(req.id) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1f)
              ) {
                Text("সক্রিয় করুন")
              }
              Button(
                onClick = { viewModel.adminRejectActivation(req.id, "পেমেন্ট তথ্য ভেরিফাই করা যায়নি।") },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedError),
                modifier = Modifier.weight(1f)
              ) {
                Text("বাতিল করুন")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminSettingsTab(viewModel: KajSetuViewModel) {
  val settings by viewModel.siteSettings.collectAsState()
  var currentSettings by remember(settings) { mutableStateOf(settings ?: SiteSettingsEntity()) }

  var actFee by remember(currentSettings) { mutableStateOf(currentSettings.activationFee.toString()) }
  var minDep by remember(currentSettings) { mutableStateOf(currentSettings.minDeposit.toString()) }
  var maxDep by remember(currentSettings) { mutableStateOf(currentSettings.maxDeposit.toString()) }
  var minWith by remember(currentSettings) { mutableStateOf(currentSettings.minWithdrawal.toString()) }
  var maxWith by remember(currentSettings) { mutableStateOf(currentSettings.maxWithdrawal.toString()) }
  var withFee by remember(currentSettings) { mutableStateOf(currentSettings.withdrawalFeePercent.toString()) }
  var refRew by remember(currentSettings) { mutableStateOf(currentSettings.referralReward.toString()) }
  var bkash by remember(currentSettings) { mutableStateOf(currentSettings.bkashNumber) }
  var nagad by remember(currentSettings) { mutableStateOf(currentSettings.nagadNumber) }
  var rocket by remember(currentSettings) { mutableStateOf(currentSettings.rocketNumber) }
  var telegramUrl by remember(currentSettings) { mutableStateOf(currentSettings.telegramChannelUrl) }
  var notice by remember(currentSettings) { mutableStateOf(currentSettings.noticeBanner) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text("সাইট ও আর্থিক পলিসি কনফিগারেশন", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }

    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(value = actFee, onValueChange = { actFee = it }, label = { Text("অ্যাকাউন্ট অ্যাক্টিভেশন ফি (টাকা)") }, modifier = Modifier.fillMaxWidth())
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = minDep, onValueChange = { minDep = it }, label = { Text("মিনিমাম ডিপোজিট") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = maxDep, onValueChange = { maxDep = it }, label = { Text("ম্যাক্সিমাম ডিপোজিট") }, modifier = Modifier.weight(1f))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = minWith, onValueChange = { minWith = it }, label = { Text("মিনিমাম উইথড্র") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = maxWith, onValueChange = { maxWith = it }, label = { Text("ম্যাক্সিমাম উইথড্র") }, modifier = Modifier.weight(1f))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = withFee, onValueChange = { withFee = it }, label = { Text("উইথড্র ফি (%)") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = refRew, onValueChange = { refRew = it }, label = { Text("রেফারেল বোনাস (টাকা)") }, modifier = Modifier.weight(1f))
          }
          OutlinedTextField(value = bkash, onValueChange = { bkash = it }, label = { Text("বিকাশ নম্বর") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = nagad, onValueChange = { nagad = it }, label = { Text("নগদ নম্বর") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(value = rocket, onValueChange = { rocket = it }, label = { Text("রকেট নম্বর") }, modifier = Modifier.fillMaxWidth())
          OutlinedTextField(
            value = telegramUrl,
            onValueChange = { telegramUrl = it },
            label = { Text("অফিসিয়াল টেলিগ্রাম চ্যানেল ইউআরএল (Telegram Channel URL)") },
            placeholder = { Text("https://t.me/KajSetuOfficial") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(value = notice, onValueChange = { notice = it }, label = { Text("নোটিশ ব্যানার টেক্সট") }, modifier = Modifier.fillMaxWidth())

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              val updated = currentSettings.copy(
                activationFee = actFee.toDoubleOrNull() ?: 100.0,
                minDeposit = minDep.toDoubleOrNull() ?: 100.0,
                maxDeposit = maxDep.toDoubleOrNull() ?: 25000.0,
                minWithdrawal = minWith.toDoubleOrNull() ?: 200.0,
                maxWithdrawal = maxWith.toDoubleOrNull() ?: 10000.0,
                withdrawalFeePercent = withFee.toDoubleOrNull() ?: 2.0,
                referralReward = refRew.toDoubleOrNull() ?: 25.0,
                bkashNumber = bkash,
                nagadNumber = nagad,
                rocketNumber = rocket,
                telegramChannelUrl = telegramUrl.ifBlank { "https://t.me/KajSetuOfficial" },
                noticeBanner = notice
              )
              viewModel.adminUpdateSettings(updated)
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            modifier = Modifier.fillMaxWidth().height(48.dp)
          ) {
            Text("সেটিংস সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun AdminSupportTab(viewModel: KajSetuViewModel) {
  val tickets by viewModel.allTickets.collectAsState()
  var replyTicketId by remember { mutableStateOf<String?>(null) }
  var replyText by remember { mutableStateOf("") }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Text("সাপোর্ট টিকেট ও গ্রাহক সেবা (${tickets.size}টি)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }

    items(tickets) { t ->
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(t.username + " • " + t.category, fontWeight = FontWeight.Bold)
            StatusPill(t.status)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("বিষয়: " + t.subject, style = MaterialTheme.typography.titleSmall.copy(color = BluePrimaryDark))
          Text(t.message, style = MaterialTheme.typography.bodySmall)

          if (t.adminReply.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text("উত্তর দেওয়া হয়েছে: ${t.adminReply}", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreenDark))
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              replyTicketId = t.id
              replyText = t.adminReply
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
          ) {
            Text("উত্তর দিন / আপডেট করুন", fontSize = 11.sp)
          }
        }
      }
    }
  }

  if (replyTicketId != null) {
    AlertDialog(
      onDismissRequest = { replyTicketId = null },
      title = { Text("টিকেটের উত্তর দিন") },
      text = {
        OutlinedTextField(
          value = replyText,
          onValueChange = { replyText = it },
          label = { Text("অ্যাডমিন উত্তর") },
          modifier = Modifier.fillMaxWidth().height(120.dp)
        )
      },
      confirmButton = {
        Button(onClick = {
          replyTicketId?.let { id ->
            viewModel.adminReplyTicket(id, replyText, "RESOLVED")
          }
          replyTicketId = null
        }) {
          Text("পাঠান ও সম্পন্ন করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { replyTicketId = null }) { Text("বাতিল") }
      }
    )
  }
}

@Composable
fun AdminAuditLogsTab(viewModel: KajSetuViewModel) {
  val logs by viewModel.auditLogs.collectAsState()

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text("নিরাপত্তা ও আর্থিক অডিট লগ (অপরিবর্তনীয়)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }

    items(logs) { log ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(log.action, fontWeight = FontWeight.Bold, color = BluePrimaryDark, style = MaterialTheme.typography.labelMedium)
            Text(formatDateTime(log.timestamp), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(log.details, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
          Spacer(modifier = Modifier.height(4.dp))
          Text("অ্যাডমিন: ${log.adminUsername} • টার্গেট: ${log.targetType}", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
        }
      }
    }
  }
}

@Composable
fun AdminAdsTab(viewModel: KajSetuViewModel) {
  val ads by viewModel.allAds.collectAsState()
  val settings by viewModel.siteSettings.collectAsState()
  val context = androidx.compose.ui.platform.LocalContext.current

  var showCreateDialog by remember { mutableStateOf(false) }

  // New Ad form states
  var adTitle by remember { mutableStateOf("") }
  var adSubtitle by remember { mutableStateOf("") }
  var adTargetUrl by remember { mutableStateOf("https://t.me/") }
  var adPosition by remember { mutableStateOf("DASHBOARD") }
  var adButtonText by remember { mutableStateOf("ভিজিট করুন") }

  // Sponsorship contact settings states
  var contactWp by remember(settings) { mutableStateOf(settings?.adContactWhatsapp ?: "8801712345678") }
  var contactEmail by remember(settings) { mutableStateOf(settings?.adContactEmail ?: "sponsor@kajsetu.com") }

  val positions = listOf(
    "HEADER" to "টপ হেডার",
    "DASHBOARD" to "ড্যাশবোর্ড",
    "BETWEEN_TASKS" to "টাস্কের মাঝে",
    "FOOTER" to "ফুটার"
  )

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header & Action
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "কাস্টম এড ও স্পন্সরশিপ ম্যানেজার",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
          )
          Text(
            text = "টেলিগ্রাম বট, নিজস্ব ওয়েবসাইট ও ক্লায়েন্ট বিজ্ঞাপন নিয়ন্ত্রণ",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
          )
        }
        Button(
          onClick = { showCreateDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("নতুন এড")
        }
      }
    }

    // Stats
    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
          title = "মোট বিজ্ঞাপন",
          value = "${ads.size}টি",
          icon = Icons.Default.Campaign,
          iconColor = BluePrimary,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "সক্রিয় বিজ্ঞাপন",
          value = "${ads.count { it.isActive }}টি",
          icon = Icons.Default.CheckCircle,
          iconColor = EmeraldGreen,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "মোট ক্লিক",
          value = "${ads.sumOf { it.clickCount }} বার",
          icon = Icons.Default.TouchApp,
          iconColor = AmberGold,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Sponsor Inquiry Contact Configuration
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "📢 'Advertise with Us' যোগাযোগ বাটন কনফিগারেশন",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
          )
          Text(
            text = "ক্লায়েন্টরা এই WhatsApp ও Email এ ক্লিক করে সরাসরি আপনার সাথে বিজ্ঞাপনের জন্য যোগাযোগ করবে:",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = contactWp,
            onValueChange = { contactWp = it },
            label = { Text("WhatsApp নম্বর (কান্ট্রি কোড সহ, যেমন: 8801712345678)") },
            leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = EmeraldGreenDark) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = contactEmail,
            onValueChange = { contactEmail = it },
            label = { Text("বিজ্ঞাপন সংক্রান্ত সাপোর্ট ইমেইল") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BluePrimary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              val current = settings ?: SiteSettingsEntity()
              viewModel.adminUpdateSettings(
                current.copy(
                  adContactWhatsapp = contactWp.trim(),
                  adContactEmail = contactEmail.trim()
                )
              )
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
          ) {
            Text("যোগাযোগ তথ্য সংরক্ষণ করুন")
          }
        }
      }
    }

    // Ads List Section
    item {
      Text(
        text = "বিজ্ঞাপন তালিকা ও ১-ক্লিক টগল কন্ট্রোল (${ads.size}টি)",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
      )
    }

    if (ads.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.Campaign,
          title = "কোনো বিজ্ঞাপন যুক্ত করা নেই",
          message = "উপরে 'নতুন এড' বাটনে ক্লিক করে টেলিগ্রাম বট বা ওয়েবসাইটের ব্যানার যুক্ত করুন।"
        )
      }
    } else {
      items(ads) { ad ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (ad.isActive) SurfaceCard else Color(0xFFF8FAFC)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = if (ad.isActive) 2.dp else 0.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (ad.isActive) BluePrimary.copy(alpha = 0.3f) else CardBorder)
          )
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (ad.isActive) EmeraldGreenBg else RedBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = if (ad.isActive) "সক্রিয় (চালু)" else "বন্ধ (নিষ্ক্রিয়)",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = if (ad.isActive) EmeraldGreenDark else RedError,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(BlueLightBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  val posLabel = positions.find { it.first == ad.position }?.second ?: ad.position
                  Text(
                    text = "অবস্থান: $posLabel",
                    style = MaterialTheme.typography.labelSmall.copy(color = BluePrimaryDark)
                  )
                }
              }

              // 1-Click Toggle Switch
              Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                  checked = ad.isActive,
                  onCheckedChange = { viewModel.adminToggleAd(ad.id, it) }
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = ad.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )

            if (ad.subtitle.isNotBlank()) {
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = ad.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateBackground)
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Link, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = ad.targetUrl,
                style = MaterialTheme.typography.labelSmall.copy(color = BluePrimaryDark, fontWeight = FontWeight.Medium),
                maxLines = 1,
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "🔥 মোট ক্লিক: ${ad.clickCount} বার",
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontWeight = FontWeight.Bold)
              )

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                  onClick = { viewModel.openAdTarget(context, ad) },
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("লিংক টেস্ট", fontSize = 11.sp)
                }

                IconButton(
                  onClick = { viewModel.adminDeleteAd(ad.id) },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(Icons.Default.Delete, contentDescription = "মুছে ফেলুন", tint = RedError, modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }
      }
    }
  }

  // Create Ad Dialog
  if (showCreateDialog) {
    AlertDialog(
      onDismissRequest = { showCreateDialog = false },
      title = { Text("নতুন প্রমোশনাল এড / ব্যানার যুক্ত করুন", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = adTitle,
            onValueChange = { adTitle = it },
            label = { Text("বিজ্ঞাপনের শিরোনাম (যেমন: টেলিগ্রাম বট)") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = adSubtitle,
            onValueChange = { adSubtitle = it },
            label = { Text("সংক্ষিপ্ত বিবরণ / অফার বার্তা") },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = adTargetUrl,
            onValueChange = { adTargetUrl = it },
            label = { Text("টার্গেট লিংক (URL / Telegram Link)") },
            placeholder = { Text("https://t.me/yourbot") },
            modifier = Modifier.fillMaxWidth()
          )

          Text("প্রদর্শনের স্থান (Display Position):", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            positions.forEach { (posKey, posName) ->
              FilterChip(
                selected = adPosition == posKey,
                onClick = { adPosition = posKey },
                label = { Text(posName, fontSize = 11.sp) }
              )
            }
          }

          OutlinedTextField(
            value = adButtonText,
            onValueChange = { adButtonText = it },
            label = { Text("বাটন টেক্সট (যেমন: জয়েন করুন / ভিজিট)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (adTitle.isNotBlank() && adTargetUrl.isNotBlank()) {
              viewModel.adminCreateAd(
                AdvertisementEntity(
                  title = adTitle.trim(),
                  subtitle = adSubtitle.trim(),
                  targetUrl = adTargetUrl.trim(),
                  position = adPosition,
                  buttonText = adButtonText.ifBlank { "ভিজিট করুন" }
                )
              )
              showCreateDialog = false
              adTitle = ""
              adSubtitle = ""
            } else {
              viewModel.showMessage("শিরোনাম ও সঠিক লিংক আবশ্যক।")
            }
          }
        ) {
          Text("বিজ্ঞাপন চালু করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateDialog = false }) { Text("বাতিল") }
      }
    )
  }
}

@Composable
fun AdminGmailReviewsTab(viewModel: KajSetuViewModel) {
  val pendingGmails by viewModel.pendingGmailSubmissions.collectAsState()
  val allGmails by viewModel.allGmailSubmissions.collectAsState()
  val context = androidx.compose.ui.platform.LocalContext.current

  var showRejectDialog by remember { mutableStateOf<GmailSubmissionEntity?>(null) }
  var rejectReason by remember { mutableStateOf("পাসওয়ার্ড সঠিক নয় অথবা টু-স্টেপ ভেরিফিকেশন অন করা রয়েছে।") }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "জিমেইল বিক্রয় রিভিউ ও অনুমোদন",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
          )
          Text(
            text = "ইউজারদের জমা দেওয়া ফ্রেশ জিমেইল লগইন চেক করে ওয়ালেটে ৳১৫ ক্রেডিট করুন",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
          )
        }
      }
    }

    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
          title = "পেন্ডিং রিভিউ",
          value = "${pendingGmails.size}টি",
          icon = Icons.Default.HourglassTop,
          iconColor = AmberGold,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "অনুমোদিত",
          value = "${allGmails.count { it.status == "APPROVED" }}টি",
          icon = Icons.Default.CheckCircle,
          iconColor = EmeraldGreen,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "মোট জিমেইল",
          value = "${allGmails.size}টি",
          icon = Icons.Default.Mail,
          iconColor = BluePrimary,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Text(
        text = "পেন্ডিং জিমেইল আবেদন (${pendingGmails.size}টি)",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
      )
    }

    if (pendingGmails.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.CheckCircleOutline,
          title = "কোনো পেন্ডিং জিমেইল নেই",
          message = "সকল জিমেইল আবেদন পর্যালোচনা সম্পন্ন হয়েছে।"
        )
      }
    } else {
      items(pendingGmails) { g ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "ইউজার: @${g.username}",
                  style = MaterialTheme.typography.labelMedium.copy(color = BluePrimary, fontWeight = FontWeight.Bold)
                )
                Text(
                  text = formatDateTime(g.createdAt),
                  style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(EmeraldGreenBg)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("রিওয়ার্ড: ৳${g.pricePerMail}", color = EmeraldGreenDark, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gmail ID with Copy
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
                Text(g.gmailAddress, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
              }
              IconButton(onClick = {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Gmail", g.gmailAddress))
              }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "কপি", tint = TextMuted, modifier = Modifier.size(16.dp))
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Password with Copy
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
                Icon(Icons.Default.Key, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("পাসওয়ার্ড: ${g.password}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
              }
              IconButton(onClick = {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Password", g.password))
              }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "কপি", tint = TextMuted, modifier = Modifier.size(16.dp))
              }
            }

            if (g.recoveryEmail.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text("রিকভারি ইমেইল: ${g.recoveryEmail}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }

            if (g.notes.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text("নোট: ${g.notes}", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = { viewModel.adminApproveGmail(g.id) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("অনুমোদন (৳${g.pricePerMail})")
              }

              OutlinedButton(
                onClick = { showRejectDialog = g },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedError),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("বাতিল করুন")
              }
            }
          }
        }
      }
    }
  }

  // Reject Dialog
  showRejectDialog?.let { target ->
    AlertDialog(
      onDismissRequest = { showRejectDialog = null },
      title = { Text("জিমেইল আবেদন বাতিল করুন") },
      text = {
        Column {
          Text("জিমেইল: ${target.gmailAddress}", fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = rejectReason,
            onValueChange = { rejectReason = it },
            label = { Text("বাতিলের কারণ") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.adminRejectGmail(target.id, rejectReason)
            showRejectDialog = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = RedError)
        ) {
          Text("বাতিল নিশ্চিত করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showRejectDialog = null }) { Text("বন্ধ করুন") }
      }
    )
  }
}

@Composable
fun AdminRedeemCodeTab(viewModel: KajSetuViewModel) {
  val redeemCodes by viewModel.allRedeemCodes.collectAsState()
  val siteSettings by viewModel.siteSettings.collectAsState()

  var codeInput by remember { mutableStateOf("") }
  var amountInput by remember { mutableStateOf("") }
  var maxUsesInput by remember { mutableStateOf("50") }
  var isCreating by remember { mutableStateOf(false) }

  var telegramUrlInput by remember(siteSettings) { mutableStateOf(siteSettings?.telegramChannelUrl ?: "https://t.me/KajSetuOfficial") }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header & Telegram Channel Setting Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Send, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "টেলিগ্রাম চ্যানেল লিংক কনফিগারেশন",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )
              Text(
                text = "ইউজার অ্যাপের রিডিম পেজের টেলিগ্রাম বাটনে এই লিংকটি ডাইনামিক্যালি আপডেট হবে।",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
              )
            }
          }

          OutlinedTextField(
            value = telegramUrlInput,
            onValueChange = { telegramUrlInput = it },
            label = { Text("টেলিগ্রাম চ্যানেল ইউআরএল (Telegram URL)") },
            placeholder = { Text("https://t.me/KajSetuOfficial") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          Button(
            onClick = {
              val current = siteSettings ?: com.example.data.entity.SiteSettingsEntity()
              val updated = current.copy(
                telegramChannelUrl = telegramUrlInput.ifBlank { "https://t.me/KajSetuOfficial" }
              )
              viewModel.adminUpdateSettings(updated)
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            modifier = Modifier.align(Alignment.End)
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("টেলিগ্রাম লিংক সেভ করুন")
          }
        }
      }
    }

    // 2. Create New Redeem Code Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "নতুন রিডিম কোড তৈরি করুন (Create Redeem Code)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )
              Text(
                text = "ইউজাররা এই কোড রিডিম করে সাথে সাথে ওয়ালেটে বোনাস গ্রহণ করতে পারবে।",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
              )
            }
          }

          OutlinedTextField(
            value = codeInput,
            onValueChange = { codeInput = it.uppercase().trim() },
            label = { Text("রিডিম কোড নাম (e.g. KAJSETU50, BONUS20)") },
            placeholder = { Text("যেমন: BONUS2026") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = amountInput,
              onValueChange = { amountInput = it },
              label = { Text("বোনাস টাকা (BDT)") },
              placeholder = { Text("যেমন: 20") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
              value = maxUsesInput,
              onValueChange = { maxUsesInput = it },
              label = { Text("ম্যাক্স ইউজার সংখ্যা") },
              placeholder = { Text("যেমন: 50") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            )
          }

          Button(
            onClick = {
              val amt = amountInput.toDoubleOrNull() ?: return@Button
              val maxU = maxUsesInput.toIntOrNull() ?: return@Button
              if (codeInput.isBlank() || amt <= 0.0 || maxU <= 0) return@Button

              isCreating = true
              viewModel.adminCreateRedeemCode(
                code = codeInput,
                bonusAmount = amt,
                maxUses = maxU,
                onSuccess = {
                  isCreating = false
                  codeInput = ""
                  amountInput = ""
                  maxUsesInput = "50"
                }
              )
            },
            enabled = codeInput.isNotBlank() && amountInput.isNotBlank() && maxUsesInput.isNotBlank() && !isCreating,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            modifier = Modifier.fillMaxWidth().height(48.dp)
          ) {
            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("কোড প্রকাশ করুন (Publish Code)", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 3. Active Codes List Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "তৈরি করা কোডের তালিকা (Active & Managed Codes: ${redeemCodes.size}টি)",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
      }
    }

    if (redeemCodes.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("কোনো রিডিম কোড পাওয়া যায়নি। নতুন কোড তৈরি করুন।", color = TextMuted)
          }
        }
      }
    } else {
      items(redeemCodes) { rc ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = rc.code,
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = BluePrimary
                    )
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (rc.isActive && rc.usedCount < rc.maxUses) EmeraldGreenBg else RedBg
                  ) {
                    Text(
                      text = if (!rc.isActive) "নিষ্ক্রিয় (Inactive)" else if (rc.usedCount >= rc.maxUses) "লিমিট শেষ (Full)" else "সক্রিয় (Active)",
                      color = if (!rc.isActive) RedError else if (rc.usedCount >= rc.maxUses) RedError else EmeraldGreenDark,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "বোনাস: ৳${String.format("%.2f", rc.bonusAmount)} | ব্যবহার: ${rc.usedCount}/${rc.maxUses} জন",
                  style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontWeight = FontWeight.Medium)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Active/Inactive Toggle & Delete
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Active / Inactive Toggle Button
              OutlinedButton(
                onClick = {
                  viewModel.adminToggleRedeemCode(rc.id, !rc.isActive)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = if (rc.isActive) AmberGold else EmeraldGreenDark
                )
              ) {
                Icon(
                  if (rc.isActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (rc.isActive) "নিষ্ক্রিয় করুন" else "সক্রিয় করুন",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              // Delete Button
              Button(
                onClick = {
                  viewModel.adminDeleteRedeemCode(rc.id)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedError)
              ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("মুছুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

