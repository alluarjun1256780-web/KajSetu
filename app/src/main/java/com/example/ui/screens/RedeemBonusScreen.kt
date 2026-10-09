package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KajSetuViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.KajSetuTopBar
import com.example.ui.components.formatBdt
import com.example.ui.components.formatDateTime
import com.example.ui.theme.*

@Composable
fun RedeemBonusScreen(
  viewModel: KajSetuViewModel,
  onBack: () -> Unit
) {
  val user by viewModel.currentUser.collectAsState()
  val siteSettings by viewModel.siteSettings.collectAsState()
  val usages by viewModel.userRedeemUsages.collectAsState()
  val context = LocalContext.current

  var redeemCodeInput by remember { mutableStateOf("") }
  var isSubmitting by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "রিডিম বোনাস (Redeem Bonus)",
        subtitle = "অফিসিয়াল কোড দিয়ে ফ্রি বোনাস টাকা নিন",
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
      // 1. Prominent Official Telegram Channel Notice & Banner
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.linearGradient(
                  colors = listOf(
                    Color(0xFF0284C7), // Sky Blue
                    Color(0xFF0369A1),
                    Color(0xFF0F172A)
                  )
                )
              )
              .padding(20.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "📢 প্রতিদিন ফ্রি রিডিম কোড পান!",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  )
                  Text(
                    text = "আমাদের অফিসিয়াল টেলিগ্রাম চ্যানেলে জয়েন হন",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = Color.White.copy(alpha = 0.9f)
                    )
                  )
                }
              }

              Text(
                text = "রিডিম কোড পেতে হলে আমাদের অফিশিয়াল টেলিগ্রাম চ্যানেলে জয়েন হন, ওখানে প্রতিদিন নতুন রিডিম কোড ও ধামাকা বোনাস অফার দেওয়া হয়!",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = Color.White.copy(alpha = 0.95f),
                  lineHeight = 22.sp
                )
              )

              // Dynamic Telegram Join Button
              Button(
                onClick = {
                  viewModel.openTelegramChannel(context, siteSettings?.telegramChannelUrl)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF22C55E), // Emerald Green
                  contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                modifier = Modifier
                  .align(Alignment.End)
                  .testTag("join_telegram_channel_button")
              ) {
                Icon(
                  Icons.Default.Send,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "টেলিগ্রাম চ্যানেলে জয়েন হন",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
            }
          }
        }
      }

      // 2. Redeem Code Input Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = EmeraldGreen,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "আপনার রিডিম কোড লিখুন",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = redeemCodeInput,
              onValueChange = { redeemCodeInput = it.uppercase().trim() },
              label = { Text("রিডিম কোড (e.g. KAJSETU50)") },
              placeholder = { Text("কোড এখানে পেস্ট করুন") },
              leadingIcon = {
                Icon(Icons.Default.VpnKey, contentDescription = null, tint = BluePrimary)
              },
              trailingIcon = {
                if (redeemCodeInput.isNotBlank()) {
                  IconButton(onClick = { redeemCodeInput = "" }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("redeem_code_input_field")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                if (redeemCodeInput.isBlank()) return@Button
                isSubmitting = true
                viewModel.redeemBonusCode(
                  code = redeemCodeInput,
                  onSuccess = {
                    isSubmitting = false
                    redeemCodeInput = ""
                  }
                )
                isSubmitting = false
              },
              enabled = redeemCodeInput.isNotBlank() && !isSubmitting,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_redeem_code_button")
            ) {
              Icon(Icons.Default.Redeem, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isSubmitting) "যাচাই করা হচ্ছে..." else "বোনাস সংগ্রহ করুন (Redeem Now)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
        }
      }

      // 3. User Redeem History
      item {
        Text(
          text = "আমার রিডিম হিস্টোরি (${usages.size}টি)",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        )
      }

      if (usages.isEmpty()) {
        item {
          EmptyStateView(
            icon = Icons.Default.CardGiftcard,
            title = "কোনো রিডিম রেকর্ড নেই",
            message = "টেলিগ্রাম চ্যানেল থেকে কোড সংগ্রহ করে উপরে সাবমিট করুন এবং ফ্রি বোনাস নিন।"
          )
        }
      } else {
        items(usages.size) { index ->
          val usage = usages[index]
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreenBg),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldGreenDark,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = usage.code,
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = TextPrimary
                    )
                  )
                  Text(
                    text = formatDateTime(usage.redeemedAt),
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                  )
                }
              }

              Text(
                text = "+${formatBdt(usage.bonusAmount)}",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = EmeraldGreenDark
                )
              )
            }
          }
        }
      }
    }
  }
}
