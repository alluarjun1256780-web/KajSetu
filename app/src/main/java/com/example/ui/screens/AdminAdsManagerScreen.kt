package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.*
import com.example.ui.KajSetuViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AdminAdsManagerScreen(viewModel: KajSetuViewModel) {
  val ads by viewModel.allAds.collectAsState()
  val advertisers by viewModel.advertisers.collectAsState()
  val packages by viewModel.adPackages.collectAsState()
  val campaigns by viewModel.adCampaigns.collectAsState()
  val contactSettings by viewModel.adContactSettings.collectAsState()
  val auditLogs by viewModel.adAuditLogs.collectAsState()
  val context = LocalContext.current

  var selectedSubTab by remember { mutableStateOf(0) }
  val subTabs = listOf(
    "বিজ্ঞাপন (${ads.size})",
    "ক্যাম্পেইন (${campaigns.size})",
    "বিজ্ঞাপনদাতা (${advertisers.size})",
    "প্যাকেজ (${packages.size})",
    "রেভিনিউ ও রিপোর্ট",
    "এড সেটিংস",
    "অডিট লগ"
  )

  Column(modifier = Modifier.fillMaxSize()) {
    // Sub Tab Row
    ScrollableTabRow(
      selectedTabIndex = selectedSubTab,
      containerColor = SurfaceCard,
      contentColor = BluePrimary,
      edgePadding = 12.dp
    ) {
      subTabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedSubTab == index,
          onClick = { selectedSubTab = index },
          text = {
            Text(
              text = title,
              fontSize = 12.sp,
              fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal
            )
          }
        )
      }
    }

    // Active Tab Content
    when (selectedSubTab) {
      0 -> AdsListSubTab(viewModel, ads, advertisers, context)
      1 -> CampaignsSubTab(viewModel, campaigns, advertisers, packages)
      2 -> AdvertisersSubTab(viewModel, advertisers)
      3 -> PackagesSubTab(viewModel, packages)
      4 -> RevenueAnalyticsSubTab(ads, campaigns, advertisers)
      5 -> AdSettingsSubTab(viewModel, contactSettings)
      6 -> AdAuditLogsSubTab(auditLogs)
    }
  }
}

// ==========================================
// 1. ADS LIST SUB-TAB
// ==========================================
@Composable
private fun AdsListSubTab(
  viewModel: KajSetuViewModel,
  ads: List<AdvertisementEntity>,
  advertisers: List<AdvertiserEntity>,
  context: android.content.Context
) {
  var showCreateDialog by remember { mutableStateOf(false) }
  var adToEdit by remember { mutableStateOf<AdvertisementEntity?>(null) }

  val positions = listOf(
    "HEADER" to "হেডার (Header)",
    "DASHBOARD" to "ড্যাশবোর্ড (Dashboard)",
    "BETWEEN_TASKS" to "টাস্কের মাঝে (Between Tasks)",
    "HOMEPAGE" to "হোমপেজ (Homepage)",
    "SIDEBAR" to "সাইডবার (Sidebar)",
    "MARKETPLACE" to "মার্কেটপ্লেস (Marketplace)",
    "FOOTER" to "ফুটার (Footer)",
    "MOBILE_BANNER" to "মোবাইল ব্যানার",
    "DESKTOP_BANNER" to "ডেস্কটপ ব্যানার"
  )

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // Actions Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("বিজ্ঞাপন তালিকা ও নিয়ন্ত্রণ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          Text("মোট ${ads.size}টি বিজ্ঞাপনের অবস্থা ও পরিসংখ্যান", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
        Button(
          onClick = { showCreateDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("নতুন Ad তৈরি")
        }
      }
    }

    if (ads.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.Campaign,
          title = "কোনো বিজ্ঞাপন নেই",
          message = "নতুন বিজ্ঞাপন তৈরি করতে উপরের বাটনে চাপুন।"
        )
      }
    } else {
      items(ads) { ad ->
        val ctr = if (ad.impressionCount > 0) String.format("%.2f", (ad.clickCount.toDouble() / ad.impressionCount.toDouble()) * 100.0) else "0.00"

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = if (ad.isActive) SurfaceCard else Color(0xFFF8FAFC)),
          elevation = CardDefaults.cardElevation(defaultElevation = if (ad.isActive) 2.dp else 0.dp),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (ad.isActive) BluePrimary.copy(alpha = 0.3f) else CardBorder)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Badges & 1-Click Toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (ad.isActive) EmeraldGreenBg else RedBg)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = if (ad.isActive) "সক্রিয় (Active)" else "বন্ধ (Disabled)",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = if (ad.isActive) EmeraldGreenDark else RedError,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(BlueLightBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  val posLabel = positions.find { it.first == ad.position }?.second ?: ad.position
                  Text(posLabel, style = MaterialTheme.typography.labelSmall.copy(color = BluePrimaryDark, fontSize = 10.sp))
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AmberGoldBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("প্রায়োরিটি: ${ad.priority}", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF92400E), fontSize = 10.sp))
                }
              }

              // 1-Click Toggle
              Switch(
                checked = ad.isActive,
                onCheckedChange = { viewModel.adminToggleAd(ad.id, it) }
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Advertiser
            Text(ad.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
            Text(
              text = "বিজ্ঞাপনদাতা: ${ad.advertiserName} • টাইপ: ${ad.campaignType} • মূল্য: ${formatBdt(ad.price)}",
              style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            if (ad.description.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(ad.description, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted), maxLines = 2)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Target URL display
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
              Text(ad.targetUrl, style = MaterialTheme.typography.labelSmall.copy(color = BluePrimaryDark), maxLines = 1, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics Bar: Impressions, Clicks, CTR
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1F5F9))
                .padding(8.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ইমপ্রেশন", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Text("${ad.impressionCount}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ক্লিক", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Text("${ad.clickCount}", fontWeight = FontWeight.Bold, color = BluePrimaryDark, fontSize = 13.sp)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CTR হার", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Text("$ctr%", fontWeight = FontWeight.Bold, color = EmeraldGreenDark, fontSize = 13.sp)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = { viewModel.openAdTarget(context, ad) },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("লিংক টেস্ট", fontSize = 11.sp)
              }

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = { adToEdit = ad }, modifier = Modifier.size(34.dp)) {
                  Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BluePrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { viewModel.adminDeleteAd(ad.id) }, modifier = Modifier.size(34.dp)) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }
      }
    }
  }

  // Create / Edit Ad Dialog
  if (showCreateDialog || adToEdit != null) {
    AdFormDialog(
      initialAd = adToEdit,
      advertisers = advertisers,
      onDismiss = {
        showCreateDialog = false
        adToEdit = null
      },
      onSave = { ad ->
        if (adToEdit != null) {
          viewModel.adminUpdateAd(ad)
        } else {
          viewModel.adminCreateAd(ad)
        }
        showCreateDialog = false
        adToEdit = null
      }
    )
  }
}

// Dialog for Ad Form
@Composable
private fun AdFormDialog(
  initialAd: AdvertisementEntity?,
  advertisers: List<AdvertiserEntity>,
  onDismiss: () -> Unit,
  onSave: (AdvertisementEntity) -> Unit
) {
  var title by remember { mutableStateOf(initialAd?.title ?: "") }
  var advertiserName by remember { mutableStateOf(initialAd?.advertiserName ?: if (advertisers.isNotEmpty()) advertisers.first().companyName else "নিজস্ব প্রমোশন") }
  var targetUrl by remember { mutableStateOf(initialAd?.targetUrl ?: "https://t.me/") }
  var description by remember { mutableStateOf(initialAd?.description ?: "") }
  var position by remember { mutableStateOf(initialAd?.position ?: "DASHBOARD") }
  var campaignType by remember { mutableStateOf(initialAd?.campaignType ?: "BANNER") }
  var pricingType by remember { mutableStateOf(initialAd?.pricingType ?: "FIXED") }
  var priceStr by remember { mutableStateOf(initialAd?.price?.toString() ?: "500.0") }
  var priorityStr by remember { mutableStateOf(initialAd?.priority?.toString() ?: "5") }
  var buttonText by remember { mutableStateOf(initialAd?.buttonText ?: "ভিজিট করুন") }
  var bannerUrl by remember { mutableStateOf(initialAd?.bannerImageUrl ?: "") }

  // Photo Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      bannerUrl = uri.toString()
    }
  }

  val positions = listOf("HEADER", "DASHBOARD", "BETWEEN_TASKS", "HOMEPAGE", "SIDEBAR", "MARKETPLACE", "FOOTER")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(if (initialAd != null) "বিজ্ঞাপন এডিট করুন" else "নতুন বিজ্ঞাপন তৈরি করুন", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("বিজ্ঞাপনের শিরোনাম (Ad Title)") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = advertiserName,
          onValueChange = { advertiserName = it },
          label = { Text("বিজ্ঞাপনদাতা / কোম্পানির নাম") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = targetUrl,
          onValueChange = { targetUrl = it },
          label = { Text("টার্গেট লিংক (Website / Telegram Bot URL)") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("বিজ্ঞাপনের বিবরণ (Description)") },
          modifier = Modifier.fillMaxWidth()
        )

        // Banner Image Upload
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("ব্যানার ছবি (ঐচ্ছিক):", style = MaterialTheme.typography.bodySmall)
          OutlinedButton(
            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (bannerUrl.isNotBlank()) "ছবি সংযুক্ত ✓" else "গ্যালারি থেকে আপলোড", fontSize = 11.sp)
          }
        }

        Text("প্রদর্শনের স্থান (Position):", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(positions) { pos ->
            FilterChip(
              selected = position == pos,
              onClick = { position = pos },
              label = { Text(pos, fontSize = 11.sp) }
            )
          }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = priceStr,
            onValueChange = { priceStr = it },
            label = { Text("মূল্য (৳)") },
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = priorityStr,
            onValueChange = { priorityStr = it },
            label = { Text("প্রায়োরিটি (1-10)") },
            modifier = Modifier.weight(1f)
          )
        }

        OutlinedTextField(
          value = buttonText,
          onValueChange = { buttonText = it },
          label = { Text("অ্যাকশন বাটন টেক্সট") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank() && targetUrl.isNotBlank()) {
            val price = priceStr.toDoubleOrNull() ?: 0.0
            val prio = priorityStr.toIntOrNull() ?: 5
            val ad = initialAd?.copy(
              title = title.trim(),
              advertiserName = advertiserName.trim(),
              targetUrl = targetUrl.trim(),
              description = description.trim(),
              position = position,
              campaignType = campaignType,
              pricingType = pricingType,
              price = price,
              priority = prio,
              buttonText = buttonText.ifBlank { "ভিজিট করুন" },
              bannerImageUrl = bannerUrl
            ) ?: AdvertisementEntity(
              title = title.trim(),
              advertiserName = advertiserName.trim(),
              targetUrl = targetUrl.trim(),
              description = description.trim(),
              position = position,
              campaignType = campaignType,
              pricingType = pricingType,
              price = price,
              priority = prio,
              buttonText = buttonText.ifBlank { "ভিজিট করুন" },
              bannerImageUrl = bannerUrl
            )
            onSave(ad)
          }
        }
      ) {
        Text("সংরক্ষণ করুন")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("বাতিল") }
    }
  )
}

// ==========================================
// 2. CAMPAIGNS SUB-TAB
// ==========================================
@Composable
private fun CampaignsSubTab(
  viewModel: KajSetuViewModel,
  campaigns: List<AdCampaignEntity>,
  advertisers: List<AdvertiserEntity>,
  packages: List<AdPackageEntity>
) {
  var showCreateCampaignDialog by remember { mutableStateOf(false) }
  var campaignForPayment by remember { mutableStateOf<AdCampaignEntity?>(null) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("বিজ্ঞাপন ক্যাম্পেইন ও পেমেন্ট", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          Text("পেইড ক্যাম্পেইন পর্যবেক্ষণ ও ম্যানুয়াল পেমেন্ট ভেরিফিকেশন", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
        Button(
          onClick = { showCreateCampaignDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("নতুন ক্যাম্পেইন")
        }
      }
    }

    if (campaigns.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.ReceiptLong,
          title = "কোনো ক্যাম্পেইন নেই",
          message = "নতুন স্পনসরশিপ বা পেইড ক্যাম্পেইন যুক্ত করতে উপরের বাটনে চাপুন।"
        )
      }
    } else {
      items(campaigns) { camp ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(camp.campaignName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text("বিজ্ঞাপনদাতা: ${camp.advertiserName}", style = MaterialTheme.typography.bodySmall.copy(color = BluePrimaryDark))
              }
              Text(formatBdt(camp.price), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (camp.paymentStatus == "PAID") EmeraldGreenBg else AmberGoldBg)
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "পেমেন্ট: ${camp.paymentStatus}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (camp.paymentStatus == "PAID") EmeraldGreenDark else Color(0xFF92400E),
                    fontWeight = FontWeight.Bold
                  )
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(BlueLightBg)
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("অবস্থান: ${camp.position}", style = MaterialTheme.typography.labelSmall.copy(color = BluePrimaryDark))
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(SlateBackground)
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("স্ট্যাটাস: ${camp.status}", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
              }
            }

            if (camp.transactionId.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text("TrxID: ${camp.transactionId} • মেথড: ${camp.paymentMethod}", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (camp.paymentStatus != "PAID") {
                Button(
                  onClick = { campaignForPayment = camp },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("পেমেন্ট পেইড মার্ক করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              } else {
                Text("পেমেন্ট সম্পন্ন ✓", color = EmeraldGreenDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(
                  onClick = {
                    val nextStatus = if (camp.status == "ACTIVE") "PAUSED" else "ACTIVE"
                    viewModel.adminUpdateCampaignStatus(camp.id, nextStatus)
                  }
                ) {
                  Text(if (camp.status == "ACTIVE") "পজ করুন" else "চালু করুন", fontSize = 11.sp)
                }

                IconButton(onClick = { viewModel.adminDeleteCampaign(camp.id) }, modifier = Modifier.size(32.dp)) {
                  Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(16.dp))
                }
              }
            }
          }
        }
      }
    }
  }

  // Create Campaign Dialog
  if (showCreateCampaignDialog) {
    CreateCampaignDialog(
      advertisers = advertisers,
      packages = packages,
      onDismiss = { showCreateCampaignDialog = false },
      onSave = { camp ->
        viewModel.adminCreateCampaign(camp)
        showCreateCampaignDialog = false
      }
    )
  }

  // Payment Mark Dialog
  campaignForPayment?.let { camp ->
    MarkPaymentDialog(
      campaign = camp,
      onDismiss = { campaignForPayment = null },
      onConfirm = { method, trxId, notes ->
        viewModel.adminMarkCampaignPaid(camp.id, method, trxId, notes)
        campaignForPayment = null
      }
    )
  }
}

@Composable
private fun CreateCampaignDialog(
  advertisers: List<AdvertiserEntity>,
  packages: List<AdPackageEntity>,
  onDismiss: () -> Unit,
  onSave: (AdCampaignEntity) -> Unit
) {
  var campName by remember { mutableStateOf("") }
  var advertiserName by remember { mutableStateOf(if (advertisers.isNotEmpty()) advertisers.first().companyName else "ক্লায়েন্ট কোম্পানি") }
  var selectedPkgName by remember { mutableStateOf(if (packages.isNotEmpty()) packages.first().name else "কাস্টম প্যাক") }
  var priceStr by remember { mutableStateOf(if (packages.isNotEmpty()) packages.first().price.toString() else "500.0") }
  var position by remember { mutableStateOf("DASHBOARD") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("নতুন স্পনসরশিপ ক্যাম্পেইন", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = campName,
          onValueChange = { campName = it },
          label = { Text("ক্যাম্পেইনের নাম") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = advertiserName,
          onValueChange = { advertiserName = it },
          label = { Text("বিজ্ঞাপনদাতা / ক্লায়েন্ট") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = priceStr,
          onValueChange = { priceStr = it },
          label = { Text("নির্ধারিত মূল্য (টাকা)") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (campName.isNotBlank()) {
          onSave(
            AdCampaignEntity(
              campaignName = campName.trim(),
              advertiserName = advertiserName.trim(),
              packageName = selectedPkgName,
              price = priceStr.toDoubleOrNull() ?: 500.0,
              position = position,
              paymentStatus = "UNPAID",
              status = "ACTIVE"
            )
          )
        }
      }) {
        Text("তৈরি করুন")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("বাতিল") }
    }
  )
}

@Composable
private fun MarkPaymentDialog(
  campaign: AdCampaignEntity,
  onDismiss: () -> Unit,
  onConfirm: (method: String, trxId: String, notes: String) -> Unit
) {
  var method by remember { mutableStateOf("BKASH") }
  var trxId by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }

  val methods = listOf("BKASH" to "বিকাশ", "NAGAD" to "নগদ", "ROCKET" to "রকেট", "BANK" to "ব্যাংক", "MANUAL" to "ক্যাশ/ম্যানুয়াল")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("পেমেন্ট ভেরিফাই ও পেইড মার্ক করুন", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("ক্যাম্পেইন: ${campaign.campaignName} (৳${campaign.price})", fontWeight = FontWeight.SemiBold)

        Text("পেমেন্ট মাধ্যম নির্বাচন:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(methods) { (key, label) ->
            FilterChip(
              selected = method == key,
              onClick = { method = key },
              label = { Text(label, fontSize = 11.sp) }
            )
          }
        }

        OutlinedTextField(
          value = trxId,
          onValueChange = { trxId = it },
          label = { Text("ট্রানজেকশন আইডি (TrxID)") },
          placeholder = { Text("যেমন: BK89230194") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("পেমেন্ট সম্পর্কিত মন্তব্য / নোট") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(method, trxId.trim(), notes.trim())
        },
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
      ) {
        Text("পেইড হিসেবে নিশ্চিত করুন")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("বাতিল") }
    }
  )
}

// ==========================================
// 3. ADVERTISERS SUB-TAB
// ==========================================
@Composable
private fun AdvertisersSubTab(
  viewModel: KajSetuViewModel,
  advertisers: List<AdvertiserEntity>
) {
  var showCreateDialog by remember { mutableStateOf(false) }
  var advToEdit by remember { mutableStateOf<AdvertiserEntity?>(null) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("বিজ্ঞাপনদাতা ও ক্লায়েন্ট তালিকা", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          Text("মোট ${advertisers.size} জন ক্লায়েন্ট ও পার্টনার", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
        Button(
          onClick = { showCreateDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("নতুন বিজ্ঞাপনদাতা")
        }
      }
    }

    if (advertisers.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.Business,
          title = "কোনো বিজ্ঞাপনদাতা নেই",
          message = "নতুন কোম্পানি বা ক্লায়েন্ট যোগ করতে উপরের বাটনে চাপুন।"
        )
      }
    } else {
      items(advertisers) { adv ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(adv.companyName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (adv.status == "ACTIVE") EmeraldGreenBg else RedBg)
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(adv.status, color = if (adv.status == "ACTIVE") EmeraldGreenDark else RedError, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }

            if (adv.contactPerson.isNotBlank()) {
              Text("যোগাযোগকারী: ${adv.contactPerson}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
            if (adv.phone.isNotBlank()) {
              Text("WhatsApp: ${adv.phone}", style = MaterialTheme.typography.bodySmall.copy(color = EmeraldGreenDark))
            }
            if (adv.email.isNotBlank()) {
              Text("ইমেইল: ${adv.email}", style = MaterialTheme.typography.bodySmall.copy(color = BluePrimaryDark))
            }
            if (adv.telegramUsername.isNotBlank()) {
              Text("Telegram: @${adv.telegramUsername.replace("@", "")}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF0088CC)))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
              IconButton(onClick = { advToEdit = adv }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BluePrimary, modifier = Modifier.size(16.dp))
              }
              IconButton(onClick = { viewModel.adminDeleteAdvertiser(adv.id) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }
  }

  // Dialog for Advertiser
  if (showCreateDialog || advToEdit != null) {
    AdvertiserFormDialog(
      initialAdv = advToEdit,
      onDismiss = {
        showCreateDialog = false
        advToEdit = null
      },
      onSave = { adv ->
        if (advToEdit != null) {
          viewModel.adminUpdateAdvertiser(adv)
        } else {
          viewModel.adminCreateAdvertiser(adv)
        }
        showCreateDialog = false
        advToEdit = null
      }
    )
  }
}

@Composable
private fun AdvertiserFormDialog(
  initialAdv: AdvertiserEntity?,
  onDismiss: () -> Unit,
  onSave: (AdvertiserEntity) -> Unit
) {
  var name by remember { mutableStateOf(initialAdv?.companyName ?: "") }
  var contact by remember { mutableStateOf(initialAdv?.contactPerson ?: "") }
  var phone by remember { mutableStateOf(initialAdv?.phone ?: "") }
  var email by remember { mutableStateOf(initialAdv?.email ?: "") }
  var tg by remember { mutableStateOf(initialAdv?.telegramUsername ?: "") }
  var website by remember { mutableStateOf(initialAdv?.website ?: "") }
  var notes by remember { mutableStateOf(initialAdv?.notes ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(if (initialAdv != null) "বিজ্ঞাপনদাতা তথ্য সম্পাদনা" else "নতুন বিজ্ঞাপনদাতা যুক্ত করুন", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("কোম্পানি / ব্র্যান্ডের নাম") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("যোগাযোগকারী ব্যক্তি") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("WhatsApp নম্বর") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("ইমেইল ঠিকানা") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tg, onValueChange = { tg = it }, label = { Text("Telegram ইউজারনেম") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = website, onValueChange = { website = it }, label = { Text("ওয়েবসাইট লিংক") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("মন্তব্য / নোট") }, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      Button(onClick = {
        if (name.isNotBlank()) {
          val adv = initialAdv?.copy(
            companyName = name.trim(),
            contactPerson = contact.trim(),
            phone = phone.trim(),
            email = email.trim(),
            telegramUsername = tg.trim(),
            website = website.trim(),
            notes = notes.trim()
          ) ?: AdvertiserEntity(
            companyName = name.trim(),
            contactPerson = contact.trim(),
            phone = phone.trim(),
            email = email.trim(),
            telegramUsername = tg.trim(),
            website = website.trim(),
            notes = notes.trim()
          )
          onSave(adv)
        }
      }) {
        Text("সংরক্ষণ করুন")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("বাতিল") }
    }
  )
}

// ==========================================
// 4. PACKAGES SUB-TAB
// ==========================================
@Composable
private fun PackagesSubTab(
  viewModel: KajSetuViewModel,
  packages: List<AdPackageEntity>
) {
  var showCreateDialog by remember { mutableStateOf(false) }
  var pkgToEdit by remember { mutableStateOf<AdPackageEntity?>(null) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("বিজ্ঞাপন প্যাকেজসমূহ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          Text("প্যাকেজের মূল্য, মেয়াদ ও অবস্থান ব্যবস্থাপনা", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }
        Button(
          onClick = { showCreateDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("নতুন প্যাকেজ")
        }
      }
    }

    items(packages) { pkg ->
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
              Text(pkg.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
              Text("মেয়াদ: ${pkg.durationDays} দিন • অবস্থান: ${pkg.position}", style = MaterialTheme.typography.bodySmall.copy(color = BluePrimary))
            }
            Text(formatBdt(pkg.price), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark))
          }

          if (pkg.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(pkg.description, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text("সুবিধাসমূহ: ${pkg.features}", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Switch(
                checked = pkg.isActive,
                onCheckedChange = { viewModel.adminTogglePackage(pkg.id, it) }
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(if (pkg.isActive) "সক্রিয়" else "নিষ্ক্রিয়", fontSize = 12.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              IconButton(onClick = { pkgToEdit = pkg }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BluePrimary, modifier = Modifier.size(16.dp))
              }
              IconButton(onClick = { viewModel.adminDeletePackage(pkg.id) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }
  }

  if (showCreateDialog || pkgToEdit != null) {
    PackageFormDialog(
      initialPkg = pkgToEdit,
      onDismiss = {
        showCreateDialog = false
        pkgToEdit = null
      },
      onSave = { pkg ->
        if (pkgToEdit != null) {
          viewModel.adminUpdatePackage(pkg)
        } else {
          viewModel.adminCreatePackage(pkg)
        }
        showCreateDialog = false
        pkgToEdit = null
      }
    )
  }
}

@Composable
private fun PackageFormDialog(
  initialPkg: AdPackageEntity?,
  onDismiss: () -> Unit,
  onSave: (AdPackageEntity) -> Unit
) {
  var name by remember { mutableStateOf(initialPkg?.name ?: "") }
  var durationStr by remember { mutableStateOf(initialPkg?.durationDays?.toString() ?: "7") }
  var priceStr by remember { mutableStateOf(initialPkg?.price?.toString() ?: "500.0") }
  var position by remember { mutableStateOf(initialPkg?.position ?: "DASHBOARD") }
  var description by remember { mutableStateOf(initialPkg?.description ?: "") }
  var features by remember { mutableStateOf(initialPkg?.features ?: "ব্যানার বিজ্ঞাপন, ক্লিক ট্র্যাকিং, টার্গেটেড ইউজার") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(if (initialPkg != null) "প্যাকেজ সম্পাদনা" else "নতুন বিজ্ঞাপন প্যাকেজ", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("প্যাকেজের নাম") }, modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = durationStr, onValueChange = { durationStr = it }, label = { Text("মেয়াদ (দিন)") }, modifier = Modifier.weight(1f))
          OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("মূল্য (৳)") }, modifier = Modifier.weight(1f))
        }
        OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("অবস্থান (Position)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("সংক্ষিপ্ত বিবরণ") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = features, onValueChange = { features = it }, label = { Text("ফিচার ও সুবিধাসমূহ") }, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      Button(onClick = {
        if (name.isNotBlank()) {
          val pkg = initialPkg?.copy(
            name = name.trim(),
            durationDays = durationStr.toIntOrNull() ?: 7,
            price = priceStr.toDoubleOrNull() ?: 500.0,
            position = position.trim(),
            description = description.trim(),
            features = features.trim()
          ) ?: AdPackageEntity(
            name = name.trim(),
            durationDays = durationStr.toIntOrNull() ?: 7,
            price = priceStr.toDoubleOrNull() ?: 500.0,
            position = position.trim(),
            description = description.trim(),
            features = features.trim()
          )
          onSave(pkg)
        }
      }) {
        Text("সংরক্ষণ করুন")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("বাতিল") }
    }
  )
}

// ==========================================
// 5. REVENUE & ANALYTICS SUB-TAB
// ==========================================
@Composable
private fun RevenueAnalyticsSubTab(
  ads: List<AdvertisementEntity>,
  campaigns: List<AdCampaignEntity>,
  advertisers: List<AdvertiserEntity>
) {
  val paidCampaigns = campaigns.filter { it.paymentStatus == "PAID" }
  val totalRevenue = paidCampaigns.sumOf { it.price }
  val pendingRevenue = campaigns.filter { it.paymentStatus != "PAID" }.sumOf { it.price }
  val totalClicks = ads.sumOf { it.clickCount }
  val totalImpressions = ads.sumOf { it.impressionCount }
  val overallCtr = if (totalImpressions > 0) String.format("%.2f", (totalClicks.toDouble() / totalImpressions.toDouble()) * 100.0) else "0.00"

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Text("বিজ্ঞাপন রাজস্ব ও পারফরম্যান্স অ্যানালিটিক্স", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }

    // Revenue Cards Grid
    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
          title = "মোট বিজ্ঞাপন আয়",
          value = formatBdt(totalRevenue),
          icon = Icons.Default.MonetizationOn,
          iconColor = EmeraldGreen,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "পেন্ডিং পেমেন্ট",
          value = formatBdt(pendingRevenue),
          icon = Icons.Default.Pending,
          iconColor = RedError,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatCard(
          title = "মোট ইমপ্রেশন",
          value = "$totalImpressions বার",
          icon = Icons.Default.Visibility,
          iconColor = BluePrimary,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "মোট ক্লিক ও CTR",
          value = "$totalClicks ($overallCtr%)",
          icon = Icons.Default.TouchApp,
          iconColor = AmberGold,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Top Performing Ads
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("শীর্ষ পারফর্মিং বিজ্ঞাপন (Top Performing Ads)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(10.dp))

          val topAds = ads.sortedByDescending { it.clickCount }.take(5)
          if (topAds.isEmpty()) {
            Text("কোনো ডেটা নেই", color = TextMuted, fontSize = 12.sp)
          } else {
            topAds.forEachIndexed { idx, ad ->
              val ctr = if (ad.impressionCount > 0) String.format("%.2f", (ad.clickCount.toDouble() / ad.impressionCount.toDouble()) * 100.0) else "0.00"
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("#${idx + 1}", fontWeight = FontWeight.Bold, color = BluePrimary, fontSize = 13.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(ad.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("${ad.position} • ${ad.advertiserName}", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                  }
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text("${ad.clickCount} ক্লিক", fontWeight = FontWeight.Bold, color = EmeraldGreenDark, fontSize = 13.sp)
                  Text("CTR $ctr%", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                }
              }
              if (idx < topAds.size - 1) Divider(color = SlateBackground)
            }
          }
        }
      }
    }
  }
}

// ==========================================
// 6. AD SETTINGS SUB-TAB
// ==========================================
@Composable
private fun AdSettingsSubTab(
  viewModel: KajSetuViewModel,
  currentSettings: AdContactSettingsEntity?
) {
  var adsEnabled by remember(currentSettings) { mutableStateOf(currentSettings?.adsEnabled ?: true) }
  var whatsappNumber by remember(currentSettings) { mutableStateOf(currentSettings?.whatsappNumber ?: "+8801712345678") }
  var whatsappMessage by remember(currentSettings) { mutableStateOf(currentSettings?.whatsappMessage ?: "হ্যালো, আমি কাজসেতু প্ল্যাটফর্মে বিজ্ঞাপন ও স্পন্সরশিপ দিতে আগ্রহী।") }
  var email by remember(currentSettings) { mutableStateOf(currentSettings?.email ?: "sponsor@kajsetu.com") }
  var emailSubject by remember(currentSettings) { mutableStateOf(currentSettings?.emailSubject ?: "বিজ্ঞাপন প্রচার সংক্রান্ত অনুসন্ধান - কাজসেতু") }
  var telegramLink by remember(currentSettings) { mutableStateOf(currentSettings?.telegramLink ?: "https://t.me/KajSetuOfficial") }
  var defaultPosition by remember(currentSettings) { mutableStateOf(currentSettings?.defaultAdPosition ?: "DASHBOARD") }
  var rotationMode by remember(currentSettings) { mutableStateOf(currentSettings?.rotationMode ?: "PRIORITY") }
  var impressionTracking by remember(currentSettings) { mutableStateOf(currentSettings?.impressionTrackingEnabled ?: true) }
  var clickTracking by remember(currentSettings) { mutableStateOf(currentSettings?.clickTrackingEnabled ?: true) }
  var adLabel by remember(currentSettings) { mutableStateOf(currentSettings?.adLabelText ?: "Sponsored") }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Text("বিজ্ঞাপন ও স্পন্সরশিপ সিস্টেম সেটিংস", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
      Text("রিয়েল-টাইম কনফিগারেশন - ডাটাবেজে সংরক্ষণ ও ইনস্ট্যান্ট আপডেট", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
    }

    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Master Toggle
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
              Text("বিজ্ঞাপন ডিসপ্লে সক্রিয় (Master Enable/Disable)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("সাইটজুড়ে বিজ্ঞাপন দেখানো চালু বা বন্ধ রাখুন", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
            }
            Switch(checked = adsEnabled, onCheckedChange = { adsEnabled = it })
          }

          Divider(color = CardBorder)

          // WhatsApp Configuration
          OutlinedTextField(
            value = whatsappNumber,
            onValueChange = { whatsappNumber = it },
            label = { Text("WhatsApp নম্বর (কান্ট্রি কোড সহ)") },
            leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = EmeraldGreenDark) },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = whatsappMessage,
            onValueChange = { whatsappMessage = it },
            label = { Text("WhatsApp প্রি-ফিল্ড বার্তা (Pre-filled Message)") },
            modifier = Modifier.fillMaxWidth()
          )

          Divider(color = CardBorder)

          // Email Configuration
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("বিজ্ঞাপন সাপোর্ট ইমেইল (Email Address)") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BluePrimary) },
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = emailSubject,
            onValueChange = { emailSubject = it },
            label = { Text("ইমেইল বিষয় (Email Subject)") },
            modifier = Modifier.fillMaxWidth()
          )

          Divider(color = CardBorder)

          // Telegram Configuration
          OutlinedTextField(
            value = telegramLink,
            onValueChange = { telegramLink = it },
            label = { Text("Telegram লিংক বা ইউজারনেম (Telegram Link/Username)") },
            leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF0088CC)) },
            modifier = Modifier.fillMaxWidth()
          )

          Divider(color = CardBorder)

          // Label and Rotation
          OutlinedTextField(
            value = adLabel,
            onValueChange = { adLabel = it },
            label = { Text("বিজ্ঞাপন লেবেল টেক্সট (যেমন: Sponsored / বিজ্ঞাপন)") },
            modifier = Modifier.fillMaxWidth()
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("ইমপ্রেশন ট্র্যাকিং সক্রিয়", fontSize = 13.sp)
            Switch(checked = impressionTracking, onCheckedChange = { impressionTracking = it })
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("ক্লিক ট্র্যাকিং ও এন্টি-ফ্রড সক্রিয়", fontSize = 13.sp)
            Switch(checked = clickTracking, onCheckedChange = { clickTracking = it })
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              viewModel.adminUpdateAdContactSettings(
                AdContactSettingsEntity(
                  adsEnabled = adsEnabled,
                  whatsappNumber = whatsappNumber.trim(),
                  whatsappMessage = whatsappMessage.trim(),
                  email = email.trim(),
                  emailSubject = emailSubject.trim(),
                  telegramLink = telegramLink.trim(),
                  defaultAdPosition = defaultPosition,
                  rotationMode = rotationMode,
                  impressionTrackingEnabled = impressionTracking,
                  clickTrackingEnabled = clickTracking,
                  adLabelText = adLabel.trim()
                )
              )
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            modifier = Modifier.fillMaxWidth().height(44.dp)
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("সব সেটিংস সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// ==========================================
// 7. AD AUDIT LOGS SUB-TAB
// ==========================================
@Composable
private fun AdAuditLogsSubTab(logs: List<AdAuditLogEntity>) {
  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Text("বিজ্ঞাপন ও স্পন্সরশিপ অডিট লগ", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
      Text("অ্যাডমিনের প্রতিটি অ্যাকশন, পরিবর্তন ও পেমেন্ট রেকর্ড", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
    }

    if (logs.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.History,
          title = "কোনো অডিট লগ নেই",
          message = "বিজ্ঞাপন ব্যবস্থাপনা সম্পর্কিত সব কার্যক্রম এখানে রেকর্ড হবে।"
        )
      }
    } else {
      items(logs) { log ->
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(log.action, fontWeight = FontWeight.Bold, color = BluePrimaryDark, style = MaterialTheme.typography.labelMedium)
              Text(formatDateTime(log.timestamp), style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("টার্গেট: ${log.target}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
            if (log.newValue.isNotBlank()) {
              Text("নতুন মান: ${log.newValue}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
            Text("সম্পাদনকারী অ্যাডমিন: ${log.adminUsername}", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
          }
        }
      }
    }
  }
}
