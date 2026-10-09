package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatBdt(amount: Double): String {
  return "৳" + String.format(Locale.US, "%,.2f", amount)
}

fun formatDateTime(timestamp: Long): String {
  val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
  return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KajSetuTopBar(
  title: String,
  subtitle: String? = null,
  showBack: Boolean = false,
  onBackClick: () -> Unit = {},
  unreadCount: Int = 0,
  onNotificationClick: () -> Unit = {},
  onProfileClick: () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {}
) {
  Surface(
    color = SurfaceCard,
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    TopAppBar(
      title = {
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = BluePrimaryDark
            )
          )
          if (subtitle != null) {
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
          }
        }
      },
      navigationIcon = {
        if (showBack) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "ফিরে যান",
              tint = TextPrimary
            )
          }
        } else {
          Box(
            modifier = Modifier
              .padding(start = 12.dp, end = 8.dp)
              .size(38.dp)
              .clip(CircleShape)
              .background(BlueLightBg),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Work,
              contentDescription = "লোগো",
              tint = BluePrimary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      },
      actions = {
        actions()
        if (unreadCount >= 0) {
          IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.testTag("notifications_button")
          ) {
            BadgedBox(
              badge = {
                if (unreadCount > 0) {
                  Badge(
                    containerColor = RedError,
                    contentColor = Color.White
                  ) {
                    Text("$unreadCount")
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "নোটিফিকেশন",
                tint = TextSecondary
              )
            }
          }
        }
        IconButton(
          onClick = onProfileClick,
          modifier = Modifier.testTag("profile_button")
        ) {
          Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "প্রোফাইল",
            tint = BluePrimary
          )
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = SurfaceCard
      )
    )
  }
}

@Composable
fun StatCard(
  title: String,
  value: String,
  icon: ImageVector,
  iconColor: Color,
  backgroundColor: Color = SurfaceCard,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  onClick: (() -> Unit)? = null
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
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
          .clip(RoundedCornerShape(12.dp))
          .background(iconColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = iconColor,
          modifier = Modifier.size(24.dp)
        )
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodySmall.copy(
            color = TextSecondary,
            fontWeight = FontWeight.Medium
          )
        )
        Text(
          text = value,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
          )
        }
      }
    }
  }
}

@Composable
fun StatusPill(status: String) {
  val (bgColor, txtColor, label) = when (status.uppercase()) {
    "ACTIVE", "APPROVED", "COMPLETED" -> Triple(EmeraldGreenBg, EmeraldGreenDark, "সক্রিয় / সফল")
    "INACTIVE" -> Triple(AmberGoldBg, AmberGold, "নিষ্ক্রিয়")
    "PENDING" -> Triple(AmberGoldBg, Color(0xFFB45309), "যাচাইাধীন / পেন্ডিং")
    "PROCESSING" -> Triple(BlueLightBg, BluePrimary, "প্রক্রিয়াধীন")
    "REJECTED", "SUSPENDED" -> Triple(RedBg, RedError, "বাতিল / স্থগিত")
    else -> Triple(Color(0xFFF1F5F9), TextSecondary, status)
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(bgColor)
      .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.SemiBold,
        color = txtColor
      )
    )
  }
}

@Composable
fun EmptyStateView(
  icon: ImageVector,
  title: String,
  message: String,
  actionButtonText: String? = null,
  onActionClick: () -> Unit = {}
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(BlueLightBg),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = BluePrimary,
        modifier = Modifier.size(32.dp)
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        textAlign = TextAlign.Center
      )
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = message,
      style = MaterialTheme.typography.bodyMedium.copy(
        color = TextSecondary,
        textAlign = TextAlign.Center
      )
    )
    if (actionButtonText != null) {
      Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = onActionClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
      ) {
        Text(actionButtonText)
      }
    }
  }
}

@Composable
fun SponsoredAdBanner(
  ad: com.example.data.entity.AdvertisementEntity,
  onAdClick: () -> Unit,
  onImpression: () -> Unit = {},
  labelText: String = "Sponsored",
  modifier: Modifier = Modifier
) {
  LaunchedEffect(ad.id) {
    onImpression()
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onAdClick() }
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
              .background(AmberGoldBg)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = labelText.ifBlank { "Sponsored" },
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E),
                fontSize = 10.sp
              )
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = ad.advertiserName.ifBlank {
              when (ad.position) {
                "HEADER" -> "টপ ব্যানার স্পন্সর"
                "BETWEEN_TASKS" -> "টাস্ক স্পন্সর"
                else -> "বিজ্ঞাপন"
              }
            },
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
          )
        }

        Icon(
          imageVector = Icons.Default.OpenInNew,
          contentDescription = null,
          tint = BluePrimary,
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(BlueLightBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when {
              ad.targetUrl.contains("t.me") -> Icons.Default.Send
              ad.campaignType == "TELEGRAM_BOT" -> Icons.Default.SmartToy
              else -> Icons.Default.Campaign
            },
            contentDescription = null,
            tint = BluePrimary,
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = ad.title,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
          )
          if (ad.subtitle.isNotBlank() || ad.description.isNotBlank()) {
            Text(
              text = ad.subtitle.ifBlank { ad.description },
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
              maxLines = 2
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = onAdClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
      ) {
        Text(ad.buttonText.ifBlank { "ভিজিট করুন" }, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun AdvertiseWithUsCard(
  onContactWhatsApp: () -> Unit,
  onContactEmail: () -> Unit,
  onContactTelegram: () -> Unit = {},
  onViewPackages: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldGreen.copy(alpha = 0.4f))),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(EmeraldGreenBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Campaign, contentDescription = null, tint = EmeraldGreenDark, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Advertise With Us • আপনার ব্যবসার প্রচার করুন",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
          )
          Text(
            text = "আমাদের ওয়েবসাইটে আপনার ব্যবসা, সার্ভিস বা ব্র্যান্ডের প্রচার করতে যোগাযোগ করুন।",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Contact Buttons
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Button(
          onClick = onContactWhatsApp,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
          modifier = Modifier.weight(1f).height(40.dp),
          contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
          Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onContactEmail,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
          modifier = Modifier.weight(1f).height(40.dp),
          contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
          Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Email", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onContactTelegram,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC)),
          modifier = Modifier.weight(1f).height(40.dp),
          contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
          Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Telegram", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedButton(
        onClick = onViewPackages,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldGreenDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldGreen)),
        modifier = Modifier.fillMaxWidth().height(38.dp)
      ) {
        Icon(Icons.Default.ViewCarousel, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldGreenDark)
        Spacer(modifier = Modifier.width(6.dp))
        Text("বিজ্ঞাপন প্যাকেজসমূহ ও রেট দেখুন (View Packages)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}

