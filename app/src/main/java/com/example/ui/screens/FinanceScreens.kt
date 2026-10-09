package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TransactionEntity
import com.example.ui.KajSetuViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun WalletScreen(
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit,
  onBack: () -> Unit
) {
  val wallet by viewModel.currentWallet.collectAsState()
  val userSubmissions by viewModel.userSubmissions.collectAsState()
  val transactions by viewModel.userTransactions.collectAsState()
  var filterType by remember { mutableStateOf("ALL") }

  val approvedTaskSubmissions = remember(userSubmissions) {
    userSubmissions.filter { it.status == "APPROVED" }
  }
  val completedTasksCount = approvedTaskSubmissions.size
  val totalEarnedFromTasks = wallet?.taskEarnings ?: 0.0

  val filteredTx = when (filterType) {
    "DEPOSIT" -> transactions.filter { it.type == "DEPOSIT" }
    "TASK" -> transactions.filter { it.type == "TASK_REWARD" }
    "REFERRAL" -> transactions.filter { it.type == "REFERRAL_REWARD" }
    "WITHDRAWAL" -> transactions.filter { it.type == "WITHDRAWAL" }
    else -> transactions
  }

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "ওয়ালেট ও আয় ব্যালেন্স",
        subtitle = "রুম ডাটাবেজ দ্বারা সংরক্ষিত হিসাব",
        showBack = true,
        onBackClick = onBack
      )
    },
    bottomBar = {
      UserBottomNav(currentScreen = Screen.Wallet, onNavigate = onNavigate)
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
      // 1. Wallet Balance Hero Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("উপলব্ধ ওয়ালেট ব্যালেন্স", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = BlueLightBg
              ) {
                Text(
                  text = "Room DB Synced",
                  color = BluePrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = formatBdt(wallet?.balance ?: 0.0),
              style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = { onNavigate(Screen.Deposit) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier.weight(1f).height(44.dp)
              ) {
                Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ডিপোজিট", fontWeight = FontWeight.Bold)
              }
              Button(
                onClick = { onNavigate(Screen.Withdraw) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1f).height(44.dp)
              ) {
                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("উইথড্র", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 2. FEATURED: Total Earned Income from Completed Tasks Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldGreen)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreenBg),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.TaskAlt,
                    contentDescription = null,
                    tint = EmeraldGreenDark,
                    modifier = Modifier.size(26.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "টাস্ক থেকে মোট অর্জিত আয়",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = TextPrimary
                    )
                  )
                  Text(
                    text = "Total Earned Income from Completed Tasks",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldGreenBg
              ) {
                Text(
                  text = "$completedTasksCount টি সম্পন্ন",
                  color = EmeraldGreenDark,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "সর্বমোট টাস্ক ইনকাম",
                  style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                )
                Text(
                  text = formatBdt(totalEarnedFromTasks),
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreenDark
                  )
                )
              }

              Button(
                onClick = { onNavigate(Screen.MyTasks) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("কাজের তালিকা", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 3. Earnings & Balance Breakdown Grid
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Share, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("রেফারেল আয়", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = formatBdt(wallet?.referralEarnings ?: 0.0),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )
            }
          }

          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("মোট উত্তোলন", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = formatBdt(wallet?.totalWithdrawal ?: 0.0),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )
            }
          }
        }
      }

      // Filter Chips
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = filterType == "ALL",
            onClick = { filterType = "ALL" },
            label = { Text("সব") },
            shape = RoundedCornerShape(20.dp)
          )
          FilterChip(
            selected = filterType == "DEPOSIT",
            onClick = { filterType = "DEPOSIT" },
            label = { Text("ডিপোজিট") },
            shape = RoundedCornerShape(20.dp)
          )
          FilterChip(
            selected = filterType == "TASK",
            onClick = { filterType = "TASK" },
            label = { Text("টাস্ক রিওয়ার্ড") },
            shape = RoundedCornerShape(20.dp)
          )
          FilterChip(
            selected = filterType == "WITHDRAWAL",
            onClick = { filterType = "WITHDRAWAL" },
            label = { Text("উইথড্র") },
            shape = RoundedCornerShape(20.dp)
          )
        }
      }

      // Ledger Transactions List
      if (filteredTx.isEmpty()) {
        item {
          EmptyStateView(
            icon = Icons.Default.ReceiptLong,
            title = "কোনো লেনদেন রেকর্ড নেই",
            message = "এই ক্যাটাগরিতে এখনো কোনো ট্রানজেকশন তৈরি হয়নি।"
          )
        }
      } else {
        items(filteredTx) { tx ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                StatusPill(status = tx.status)
                val isPos = tx.type in listOf("DEPOSIT", "TASK_REWARD", "REFERRAL_REWARD", "ADJUSTMENT")
                Text(
                  text = (if (isPos) "+ " else "- ") + formatBdt(tx.amount),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isPos) EmeraldGreenDark else RedError
                  )
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = tx.description,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )

              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "ব্যালেন্স: ${formatBdt(tx.balanceBefore)} ➔ ${formatBdt(tx.balanceAfter)}",
                  style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
                Text(
                  text = formatDateTime(tx.createdAt),
                  style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun DepositScreen(
  viewModel: KajSetuViewModel,
  onNavigate: ((Screen) -> Unit)? = null,
  onBack: () -> Unit
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val settings by viewModel.siteSettings.collectAsState()
  var selectedMethod by remember { mutableStateOf("BKASH") }
  var amountText by remember { mutableStateOf("500") }
  var senderNumber by remember { mutableStateOf("") }
  var trxId by remember { mutableStateOf("") }

  val targetNumber = when (selectedMethod) {
    "BKASH" -> settings?.bkashNumber ?: "01712-345678"
    "NAGAD" -> settings?.nagadNumber ?: "01912-345678"
    "ROCKET" -> settings?.rocketNumber ?: "01812-345678"
    else -> settings?.bankDetails ?: "ব্যাংক একাউন্ট"
  }

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "ডিপোজিট করুন",
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
      // Tutorial Video Banner
      item {
        ScreenTutorialBanner(
          title = "বিকাশ ও নগদে ডিপোজিট করার ভিডিও নিয়ম",
          subtitle = "কীভাবে টাকা পাঠিয়ে TrxID দিয়ে ডিপোজিট করবেন দেখুন",
          onWatchVideo = {
            viewModel.openExternalUrl(
              context,
              "https://www.youtube.com/results?search_query=how+to+send+money+bkash+nagad"
            )
          },
          onViewAllTutorials = { onNavigate?.invoke(Screen.Tutorials) }
        )
      }

      // Method Selector
      item {
        Text("পেমেন্ট মাধ্যম নির্বাচন করুন:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          MethodButton("বিকাশ", BkashPink, selectedMethod == "BKASH") { selectedMethod = "BKASH" }
          MethodButton("নগদ", NagadOrange, selectedMethod == "NAGAD") { selectedMethod = "NAGAD" }
          MethodButton("রকেট", RocketPurple, selectedMethod == "ROCKET") { selectedMethod = "ROCKET" }
          MethodButton("ব্যাংক", BankNavy, selectedMethod == "BANK") { selectedMethod = "BANK" }
        }
      }

      // Instructions Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = BlueLightBg),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "📌 $selectedMethod টাকা পাঠানোর নির্দেশ:",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "১. আপনার $selectedMethod অ্যাপ থেকে Send Money / Transfer অপশনে যান।\n" +
                  "২. এই নম্বরে টাকা পাঠান: $targetNumber\n" +
                  "৩. লেনদেন সফল হলে প্রাপ্ত ট্রানজেকশন আইডি (TrxID) সংরক্ষণ করুন।\n" +
                  "৪. নিচে আপনার প্রেরক নম্বর ও TrxID লিখে সাবমিট করুন।",
              style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 20.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "সীমা: সর্বনিম্ন ৳${settings?.minDeposit ?: 100.0} | সর্বোচ্চ ৳${settings?.maxDeposit ?: 25000.0}",
              style = MaterialTheme.typography.labelSmall.copy(color = BluePrimaryDark, fontWeight = FontWeight.Bold)
            )
          }
        }
      }

      // Deposit Form Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            OutlinedTextField(
              value = amountText,
              onValueChange = { amountText = it },
              label = { Text("ডিপোজিট পরিমাণ (টাকা)") },
              leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
              modifier = Modifier.fillMaxWidth().testTag("deposit_amount_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = senderNumber,
              onValueChange = { senderNumber = it },
              label = { Text("আপনার $selectedMethod নম্বর (১১ ডিজিট)") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("deposit_sender_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = trxId,
              onValueChange = { trxId = it },
              label = { Text("ট্রানজেকশন আইডি (TrxID)") },
              leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("deposit_trx_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = {
                val amt = amountText.toDoubleOrNull() ?: 0.0
                viewModel.requestDeposit(selectedMethod, senderNumber, trxId, amt)
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_deposit_button")
            ) {
              Text("ডিপোজিট আবেদন জমা দিন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun WithdrawScreen(
  viewModel: KajSetuViewModel,
  onNavigate: ((Screen) -> Unit)? = null,
  onBack: () -> Unit
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val wallet by viewModel.currentWallet.collectAsState()
  val settings by viewModel.siteSettings.collectAsState()
  val user by viewModel.currentUser.collectAsState()

  var selectedMethod by remember { mutableStateOf("BKASH") }
  var amountText by remember { mutableStateOf("300") }
  var receiverNumber by remember { mutableStateOf("") }

  val amount = amountText.toDoubleOrNull() ?: 0.0
  val feePercent = settings?.withdrawalFeePercent ?: 2.0
  val fee = (amount * feePercent) / 100.0
  val netAmount = (amount - fee).coerceAtLeast(0.0)

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "আয় উত্তোলন (উইথড্র)",
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
      // Tutorial Video Banner
      item {
        ScreenTutorialBanner(
          title = "ইনকামের টাকা উইথড্র করার ভিডিও টিউটোরিয়াল",
          subtitle = "বিকাশ ও নগদে টাকা তোলার সম্পূর্ণ নিয়ম দেখুন",
          onWatchVideo = {
            viewModel.openExternalUrl(
              context,
              "https://www.youtube.com/results?search_query=how+to+withdraw+money+to+bkash+nagad"
            )
          },
          onViewAllTutorials = { onNavigate?.invoke(Screen.Tutorials) }
        )
      }

      // Inactive warning
      if (user?.accountStatus != "active") {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AmberGoldBg),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = AmberGold)
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "উইথড্র করতে আপনার অ্যাকাউন্ট অবশ্যই ভেরিফাইড ও সক্রিয় হতে হবে।",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }

      // Balance Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("উত্তোলনযোগ্য ব্যালেন্স", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
              Text(
                formatBdt(wallet?.balance ?: 0.0),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
              )
            }
            Box(
              modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(EmeraldGreenBg).padding(8.dp)
            ) {
              Text("সার্ভিস ফি: $feePercent%", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreenDark))
            }
          }
        }
      }

      // Method Selector
      item {
        Text("উত্তোলনের মাধ্যম নির্বাচন করুন:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          MethodButton("বিকাশ", BkashPink, selectedMethod == "BKASH") { selectedMethod = "BKASH" }
          MethodButton("নগদ", NagadOrange, selectedMethod == "NAGAD") { selectedMethod = "NAGAD" }
          MethodButton("রকেট", RocketPurple, selectedMethod == "ROCKET") { selectedMethod = "ROCKET" }
          MethodButton("ব্যাংক", BankNavy, selectedMethod == "BANK") { selectedMethod = "BANK" }
        }
      }

      // Form
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            OutlinedTextField(
              value = amountText,
              onValueChange = { amountText = it },
              label = { Text("উইথড্র পরিমাণ (টাকা)") },
              leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
              modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = receiverNumber,
              onValueChange = { receiverNumber = it },
              label = { Text("আপনার $selectedMethod নম্বর (টাকা গ্রহণের জন্য)") },
              leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("withdraw_receiver_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown
            Box(
              modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(SlateBackground).padding(12.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("মোট উইথড্র:", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                  Text(formatBdt(amount), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("সার্ভিস চার্জ ($feePercent%):", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                  Text("- " + formatBdt(fee), style = MaterialTheme.typography.bodySmall.copy(color = RedError))
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp), color = CardBorder)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("আপনি নেট পাবেন:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                  Text(formatBdt(netAmount), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark))
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = {
                viewModel.requestWithdrawal(selectedMethod, receiverNumber, amount)
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_withdraw_button")
            ) {
              Text("উইথড্র রিকোয়েস্ট পাঠান", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun ActivationScreen(
  viewModel: KajSetuViewModel,
  onBack: () -> Unit
) {
  val settings by viewModel.siteSettings.collectAsState()
  val user by viewModel.currentUser.collectAsState()
  val userRequests by viewModel.pendingActivations.collectAsState()

  var selectedMethod by remember { mutableStateOf("BKASH") }
  var senderNumber by remember { mutableStateOf("") }
  var trxId by remember { mutableStateOf("") }

  val targetNumber = when (selectedMethod) {
    "BKASH" -> settings?.bkashNumber ?: "01712-345678"
    "NAGAD" -> settings?.nagadNumber ?: "01912-345678"
    else -> settings?.rocketNumber ?: "01812-345678"
  }

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "অ্যাকাউন্ট ভেরিফিকেশন ও অ্যাক্টিভেশন",
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
      // 0. Current Status Card
      item {
        val isActive = user?.accountStatus == "active"
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = if (isActive) EmeraldGreenBg else AmberGoldBg),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isActive) EmeraldGreen else AmberGold)
          )
        ) {
          Row(
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (isActive) EmeraldGreen else AmberGold),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isActive) Icons.Default.Verified else Icons.Default.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = if (isActive) "আপনার অ্যাকাউন্ট সক্রিয় ও ভেরিফাইড!" else "আপনার অ্যাকাউন্ট এখনো নিষ্ক্রিয়",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isActive) EmeraldGreenDark else Color(0xFF92400E)
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = if (isActive)
                  "সকল কাজ, জিমেইল বিক্রয় ও আনলিমিটেড উইথড্র সুবিধা আপনার জন্য উন্মুক্ত।"
                else
                  "নিচের পেমেন্ট তথ্য অনুযায়ী ফি পরিশোধ করে রিকোয়েস্ট পাঠান।",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = if (isActive) EmeraldGreenDark.copy(alpha = 0.9f) else Color(0xFFB45309)
                )
              )
            }
          }
        }
      }

      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "কেন অ্যাকাউন্ট সক্রিয় করা আবশ্যক?",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "১. বট এবং ভুয়া অ্যাকাউন্ট প্রতিরোধে এককালীন একাউন্ট ফি নেওয়া হয়।\n" +
                  "২. সক্রিয় একাউন্ট সরাসরি সকল প্রিমিয়াম মাইক্রো টাস্কে অংশগ্রহণ করতে পারে।\n" +
                  "৩. সীমাহীন আয় এবং দ্রুত বিকাশ/নগদে উত্তোলন সুবিধা আনলক হয়।\n" +
                  "৪. রেফারেল লিঙ্ক দিয়ে প্রতি বন্ধু যোগদানে আকর্ষণীয় বোনাস পাওয়া যায়।",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AmberGoldBg)
                .padding(12.dp)
            ) {
              Text(
                text = "এককালীন অ্যাক্টিভেশন ফি: " + formatBdt(settings?.activationFee ?: 100.0),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
              )
            }
          }
        }
      }

      // Method Selection
      item {
        Text("পেমেন্ট মাধ্যম বেছে নিন:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          MethodButton("বিকাশ", BkashPink, selectedMethod == "BKASH") { selectedMethod = "BKASH" }
          MethodButton("নগদ", NagadOrange, selectedMethod == "NAGAD") { selectedMethod = "NAGAD" }
          MethodButton("রকেট", RocketPurple, selectedMethod == "ROCKET") { selectedMethod = "ROCKET" }
        }
      }

      // Send Money Guide
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = BlueLightBg)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Send Money করুন: $targetNumber ($selectedMethod)",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
            )
            Text(
              text = "পরিমাণ: ${formatBdt(settings?.activationFee ?: 100.0)}। টাকা পাঠানোর পর ট্রানজেকশন আইডি নিচে লিখে সাবমিট করুন।",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
          }
        }
      }

      // Verification Form
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            OutlinedTextField(
              value = senderNumber,
              onValueChange = { senderNumber = it },
              label = { Text("প্রেরক $selectedMethod নম্বর (১১ ডিজিট)") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("activation_sender_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = trxId,
              onValueChange = { trxId = it },
              label = { Text("ট্রানজেকশন আইডি (TrxID)") },
              leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("activation_trx_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = {
                viewModel.submitActivation(selectedMethod, senderNumber, trxId)
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_activation_button")
            ) {
              Text("অ্যাক্টিভেশন সাবমিট করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun RowScope.MethodButton(
  name: String,
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = if (isSelected) color else color.copy(alpha = 0.1f),
      contentColor = if (isSelected) Color.White else color
    ),
    border = if (!isSelected) ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(color.copy(alpha = 0.3f))) else null,
    modifier = Modifier.weight(1f).height(44.dp)
  ) {
    Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
  }
}
