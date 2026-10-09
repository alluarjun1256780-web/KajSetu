package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KajSetuViewModel
import com.example.ui.Screen
import com.example.ui.components.KajSetuTopBar
import com.example.ui.theme.*

data class TutorialItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val category: String, // "GMAIL", "DEPOSIT", "WITHDRAW", "TASKS", "ACTIVATION", "REDEEM"
  val duration: String,
  val icon: ImageVector,
  val videoUrl: String,
  val steps: List<String>
)

val AllTutorials = listOf(
  TutorialItem(
    id = "tut_gmail_sell",
    title = "জিমেইল বিক্রি করে প্রতিদিন আয় করার নিয়ম",
    subtitle = "মোবাইল দিয়ে ফোন নম্বর ছাড়া আনলিমিটেড জিমেইল একাউন্ট তৈরি ও বিক্রি করুন",
    category = "GMAIL",
    duration = "০৪:৩০ মিনিট",
    icon = Icons.Default.Mail,
    videoUrl = "https://www.youtube.com/results?search_query=how+to+create+unlimited+gmail+without+phone+number",
    steps = listOf(
      "গুগল ক্রোম বা জিমেইল অ্যাপের ইনকগনিটো মোড (Incognito) ওপেন করুন।",
      "নতুন গুগল অ্যাকাউন্ট তৈরিতে ক্লিক করে নাম ও জন্মতারিখ দিন।",
      "ফোন নম্বর ছাড়া স্কিপ (Skip) অপশন নির্বাচন করুন।",
      "অ্যাকাউন্টের রিকভারি ইমেইল হিসেবে 'kajsetu.recovery@gmail.com' যুক্ত করুন।",
      "পাসওয়ার্ড দিন (যেমন: KajSetu@2026) এবং ২-স্টেপ ভেরিফিকেশন বন্ধ রাখুন।",
      "কাজসেতু অ্যাপের 'জিমেইল সেল' মেন্যুতে এসে ইমেইল ও পাসওয়ার্ড জমা দিন।",
      "অ্যাডমিন চেক করে ১০-৩০ মিনিটের মধ্যে প্রতি জিমেইলে ৳১৫ আপনার ওয়ালেটে দিয়ে দিবে।"
    )
  ),
  TutorialItem(
    id = "tut_recovery_email",
    title = "জিমেইলে রিকভারি ইমেইল যোগ করার নিয়ম",
    subtitle = "রিকভারি ইমেইল ছাড়া জিমেইল জমা দিলে অ্যাডমিন বাতিল করবে। দেখুন সঠিক পদ্ধতি",
    category = "GMAIL",
    duration = "০২:১৫ মিনিট",
    icon = Icons.Default.MarkEmailRead,
    videoUrl = "https://www.youtube.com/results?search_query=how+to+add+recovery+email+in+gmail+bangla",
    steps = listOf(
      "আপনার ফোনের Google Settings অথবা Gmail App প্রোফাইল আইকনে ট্যাপ করুন।",
      "'Manage your Google Account' অপশনে প্রবেশ করুন।",
      "Security (নিরাপত্তা) ট্যাবে যান।",
      "স্ক্রোল করে 'Recovery email' অপশনটিতে ক্লিক করুন।",
      "রিকভারি ইমেইল বক্সে 'kajsetu.recovery@gmail.com' বসিয়ে Save করুন।",
      "সফলভাবে সেভ হলে ফোন নম্বর ছাড়াই জিমেইলটি কাজসেতুতে বিক্রির জন্য রেডি।"
    )
  ),
  TutorialItem(
    id = "tut_deposit",
    title = "বিকাশ ও নগদ দিয়ে ওয়ালেটে ডিপোজিট করার উপায়",
    subtitle = "টাকা জমা দিয়ে বিজ্ঞাপন দিন অথবা একাউন্ট একটিভ করুন খুব সহজে",
    category = "DEPOSIT",
    duration = "০৩:১০ মিনিট",
    icon = Icons.Default.AccountBalanceWallet,
    videoUrl = "https://www.youtube.com/results?search_query=how+to+send+money+bkash+nagad",
    steps = listOf(
      "কাজসেতু অ্যাপের 'ডিপোজিট' বাটনে ক্লিক করুন।",
      "আপনার পছন্দমতো পেমেন্ট মাধ্যম নির্বাচন করুন (বিকাশ, নগদ, রকেট বা ব্যাংক)।",
      "স্ক্রিনে প্রদর্শিত অ্যাডমিন পার্সোনাল/মার্চেন্ট নম্বরটি কপি করুন।",
      "আপনার বিকাশ বা নগদ অ্যাপে গিয়ে 'Send Money' বা 'Cash Out' সম্পন্ন করুন।",
      "লেনদেন শেষে মেসেজ বা স্টেটমেন্ট থেকে ট্রানজ্যাকশন আইডি (TrxID) কপি করুন।",
      "কাজসেতু অ্যাপে প্রেরক নম্বর ও TrxID বসিয়ে 'ডিপোজিট সাবমিট' বাটনে চাপুন।"
    )
  ),
  TutorialItem(
    id = "tut_withdraw",
    title = "ইনকামের টাকা বিকাশ/নগদে উইথড্র করার টিউটোরিয়াল",
    subtitle = "মাত্র ২০০ টাকা হলেই সরাসরি যেকোনো বিকাশ, নগদ বা রকেট নম্বরে ক্যাশ আউট করুন",
    category = "WITHDRAW",
    duration = "০২:৫০ মিনিট",
    icon = Icons.Default.Payments,
    videoUrl = "https://www.youtube.com/results?search_query=how+to+withdraw+money+to+bkash+nagad",
    steps = listOf(
      "কাজসেতু ওয়ালেটে গিয়ে 'টাকা তোলা' বা 'উইথড্র' বাটনে ট্যাপ করুন।",
      "উইথড্র মাধ্যম পছন্দ করুন (বিকাশ, নগদ, রকেট)।",
      "উত্তোলনের পরিমাণ লিখুন (সর্বনিম্ন ২০০ টাকা)।",
      "যে নম্বরে টাকা পেতে চান সেই সঠিক ১১ ডিজিটের ব্যক্তিগত নম্বরটি দিন।",
      "উইথড্র রিকোয়েস্ট নিশ্চিত করুন।",
      "অ্যাডমিন টিম ২ থেকে ২৪ ঘণ্টার মধ্যে আপনার নম্বরে টাকা পাঠিয়ে দেবে।"
    )
  ),
  TutorialItem(
    id = "tut_tasks_earn",
    title = "মাইক্রো টাস্ক সম্পন্ন করে প্রতিদিন আয় করার কৌশল",
    subtitle = "ইউটিউব সাবস্ক্রাইব, ফেসবুক ফলো, অ্যাপ রিভিউ ও টেলিগ্রাম চ্যানেলে যুক্ত হয়ে আয়",
    category = "TASKS",
    duration = "০৫:০০ মিনিট",
    icon = Icons.Default.TaskAlt,
    videoUrl = "https://www.youtube.com/results?search_query=micro+task+earning+bangla+tutorial",
    steps = listOf(
      "অ্যাপের 'মাইক্রো টাস্ক' মেন্যুতে প্রবেশ করুন।",
      "যেকোনো কাজের ওপর ট্যাপ করে কাজের বিস্তারিত বিবরণ ও নির্দেশনা পড়ুন।",
      "'ভিজিট ও সম্পন্ন করুন' লিংকে ক্লিক করে কাজটি করুন (যেমন: ভিডিও দেখা বা সাবস্ক্রাইব করা)।",
      "কাজের প্রমাণস্বরূপ একটি স্ক্রিনশট নিন অথবা কাজের ইউজারনেম কপি করুন।",
      "কাজসেতু অ্যাপে ফিরে এসে প্রমাণ লিখুন এবং স্ক্রিনশট সাবমিট করুন।",
      "বিজ্ঞাপনদাতা আপনার কাজ অনুমোদন করলেই আপনার ওয়ালেটে টাকা যোগ হয়ে যাবে।"
    )
  ),
  TutorialItem(
    id = "tut_account_activation",
    title = "অ্যাকাউন্ট ভেরিফিকেশন ও লাইফটাইম অ্যাক্টিভেশন নিয়ম",
    subtitle = "একবার অ্যাক্টিভ করে আনলিমিটেড টাস্ক ও জিমেইল সেল করার সুযোগ নিন",
    category = "ACTIVATION",
    duration = "০২:৪০ মিনিট",
    icon = Icons.Default.VerifiedUser,
    videoUrl = "https://www.youtube.com/results?search_query=online+account+activation+tutorial",
    steps = listOf(
      "অ্যাপের ড্যাশবোর্ড থেকে 'অ্যাকাউন্ট একটিভ' কার্ডে ক্লিক করুন।",
      "বিকাশ বা নগদ নম্বর কপি করে নির্দিষ্ট ১০০ টাকা অ্যাক্টিভেশন ফি সেন্ড মানি করুন।",
      "লেনদেনের ট্রানজ্যাকশন আইডি (TrxID) ইনপুট দিন।",
      "রিকোয়েস্ট পাঠিয়ে দিলে অ্যাডমিন ৫-১০ মিনিটে অ্যাকাউন্ট সক্রিয় করবে।",
      "সক্রিয় হওয়ার পর আপনি সমস্ত প্রিমিয়াম টাস্ক ও জিমেইল বিক্রির অপশন পাবেন।"
    )
  ),
  TutorialItem(
    id = "tut_redeem_bonus",
    title = "অফিসিয়াল টেলিগ্রাম চ্যানেল থেকে ফ্রি রিডিম বোনাস কোড সংগ্রহ",
    subtitle = "প্রতিদিন ফ্রি প্রমো কোড রিডিম করে ইনস্ট্যান্ট ফ্রি টাকা নিন ওয়ালেটে",
    category = "REDEEM",
    duration = "০১:৩০ মিনিট",
    icon = Icons.Default.CardGiftcard,
    videoUrl = "https://t.me/KajSetuOfficial",
    steps = listOf(
      "কাজসেতুর অফিসিয়াল টেলিগ্রাম চ্যানেলে জয়েন করুন।",
      "প্রতিদিন অ্যাডমিন সেখানে ফ্রি রিডিম বোনাস কোড প্রকাশ করে।",
      "কোডটি কপি করে কাজসেতু অ্যাপের 'রিডিম বোনাস' অপশনে পেস্ট করুন।",
      "'বোনাস রিডিম করুন' বাটনে চাপার সাথে সাথে আপনার ওয়ালেটে টাকা যোগ হবে।"
    )
  )
)

@Composable
fun TutorialsScreen(
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  var selectedCategory by remember { mutableStateOf("ALL") }
  var selectedTutorialForModal by remember { mutableStateOf<TutorialItem?>(null) }

  val categories = listOf(
    "ALL" to "সব টিউটোরিয়াল",
    "GMAIL" to "জিমেইল সেল",
    "DEPOSIT" to "ডিপোজিট",
    "WITHDRAW" to "উইথড্র",
    "TASKS" to "মাইক্রো টাস্ক",
    "ACTIVATION" to "অ্যাক্টিভেশন",
    "REDEEM" to "ফ্রি বোনাস"
  )

  val filteredTutorials = remember(selectedCategory) {
    if (selectedCategory == "ALL") AllTutorials
    else AllTutorials.filter { it.category == selectedCategory }
  }

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "টিউটোরিয়াল ও হেল্প গাইড",
        subtitle = "ভিডিও দেখে সহজে কাজ ও লেনদেন শিখুন",
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
      // 1. Hero Banner
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .background(
                Brush.horizontalGradient(
                  colors = listOf(BluePrimaryDark, Color(0xFF1E3A8A), Color(0xFF0F172A))
                )
              )
              .padding(20.dp)
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(AmberGold),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "ভিডিও টিউটোরিয়াল একাডেমি",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  )
                  Text(
                    text = "কীভাবে কাজ করবেন ও আয় তুলবেন - সম্পূর্ণ গাইড",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                  )
                }
              }
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "আপনি নতুন ইউজার হলেও চিন্তা নেই! নিচের যেকোনো টিউটোরিয়াল দেখে ধাপে ধাপে কাজ সম্পন্ন করুন এবং ভিডিও দেখে সহায়তা নিন।",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFFE2E8F0),
                  lineHeight = 20.sp
                )
              )
            }
          }
        }
      }

      // 2. Category Filters
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(categories) { (catKey, catLabel) ->
            val isSelected = catKey == selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = catKey },
              label = { Text(catLabel, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = BluePrimary,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(20.dp)
            )
          }
        }
      }

      // 3. Tutorial Cards
      items(filteredTutorials) { item ->
        TutorialCard(
          item = item,
          onWatchVideo = {
            viewModel.openExternalUrl(context, item.videoUrl)
          },
          onReadGuide = {
            selectedTutorialForModal = item
          }
        )
      }
    }
  }

  // Tutorial Step by Step Modal Dialog
  selectedTutorialForModal?.let { tut ->
    TutorialDetailDialog(
      tutorial = tut,
      onDismiss = { selectedTutorialForModal = null },
      onWatchVideo = {
        viewModel.openExternalUrl(context, tut.videoUrl)
      }
    )
  }
}

@Composable
fun TutorialCard(
  item: TutorialItem,
  onWatchVideo: () -> Unit,
  onReadGuide: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(BlueLightBg),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = item.icon,
              contentDescription = null,
              tint = BluePrimary,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = item.title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
            )
            Text(
              text = "সময়কাল: ${item.duration}",
              style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = item.subtitle,
        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 19.sp)
      )

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = CardBorder)
      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onReadGuide,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f).height(42.dp)
        ) {
          Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("ধাপে ধাপে নিয়ম", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onWatchVideo,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RedError),
          modifier = Modifier.weight(1f).height(42.dp)
        ) {
          Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("ভিডিও দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

@Composable
fun TutorialDetailDialog(
  tutorial: TutorialItem,
  onDismiss: () -> Unit,
  onWatchVideo: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(tutorial.icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = tutorial.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    },
    text = {
      LazyColumn(
        modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        item {
          Text(
            text = "নিচের প্রতিটি ধাপ অনুসরণ করে কাজটি সহজে সম্পন্ন করুন:",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, color = TextSecondary)
          )
        }

        items(tutorial.steps) { step ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
          ) {
            Box(
              modifier = Modifier
                .padding(top = 2.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(EmeraldGreen)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = step,
              style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp, color = TextPrimary)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onWatchVideo,
        colors = ButtonDefaults.buttonColors(containerColor = RedError),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("ভিডিও প্লে করুন", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("ঠিক আছে")
      }
    }
  )
}

/**
 * Reusable banner button to place inside any specific screen (Gmail, Deposit, Withdraw, etc.)
 */
@Composable
fun ScreenTutorialBanner(
  title: String,
  subtitle: String,
  onWatchVideo: () -> Unit,
  onViewAllTutorials: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFCA5A5))
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFFEF4444)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB91C1C), fontSize = 11.sp)
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Button(
          onClick = onWatchVideo,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("ভিডিও", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        OutlinedButton(
          onClick = onViewAllTutorials,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Text("সব গাইড", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
