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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KajSetuViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ReferralScreen(
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit,
  onBack: () -> Unit
) {
  val user by viewModel.currentUser.collectAsState()
  val wallet by viewModel.currentWallet.collectAsState()
  val settings by viewModel.siteSettings.collectAsState()
  val referrals by viewModel.referrals.collectAsState()
  val context = LocalContext.current

  val refCode = user?.referralCode ?: "KAJSETU101"
  val refLink = "https://kajsetu.com/register?ref=$refCode"

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "রেফারেল ও ইনভাইট প্রোগ্রাম",
        showBack = true,
        onBackClick = onBack
      )
    },
    bottomBar = {
      UserBottomNav(currentScreen = Screen.Referral, onNavigate = onNavigate)
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
      // Hero Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(AmberGoldBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = AmberGold, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "বন্ধু ইনভাইট করে প্রতি রেফারে ৳${settings?.referralReward ?: 25.0} আয় করুন!",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
              )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "আপনার আমন্ত্রিত বন্ধু অ্যাকাউন্ট খুলে সক্রিয় করলেই সরাসরি আপনার ওয়ালেটে রেফারেল বোনাস যোগ হবে।",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, textAlign = TextAlign.Center)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Code box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BlueLightBg)
                .border(1.dp, BluePrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("আপনার রেফারেল কোড", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                  Text(refCode, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark))
                }
                Button(
                  onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("KajSetu Referral", refCode))
                    viewModel.showMessage("রেফারেল কোড কপি করা হয়েছে!")
                  },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                  Text("কপি করুন")
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("KajSetu Referral Link", refLink))
                viewModel.showMessage("রেফারেল লিংক কপি করা হয়েছে!")
              },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("রেফারেল লিংক কপি করুন")
            }
          }
        }
      }

      // Stats
      item {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          StatCard(
            title = "মোট আমন্ত্রিত বন্ধু",
            value = "${referrals.size} জন",
            icon = Icons.Default.People,
            iconColor = BluePrimary,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "মোট রেফারেল আয়",
            value = formatBdt(wallet?.referralEarnings ?: 0.0),
            icon = Icons.Default.MonetizationOn,
            iconColor = EmeraldGreen,
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Referral List
      item {
        Text("আমন্ত্রিত বন্ধুদের তালিকা", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
      }

      if (referrals.isEmpty()) {
        item {
          EmptyStateView(
            icon = Icons.Default.GroupAdd,
            title = "এখনো কেউ যুক্ত হননি",
            message = "আপনার রেফারেল কোড বন্ধুদের সাথে শেয়ার করে আয় শুরু করুন।"
          )
        }
      } else {
        items(referrals) { refUser ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(refUser.fullName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(refUser.username, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
              }
              StatusPill(refUser.accountStatus)
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProfileScreen(
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit,
  onBack: () -> Unit
) {
  val user by viewModel.currentUser.collectAsState()

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "প্রোফাইল ও সেটিংস",
        showBack = true,
        onBackClick = onBack
      )
    },
    bottomBar = {
      UserBottomNav(currentScreen = Screen.Profile, onNavigate = onNavigate)
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
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(BlueLightBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(42.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = user?.fullName ?: "ইউজার",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Text(
              text = "@${user?.username ?: ""}",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatusPill(status = user?.accountStatus ?: "INACTIVE")
          }
        }
      }

      // Information Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileInfoRow(Icons.Default.Email, "ইমেইল", user?.email ?: "-")
            ProfileInfoRow(Icons.Default.Phone, "মোবাইল নম্বর", user?.phone ?: "-")
            ProfileInfoRow(Icons.Default.Share, "রেফারেল কোড", user?.referralCode ?: "-")
            ProfileInfoRow(Icons.Default.Shield, "রোল / পদবী", if (user?.role == "admin") "সিস্টেম অ্যাডমিন" else "সাধারণ ব্যবহারকারী")
          }
        }
      }

      // Action Buttons
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            ListItem(
              headlineContent = { Text("রিডিম কোড বোনাস নিন") },
              supportingContent = { Text("ফ্রি বোনাস কোড রিডিম করুন") },
              leadingContent = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = EmeraldGreen) },
              modifier = Modifier.clickable { onNavigate(Screen.RedeemBonus) }
            )
            ListItem(
              headlineContent = { Text("টিউটোরিয়াল ও ভিডিও গাইড") },
              supportingContent = { Text("কাজ, জিমেইল সেল, ডিপোজিট ও উইথড্র নিয়ম") },
              leadingContent = { Icon(Icons.Default.PlayCircle, contentDescription = null, tint = RedError) },
              modifier = Modifier.clickable { onNavigate(Screen.Tutorials) }
            )
            ListItem(
              headlineContent = { Text("সাহায্য ও সাপোর্ট টিকিট") },
              leadingContent = { Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = BluePrimary) },
              modifier = Modifier.clickable { onNavigate(Screen.Support) }
            )
            ListItem(
              headlineContent = { Text("প্ল্যাটফর্ম নীতিমালা ও শর্তাবলী") },
              leadingContent = { Icon(Icons.Default.Policy, contentDescription = null, tint = TextSecondary) },
              modifier = Modifier.clickable { onNavigate(Screen.Policies) }
            )
            if (user?.role == "admin") {
              ListItem(
                headlineContent = { Text("অ্যাডমিন ড্যাশবোর্ড ওপেন করুন", fontWeight = FontWeight.Bold, color = BluePrimaryDark) },
                leadingContent = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = BluePrimaryDark) },
                modifier = Modifier.clickable { onNavigate(Screen.AdminPortal) }
              )
            }
          }
        }
      }

      // Logout Button
      item {
        Button(
          onClick = { viewModel.logout() },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RedError),
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("profile_logout_button")
        ) {
          Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("লগআউট করুন", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(label, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
      Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = TextPrimary))
    }
  }
}

@Composable
fun MarketplaceScreen(
  viewModel: KajSetuViewModel,
  onBack: () -> Unit
) {
  val services by viewModel.marketplaceServices.collectAsState()

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "ডিজিটাল সার্ভিস মার্কেটপ্লেস",
        subtitle = "অনুমোদিত ও বিশ্বস্ত সেবা",
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
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = BlueLightBg)
        ) {
          Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = BluePrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "সকল সার্ভিস কাজসেতু কর্তৃক অনুমোদিত এবং প্ল্যাটফর্মের আইনি নীতিমালার সাথে সম্পূর্ণ সঙ্গতিপূর্ণ।",
              style = MaterialTheme.typography.bodySmall.copy(color = BluePrimaryDark)
            )
          }
        }
      }

      items(services) { svc ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(BlueLightBg).padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(svc.category, color = BluePrimaryDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              }
              Text(
                formatBdt(svc.price),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(svc.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
            Spacer(modifier = Modifier.height(6.dp))
            Text(svc.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("ডেলিভারি: ${svc.turnaroundTime}", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
              Button(
                onClick = { viewModel.showMessage("\"${svc.title}\" সার্ভিসের জন্য সাপোর্টে টিকেট তৈরি করুন।") },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
              ) {
                Text("অর্ডার করুন", fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun NotificationsScreen(
  viewModel: KajSetuViewModel,
  onBack: () -> Unit
) {
  val notifications by viewModel.userNotifications.collectAsState()

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "নোটিফিকেশন সেন্টার",
        showBack = true,
        onBackClick = onBack,
        actions = {
          TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
            Text("সব পঠিত করুন", color = BluePrimary, fontSize = 12.sp)
          }
        }
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(SlateBackground),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (notifications.isEmpty()) {
        item {
          EmptyStateView(
            icon = Icons.Default.NotificationsNone,
            title = "কোনো নোটিফিকেশন নেই",
            message = "আপনার অ্যাকাউন্টে নতুন কোনো আপডেট বা বার্তা নেই।"
          )
        }
      } else {
        items(notifications) { notif ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (!notif.isRead) BlueLightBg.copy(alpha = 0.5f) else SurfaceCard
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(BlueLightBg),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(notif.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Spacer(modifier = Modifier.height(4.dp))
                Text(notif.message, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                Spacer(modifier = Modifier.height(6.dp))
                Text(formatDateTime(notif.createdAt), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun SupportScreen(
  viewModel: KajSetuViewModel,
  onBack: () -> Unit
) {
  val tickets by viewModel.userTickets.collectAsState()
  var showNewTicketDialog by remember { mutableStateOf(false) }

  var subject by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("পেমেন্ট") }
  var message by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "হেল্প ও সাপোর্ট টিকিট",
        showBack = true,
        onBackClick = onBack
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showNewTicketDialog = true },
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text("নতুন টিকিট") },
        containerColor = BluePrimary,
        contentColor = Color.White
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(SlateBackground),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (tickets.isEmpty()) {
        item {
          EmptyStateView(
            icon = Icons.Default.SupportAgent,
            title = "কোনো টিকিট নেই",
            message = "আপনার কোনো অভিযোগ বা জিজ্ঞাসা থাকলে নিচের বাটনে ক্লিক করে টিকিট তৈরি করুন।"
          )
        }
      } else {
        items(tickets) { t ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                StatusPill(status = t.status)
                Text(t.category, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(t.subject, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
              Spacer(modifier = Modifier.height(4.dp))
              Text(t.message, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

              if (t.adminReply.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BlueLightBg)
                    .padding(10.dp)
                ) {
                  Column {
                    Text("অ্যাডমিনের উত্তর:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark))
                    Text(t.adminReply, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary))
                  }
                }
              }
            }
          }
        }
      }
    }

    if (showNewTicketDialog) {
      AlertDialog(
        onDismissRequest = { showNewTicketDialog = false },
        title = { Text("নতুন সাপোর্ট টিকিট") },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = subject,
              onValueChange = { subject = it },
              label = { Text("বিষয়") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = message,
              onValueChange = { message = it },
              label = { Text("বিস্তারিত বিবরণ লিখুন") },
              modifier = Modifier.fillMaxWidth().height(100.dp)
            )
          }
        },
        confirmButton = {
          Button(onClick = {
            viewModel.createSupportTicket(subject, category, message)
            showNewTicketDialog = false
            subject = ""
            message = ""
          }) {
            Text("জমা দিন")
          }
        },
        dismissButton = {
          TextButton(onClick = { showNewTicketDialog = false }) {
            Text("বাতিল")
          }
        }
      )
    }
  }
}

@Composable
fun PoliciesScreen(
  onBack: () -> Unit
) {
  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "নীতিমালা ও আইনি শর্তাবলী",
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
      item {
        PolicyCard(
          title = "শর্তাবলী ও গ্রহণযোগ্য ব্যবহার নীতি (Terms of Service)",
          content = "১. কাজসেতু একটি স্বাধীন বাংলাদেশি ডিজিটাল মাইক্রো-টাস্ক ও ফ্রিল্যান্স সার্ভিস সমন্বয়কারী প্ল্যাটফর্ম।\n" +
              "২. ব্যবহারকারীদের অবশ্যই সত্য ও সঠিক তথ্য দিয়ে অ্যাকাউন্ট খুলতে হবে।\n" +
              "৩. কোনো প্রকার স্প্যামিং, ভুয়া অ্যাকাউন্ট তৈরি, রোবট/বট ব্যবহার বা প্রতারণামূলক কাজ কঠোরভাবে নিষিদ্ধ।\n" +
              "৪. গুগল, মেটা বা কোনো তৃতীয় পক্ষের প্ল্যাটফর্ম পলিসি লঙ্ঘন করে এমন কোনো আনঅথোরাইজড কাজ নিষিদ্ধ।"
        )
      }

      item {
        PolicyCard(
          title = "গোপনীয়তা নীতি (Privacy Policy)",
          content = "আমরা আপনার নাম, ফোন নম্বর ও লেনদেন রেকর্ড সুরক্ষিত রাখি। ব্যবহারকারীর কোনো ব্যক্তিগত তথ্য অনুমতি ছাড়া তৃতীয় কোনো পক্ষের সাথে বাণিজ্যিকভাবে শেয়ার করা হয় না। সমস্ত পেমেন্ট ট্রানজেকশন রেকর্ড সর্বোচ্চ নিরাপত্তার সাথে সংরক্ষিত থাকে।"
        )
      }

      item {
        PolicyCard(
          title = "রিফান্ড ও পেমেন্ট নীতিমালা (Refund & Payment)",
          content = "১. ভুলবশত কোনো বাতিলকৃত উইথড্র রিকোয়েস্টের টাকা তাৎক্ষণিকভাবে ব্যবহারকারীর মূল ওয়ালেট ব্যালেন্সে ফেরত দেওয়া হয়।\n" +
              "২. যথাযথ প্রমাণ সাপেক্ষে অনুমোদিত টাস্কের রিওয়ার্ড অপরিবর্তনীয় এবং সরাসরি ওয়ালেটে জমা হয়।"
        )
      }
    }
  }
}

@Composable
fun PolicyCard(title: String, content: String) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark))
      Spacer(modifier = Modifier.height(8.dp))
      Text(content, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp))
    }
  }
}
