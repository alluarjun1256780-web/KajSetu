package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.ui.components.KajSetuTopBar
import com.example.ui.components.formatBdt
import com.example.ui.theme.*

@Composable
fun AdvertiseScreen(
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit,
  onBack: () -> Unit
) {
  val packages by viewModel.activeAdPackages.collectAsState()
  val contactSettings by viewModel.adContactSettings.collectAsState()
  val context = LocalContext.current

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "বিজ্ঞাপন ও স্পন্সরশিপ",
        subtitle = "আপনার ব্যবসার প্রচার করুন",
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
      // 1. Hero Header
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(EmeraldGreenBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Campaign, contentDescription = null, tint = EmeraldGreenDark, modifier = Modifier.size(34.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "আপনার ব্যবসার প্রচার করুন",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
              )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "আমাদের ওয়েবসাইটে আপনার ব্যবসা, টেলিগ্রাম বট, সার্ভিস বা ব্র্যান্ডের প্রচার করতে যোগাযোগ করুন। হাজারো সক্রিয় তরুণ ফ্রিল্যান্সার ও অনলাইন আর্নারদের কাছে পৌঁছান সহজেই।",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
              )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats Row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BlueLightBg)
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("৫০,০০০+", fontWeight = FontWeight.Bold, color = BluePrimaryDark, fontSize = 16.sp)
                Text("মাসিক ইমপ্রেশন", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
              }
              Divider(modifier = Modifier.height(30.dp).width(1.dp), color = CardBorder)
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("১০০% টার্গেটেড", fontWeight = FontWeight.Bold, color = EmeraldGreenDark, fontSize = 16.sp)
                Text("বাস্তব গ্রাহক", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
              }
              Divider(modifier = Modifier.height(30.dp).width(1.dp), color = CardBorder)
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("২৪/৭", fontWeight = FontWeight.Bold, color = AmberGold, fontSize = 16.sp)
                Text("লাইভ সাপোর্ট", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
              }
            }
          }
        }
      }

      // 2. Direct Contact Buttons
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "বিজ্ঞাপন দিতে যোগাযোগ করুন",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Text(
              text = "সরাসরি অ্যাডমিনের সাথে যোগাযোগ করে আপনার বিজ্ঞাপনটি চালু করুন:",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { viewModel.contactForSponsorship(context, viaEmail = false) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.weight(1f).height(44.dp).testTag("advertise_whatsapp_button")
              ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }

              OutlinedButton(
                onClick = { viewModel.contactForSponsorship(context, viaEmail = true) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(44.dp).testTag("advertise_email_button")
              ) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Email", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }

              Button(
                onClick = { viewModel.contactViaTelegram(context) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
                modifier = Modifier.weight(1f).height(44.dp).testTag("advertise_telegram_button")
              ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Telegram", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }
      }

      // 3. Available Packages Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "বিজ্ঞাপন প্যাকেজসমূহ (Advertising Packages)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
          )
          Text(
            text = "${packages.size}টি প্যাকেজ",
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
          )
        }
      }

      if (packages.isEmpty()) {
        item {
          Text(
            text = "কোনো প্যাকেজ পাওয়া যায়নি। সরাসরি WhatsApp-এ যোগাযোগ করুন।",
            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
            modifier = Modifier.padding(vertical = 12.dp)
          )
        }
      } else {
        items(packages) { pkg ->
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
                Column {
                  Text(
                    text = pkg.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                  )
                  Text(
                    text = "মেয়াদ: ${pkg.durationDays} দিন • পজিশন: ${pkg.position}",
                    style = MaterialTheme.typography.labelSmall.copy(color = BluePrimary, fontWeight = FontWeight.SemiBold)
                  )
                }

                Text(
                  text = formatBdt(pkg.price),
                  style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
                )
              }

              if (pkg.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = pkg.description,
                  style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))
              Divider(color = CardBorder)
              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "ফিচার ও সুবিধাসমূহ:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextMuted)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = pkg.features,
                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 20.sp)
              )

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = { viewModel.contactForSponsorship(context, viaEmail = false) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier.fillMaxWidth().height(42.dp)
              ) {
                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("এই প্যাকেজটি বুক করতে যোগাযোগ করুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }
          }
        }
      }

      // 4. Advertising Rules & FAQ
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "বিজ্ঞাপন নীতি ও শর্তাবলী",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "১. কোনো প্রকার অবৈধ, স্ক্যাম বা জুয়া সংক্রান্ত প্রচার নিষিদ্ধ।\n" +
                  "২. পেমেন্ট নিশ্চিত করার সাথে সাথে অ্যাডমিন প্যানেল থেকে বিজ্ঞাপন একটিভ করে দেওয়া হবে।\n" +
                  "৩. ক্যাম্পেইন চলাকালীন রিয়েল-টাইম ক্লিক ও পারফরম্যান্স রিপোর্ট প্রদান করা হবে।\n" +
                  "৪. ব্যানার সাইজ ও ফরম্যাট অনুযায়ী অপ্টিমাইজড করে প্রদর্শন করা হবে।",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp)
            )
          }
        }
      }
    }
  }
}
