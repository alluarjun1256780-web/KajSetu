package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun LandingScreen(
  onNavigateToAuth: () -> Unit,
  onNavigateToDashboard: () -> Unit,
  onNavigateToTasks: () -> Unit,
  onNavigateToServices: () -> Unit,
  onNavigateToPolicies: () -> Unit,
  onNavigateToSupport: () -> Unit,
  isLoggedIn: Boolean,
  onContactWhatsApp: () -> Unit = {},
  onContactEmail: () -> Unit = {},
  onContactTelegram: () -> Unit = {},
  onNavigateToAdvertise: () -> Unit = {}
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(SlateBackground)
  ) {
    // 1. Header Bar
    item {
      Surface(
        color = SurfaceCard,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BlueLightBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Work,
                contentDescription = "Logo",
                tint = BluePrimary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "কাজসেতু",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = BluePrimaryDark
                )
              )
              Text(
                text = "KajSetu",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = TextMuted,
                  fontSize = 10.sp
                )
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (isLoggedIn) {
              Button(
                onClick = onNavigateToDashboard,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier.testTag("go_to_dashboard_button")
              ) {
                Text("ড্যাশবোর্ড")
              }
            } else {
              OutlinedButton(
                onClick = onNavigateToAuth,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BluePrimary),
                modifier = Modifier.testTag("landing_login_button")
              ) {
                Text("লগইন")
              }
              Button(
                onClick = onNavigateToAuth,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier.testTag("landing_register_button")
              ) {
                Text("রেজিস্ট্রেশন")
              }
            }
          }
        }
      }
    }

    // 2. Hero Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(BlueLightBg.copy(alpha = 0.5f))
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .border(1.dp, BluePrimary.copy(alpha = 0.2f), RoundedCornerShape(30.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(EmeraldGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "বাংলাদেশের জন্য স্মার্ট ডিজিটাল আয়ের প্ল্যাটফর্ম",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = BluePrimaryDark
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Headline
        Text(
          text = "অনলাইনে কাজ করুন,\nস্মার্টভাবে আয় করুন",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Supporting text
        Text(
          text = "মাইক্রো টাস্ক, ডিজিটাল সার্ভিস এবং বিভিন্ন অনুমোদিত অনলাইন কাজের মাধ্যমে আপনার দক্ষতাকে আয়ের সুযোগে পরিণত করুন।",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = TextSecondary,
            textAlign = TextAlign.Center
          ),
          modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Button(
            onClick = {
              if (isLoggedIn) onNavigateToTasks() else onNavigateToAuth()
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("hero_start_task_button")
          ) {
            Text("কাজ শুরু করুন", fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onNavigateToServices,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BluePrimary),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("hero_how_it_works_button")
          ) {
            Text("ডিজিটাল সার্ভিস")
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hero Visual Card
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "সরাসরি আয়ের ড্যাশবোর্ড ওভারভিউ",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(EmeraldGreenBg)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("লাইভ", color = EmeraldGreenDark, style = MaterialTheme.typography.labelSmall)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(BlueLightBg)
                  .padding(12.dp)
              ) {
                Column {
                  Text("মোট জমা আয়", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                  Text("৳৩৫০.০০", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark))
                }
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(EmeraldGreenBg)
                  .padding(12.dp)
              ) {
                Column {
                  Text("সম্পন্ন কাজ", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                  Text("৪২টি", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark))
                }
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(AmberGoldBg)
                  .padding(12.dp)
              ) {
                Column {
                  Text("উত্তোলন", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                  Text("বিকাশ/নগদ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E)))
                }
              }
            }
          }
        }
      }
    }

    // 3. Why Choose Us Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "কেন আমরা সেরা?",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
        Text(
          text = "বিশ্বাসযোগ্যতা এবং নিরাপত্তার সাথে কাজসেতু আপনার পাশে রয়েছে",
          style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          FeatureMiniCard(
            title = "নিরাপদ প্ল্যাটফর্ম",
            desc = "স্বচ্ছ ডেটা ও SSL এনক্রিপ্টেড সুরক্ষিত হিসাব।",
            icon = Icons.Default.Security,
            iconColor = BluePrimary,
            modifier = Modifier.weight(1f)
          )
          FeatureMiniCard(
            title = "স্বচ্ছ হিসাব",
            desc = "প্রতিটি কাজের টাকা রিয়েল-টাইম লেজারে জমা।",
            icon = Icons.Default.ReceiptLong,
            iconColor = EmeraldGreen,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          FeatureMiniCard(
            title = "দ্রুত সাপোর্ট",
            desc = "২৪/৭ সার্বক্ষণিক বাংলা হেল্পডেস্ক ও টিকিট সাপোর্ট।",
            icon = Icons.Default.SupportAgent,
            iconColor = AmberGold,
            modifier = Modifier.weight(1f)
          )
          FeatureMiniCard(
            title = "সহজ ব্যবহার",
            desc = "বিকাশ, নগদ ও রকেটে দ্রুত পেমেন্ট সুবিধা।",
            icon = Icons.Default.AccountBalanceWallet,
            iconColor = Color(0xFF8B5CF6),
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 4. How It Works Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(20.dp)
      ) {
        Text(
          text = "কীভাবে কাজ করে?",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
        Text(
          text = "সহজ ৪টি ধাপে আপনিও শুরু করতে পারেন আপনার অনলাইন আয়ের যাত্রা",
          style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(20.dp))

        StepCard(step = "০১", title = "অ্যাকাউন্ট খুলুন", desc = "সঠিক তথ্য ও মোবাইল নম্বর দিয়ে বিনামূল্যে রেজিস্ট্রেশন সম্পন্ন করুন।")
        Spacer(modifier = Modifier.height(12.dp))
        StepCard(step = "০২", title = "অ্যাকাউন্ট সক্রিয় করুন", desc = "স্প্যাম প্রতিরোধে সামান্য এককালীন ফিতে অ্যাকাউন্ট ভেরিফাই করুন।")
        Spacer(modifier = Modifier.height(12.dp))
        StepCard(step = "০৩", title = "কাজ সম্পন্ন করুন", desc = "মাইক্রো টাস্কের নির্দেশ অনুযায়ী কাজ করে প্রুফ বা প্রমাণ জমা দিন।")
        Spacer(modifier = Modifier.height(12.dp))
        StepCard(step = "০৪", title = "আয় উত্তোলন করুন", desc = "কাজ অনুমোদিত হলেই বিকাশ বা নগদ অ্যাকাউন্টে সরাসরি টাকা তুলে নিন।")
      }
    }

    // 5. CTA Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(BluePrimaryDark)
          .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "আপনার দক্ষতা, আপনার আয়।",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
          )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "হাজারো তরুণ-তরুণীর সাথে আজই যুক্ত হোন বিশ্বস্ত প্ল্যাটফর্ম কাজসেতুতে।",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = BlueLightBg,
            textAlign = TextAlign.Center
          )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = {
            if (isLoggedIn) onNavigateToDashboard() else onNavigateToAuth()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("cta_start_button")
        ) {
          Text("এখনই শুরু করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      }
    }

    // Sponsorship & Advertise With Us Card
    item {
      Box(modifier = Modifier.padding(16.dp)) {
        com.example.ui.components.AdvertiseWithUsCard(
          onContactWhatsApp = onContactWhatsApp,
          onContactEmail = onContactEmail,
          onContactTelegram = onContactTelegram,
          onViewPackages = onNavigateToAdvertise
        )
      }
    }

    // 6. Footer Links
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0F172A))
          .padding(24.dp)
      ) {
        Text(
          text = "কাজসেতু (KajSetu)",
          style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
        Text(
          text = "কাজ করুন • আয় করুন • নিরাপদে এগিয়ে যান",
          style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          TextButton(onClick = onNavigateToPolicies) {
            Text("নীতিমালা", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelMedium)
          }
          TextButton(onClick = onNavigateToAdvertise) {
            Text("বিজ্ঞাপন দিন", color = EmeraldGreen, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          }
          TextButton(onClick = onNavigateToSupport) {
            Text("সাপোর্ট", color = Color(0xFFCBD5E1), style = MaterialTheme.typography.labelMedium)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "© ২০২৬ কাজসেতু। সর্বস্বত্ব সংরক্ষিত। বাংলাদেশ।",
          style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B), textAlign = TextAlign.Center),
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@Composable
fun FeatureMiniCard(
  title: String,
  desc: String,
  icon: ImageVector,
  iconColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(iconColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 12.sp))
    }
  }
}

@Composable
fun StepCard(step: String, title: String, desc: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(SlateBackground)
      .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
      .padding(14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(BluePrimary),
      contentAlignment = Alignment.Center
    ) {
      Text(text = step, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
    Spacer(modifier = Modifier.width(14.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
      Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
    }
  }
}
