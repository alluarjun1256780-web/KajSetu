package com.example.ui.screens

import android.content.Context
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.entity.TaskEntity
import com.example.data.entity.TaskSubmissionEntity
import com.example.ui.KajSetuViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TasksScreen(
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit,
  onBack: () -> Unit
) {
  val tasks by viewModel.activeTasks.collectAsState()
  val user by viewModel.currentUser.collectAsState()
  val submissions by viewModel.userSubmissions.collectAsState()
  val activeAds by viewModel.activeAds.collectAsState()
  val adSettings by viewModel.adContactSettings.collectAsState()
  val context = LocalContext.current

  var selectedCategory by remember { mutableStateOf("সকল") }
  var selectedTaskForPopup by remember { mutableStateOf<TaskEntity?>(null) }

  // Set of task IDs already submitted by this user
  val submittedTaskIds = remember(submissions) { submissions.map { it.taskId }.toSet() }

  val categories = listOf("সকল", "সোশ্যাল মিডিয়া", "রিভিউ ও রেটিং", "অ্যাপ রিভিউ", "কমিউনিটি", "সাইন আপ")
  val filteredTasks = if (selectedCategory == "সকল") tasks else tasks.filter { it.category == selectedCategory }
  val betweenTaskAd = activeAds.firstOrNull { it.position == "BETWEEN_TASKS" } ?: activeAds.firstOrNull()

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "টাস্ক ও ইনকাম",
        subtitle = "${filteredTasks.size}টি কাজ সক্রিয় রয়েছে",
        showBack = true,
        onBackClick = onBack,
        actions = {
          TextButton(onClick = { onNavigate(Screen.MyTasks) }) {
            Text("আমার কাজ (${submissions.size})", color = BluePrimary, fontWeight = FontWeight.Bold)
          }
        }
      )
    },
    bottomBar = {
      UserBottomNav(currentScreen = Screen.Tasks, onNavigate = onNavigate)
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
      // Inactive warning if applicable
      if (user?.accountStatus != "active") {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AmberGoldBg),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AmberGold)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = RedError)
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "অ্যাকাউন্ট অ্যাক্টিভেশন জরুরি!",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                )
                Text(
                  text = "একাউন্ট একটিভ না করে কোনো ইউজার মাইক্রো টাস্ক জব বা কাজ করতে পারবেন না। আগে একাউন্ট একটিভ করুন।",
                  style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F), lineHeight = 18.sp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = { onNavigate(Screen.Activation) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
              ) {
                Text("সক্রিয় করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Tutorial Video Banner
      item {
        ScreenTutorialBanner(
          title = "মাইক্রো টাস্কের কাজ করার ভিডিও টিউটোরিয়াল",
          subtitle = "ভিডিও দেখে টাস্ক সম্পন্ন ও প্রমাণ সাবমিট করা শিখুন",
          onWatchVideo = {
            viewModel.openExternalUrl(
              context,
              "https://www.youtube.com/results?search_query=micro+task+earning+bangla+tutorial"
            )
          },
          onViewAllTutorials = { onNavigate(Screen.Tutorials) }
        )
      }

      // Categories Horizontal Row
      item {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(categories) { cat ->
            val isSelected = cat == selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = BluePrimary,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(20.dp)
            )
          }
        }
      }

      if (filteredTasks.isEmpty()) {
        item {
          EmptyStateView(
            icon = Icons.Default.Task,
            title = "কোনো কাজ পাওয়া যায়নি",
            message = "এই ক্যাটাগরিতে বর্তমানে কোনো সক্রিয় কাজ নেই। অন্য ক্যাটাগরি দেখুন।"
          )
        }
      } else {
        items(filteredTasks.take(2)) { task ->
          val isSubmitted = submittedTaskIds.contains(task.id)
          val isSlotFull = task.currentSubmissions >= task.maxSubmissions
          TaskCard(
            task = task,
            isSubmitted = isSubmitted,
            isSlotFull = isSlotFull,
            onActionClick = { selectedTaskForPopup = task }
          )
        }

        // Sponsored Ad banner in between tasks
        if (betweenTaskAd != null) {
          item {
            SponsoredAdBanner(
              ad = betweenTaskAd,
              onAdClick = { viewModel.openAdTarget(context, betweenTaskAd) },
              onImpression = { viewModel.recordAdImpression(betweenTaskAd.id) },
              labelText = adSettings?.adLabelText ?: "Sponsored"
            )
          }
        }

        items(filteredTasks.drop(2)) { task ->
          val isSubmitted = submittedTaskIds.contains(task.id)
          val isSlotFull = task.currentSubmissions >= task.maxSubmissions
          TaskCard(
            task = task,
            isSubmitted = isSubmitted,
            isSlotFull = isSlotFull,
            onActionClick = { selectedTaskForPopup = task }
          )
        }

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
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse(task.taskUrl))
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
fun TaskCard(
  task: TaskEntity,
  isSubmitted: Boolean,
  isSlotFull: Boolean,
  onActionClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onActionClick() }
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BlueLightBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = task.category,
            style = MaterialTheme.typography.labelSmall.copy(color = BluePrimaryDark, fontWeight = FontWeight.SemiBold)
          )
        }

        Text(
          text = formatBdt(task.rewardAmount),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = EmeraldGreenDark
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = task.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = task.description,
        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Group, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "স্লট: ${task.currentSubmissions}/${task.maxSubmissions}" + if (isSlotFull) " (স্লট শেষ)" else "",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isSlotFull) RedError else TextSecondary,
              fontWeight = if (isSlotFull) FontWeight.Bold else FontWeight.Normal
            )
          )
        }

        // Action button reflecting task state
        when {
          isSubmitted -> {
            Button(
              onClick = onActionClick,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE2E8F0),
                contentColor = TextSecondary
              ),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldGreenDark)
              Spacer(modifier = Modifier.width(4.dp))
              Text("জমা দেওয়া হয়েছে", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          }
          isSlotFull -> {
            Button(
              onClick = {},
              enabled = false,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color(0xFFF1F5F9),
                disabledContentColor = TextMuted
              ),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("স্লট শেষ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
          else -> {
            Button(
              onClick = onActionClick,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              modifier = Modifier.testTag("task_view_button_${task.id}")
            ) {
              Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("টাস্ক দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun TaskDetailsModal(
  task: TaskEntity,
  isSubmitted: Boolean,
  isSlotFull: Boolean,
  userAccountStatus: String,
  onDismiss: () -> Unit,
  onVisitLink: () -> Unit,
  onSubmitProof: (proofText: String, screenshotUri: String) -> Unit,
  onNavigateToActivation: () -> Unit
) {
  var proofText by remember { mutableStateOf("") }
  var selectedScreenshotUri by remember { mutableStateOf("") }

  // Android Photo Picker
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      selectedScreenshotUri = uri.toString()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceCard),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.90f)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Modal Top Bar with Close Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(BlueLightBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(task.category, color = BluePrimaryDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = formatBdt(task.rewardAmount),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scrollable Body
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Task Title
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
          )

          // Short Description
          Text(
            text = task.description,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
          )

          // Slot Progress Badge
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SlateBackground)
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "স্লট অবস্থা: ${task.currentSubmissions}/${task.maxSubmissions} সম্পন্ন",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary)
            )
            Text(
              text = "সময়সীমা: ৩ দিন",
              style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
          }

          // ১. কাজের লিংক (Visit Link Button)
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BlueLightBg),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "🔗 কাজের লিংক (Task Link)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "কাজ শুরু করার জন্য নিচের বাটনে ক্লিক করে মূল লিংকে প্রবেশ করুন:",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = onVisitLink,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                modifier = Modifier.fillMaxWidth().height(42.dp)
              ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("কাজের লিংকে যান (Visit Link)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
              }
            }
          }

          // ২. কাজের নিয়মাবলী (Instructions)
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "📋 কাজ করার নিয়মাবলী (Instructions):",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = task.instructions,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp)
              )
            }
          }

          // ৩. বায়ারের চাওয়া প্রমাণ সম্পর্কিত নির্দেশনা
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(AmberGoldBg)
              .padding(12.dp)
          ) {
            Column {
              Text(
                text = "⚠️ বায়ারের চাওয়া প্রমাণের নির্দেশনা:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = task.proofType,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB45309), fontWeight = FontWeight.Medium)
              )
            }
          }

          Divider(color = CardBorder)

          // ৪. প্রুফ সাবমিশন ফর্ম (Proof Submission Form)
          Text(
            text = "✍️ প্রুফ সাবমিশন ফর্ম (Submit Proof)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
          )

          when {
            isSubmitted -> {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(EmeraldGreenBg)
                  .padding(14.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreenDark)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "আপনি ইতোমধ্যে এই টাস্কের প্রুফ জমা দিয়েছেন!",
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
                    )
                    Text(
                      text = "আপনার সাবমিশন অ্যাডমিন পর্যালোচনায় রয়েছে। অনুমোদিত হলে স্বয়ংক্রিয়ভাবে ব্যালেন্সে রিওয়ার্ড যোগ হবে।",
                      style = MaterialTheme.typography.bodySmall.copy(color = EmeraldGreenDark.copy(alpha = 0.8f))
                    )
                  }
                }
              }
            }
            isSlotFull -> {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(RedBg)
                  .padding(14.dp)
              ) {
                Text(
                  text = "দুঃখিত! এই কাজের নির্ধারিত সব স্লট পূর্ণ হয়ে গেছে (Slot Full)। অন্য কাজ চেষ্টা করুন।",
                  style = MaterialTheme.typography.bodySmall.copy(color = RedError, fontWeight = FontWeight.Bold)
                )
              }
            }
            userAccountStatus != "active" -> {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(AmberGoldBg)
                  .padding(14.dp)
              ) {
                Column {
                  Text(
                    text = "প্রুফ জমা দিতে অ্যাকাউন্ট সক্রিয় করা আবশ্যক!",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Button(
                    onClick = onNavigateToActivation,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                  ) {
                    Text("অ্যাকাউন্ট সক্রিয় করুন")
                  }
                }
              }
            }
            else -> {
              // Active Submission Form
              OutlinedTextField(
                value = proofText,
                onValueChange = { proofText = it },
                label = { Text("প্রুফ বিবরণ (ইউজারনেম / মন্তব্য বিবরণ)") },
                placeholder = { Text("যেমন: আমার ইউটিউব নাম: RahimTech, ভিডিওতে লাইক ও কমেন্ট করেছি...") },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(110.dp)
                  .testTag("modal_proof_text_input"),
                shape = RoundedCornerShape(10.dp)
              )

              // File Attachment: Screenshot Upload
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SlateBackground),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = "📸 কাজের স্ক্রিনশট ফাইল (Screenshot File):",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                  )
                  Spacer(modifier = Modifier.height(6.dp))

                  if (selectedScreenshotUri.isNotBlank()) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldGreenBg)
                        .padding(8.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = EmeraldGreenDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = "স্ক্রিনশট ফাইল সিলেক্টেড (Image Attached)",
                          style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreenDark, fontWeight = FontWeight.Bold),
                          maxLines = 1
                        )
                      }
                      IconButton(onClick = { selectedScreenshotUri = "" }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = RedError, modifier = Modifier.size(16.dp))
                      }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                  }

                  OutlinedButton(
                    onClick = {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                  ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (selectedScreenshotUri.isNotBlank()) "স্ক্রিনশট পরিবর্তন করুন" else "গ্যালারি থেকে স্ক্রিনশট আপলোড করুন",
                      fontSize = 12.sp
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              // Submit Proof Button
              Button(
                onClick = {
                  onSubmitProof(proofText, selectedScreenshotUri)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("modal_submit_proof_button")
              ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("প্রুফ জমা দিন", fontWeight = FontWeight.Bold, fontSize = 15.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun TaskDetailScreen(
  taskId: String,
  viewModel: KajSetuViewModel,
  onNavigate: (Screen) -> Unit,
  onBack: () -> Unit
) {
  val tasks by viewModel.activeTasks.collectAsState()
  val user by viewModel.currentUser.collectAsState()
  val submissions by viewModel.userSubmissions.collectAsState()
  val context = LocalContext.current
  val task = tasks.find { it.id == taskId }

  val isSubmitted = remember(submissions, taskId) { submissions.any { it.taskId == taskId } }
  val isSlotFull = task != null && task.currentSubmissions >= task.maxSubmissions

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "কাজের বিবরণ ও প্রুফ জমা",
        showBack = true,
        onBackClick = onBack
      )
    }
  ) { padding ->
    if (task == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text("কাজটি পাওয়া যায়নি।")
      }
    } else {
      TaskDetailsModal(
        task = task,
        isSubmitted = isSubmitted,
        isSlotFull = isSlotFull,
        userAccountStatus = user?.accountStatus ?: "inactive",
        onDismiss = onBack,
        onVisitLink = {
          try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(task.taskUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
            onComplete = onBack
          )
        },
        onNavigateToActivation = {
          onNavigate(Screen.Activation)
        }
      )
    }
  }
}

@Composable
fun MyTasksScreen(
  viewModel: KajSetuViewModel,
  onBack: () -> Unit
) {
  val submissions by viewModel.userSubmissions.collectAsState()

  Scaffold(
    topBar = {
      KajSetuTopBar(
        title = "আমার সম্পন্নকৃত কাজ",
        subtitle = "মোট ${submissions.size}টি সাবমিশন",
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
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      if (submissions.isEmpty()) {
        item {
          EmptyStateView(
            icon = Icons.Default.AssignmentLate,
            title = "কোনো কাজ সাবমিট করা হয়নি",
            message = "আপনি এখনো কোনো মাইক্রো টাস্ক সম্পন্ন করেননি। কাজ শুরু করতে ব্রাউজ করুন।"
          )
        }
      } else {
        items(submissions) { sub ->
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
                StatusPill(status = sub.status)
                Text(
                  text = formatBdt(sub.rewardAmount),
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BluePrimaryDark)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = sub.taskTitle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "জমা দেওয়া প্রমাণ: ${sub.proofData}",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                maxLines = 2
              )

              if (sub.screenshotUri.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.AttachFile, contentDescription = null, tint = EmeraldGreenDark, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "স্ক্রিনশট ফাইল সংযুক্ত",
                    style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreenDark, fontWeight = FontWeight.Bold)
                  )
                }
              }

              if (sub.status == "REJECTED" && sub.rejectionReason.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(RedBg)
                    .padding(8.dp)
                ) {
                  Text(
                    text = "বাতিলের কারণ: ${sub.rejectionReason}",
                    style = MaterialTheme.typography.labelSmall.copy(color = RedError, fontWeight = FontWeight.SemiBold)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "সাবমিটের সময়: " + formatDateTime(sub.submittedAt),
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
              )
            }
          }
        }
      }
    }
  }
}
