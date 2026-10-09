package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AuthResult
import com.example.data.KajSetuRepository
import com.example.data.OperationResult
import com.example.data.entity.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
  object Landing : Screen()
  object Auth : Screen()
  object Dashboard : Screen()
  object Tasks : Screen()
  data class TaskDetail(val taskId: String) : Screen()
  object MyTasks : Screen()
  object Wallet : Screen()
  object Deposit : Screen()
  object Withdraw : Screen()
  object Activation : Screen()
  object Referral : Screen()
  object Profile : Screen()
  object Marketplace : Screen()
  object Notifications : Screen()
  object Support : Screen()
  object Policies : Screen()
  object AdminPortal : Screen()
  object Advertise : Screen()
  object GmailSell : Screen()
  object RedeemBonus : Screen()
  object Tutorials : Screen()
}

class KajSetuViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: KajSetuRepository

  init {
    val db = AppDatabase.getDatabase(application)
    val firestoreService = com.example.data.firestore.FirestoreTaskService(application)
    repository = KajSetuRepository(db, firestoreService)
  }

  // Navigation State
  private val _currentScreen = MutableStateFlow<Screen>(Screen.Landing)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  // Navigation History for Back Handler
  private val backStack = mutableListOf<Screen>()

  fun navigateTo(screen: Screen) {
    backStack.add(_currentScreen.value)
    _currentScreen.value = screen
  }

  fun navigateBack(): Boolean {
    return if (backStack.isNotEmpty()) {
      _currentScreen.value = backStack.removeAt(backStack.size - 1)
      true
    } else {
      false
    }
  }

  // Current Logged-in User
  private val _currentUserId = MutableStateFlow<String?>("user_rahim_001") // Start seeded with Rahim for immediate rich preview
  val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

  val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUser(id) else flowOf(null)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val currentWallet: StateFlow<WalletEntity?> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeWallet(id) else flowOf(null)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val siteSettings: StateFlow<SiteSettingsEntity?> = repository.observeSiteSettings()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SiteSettingsEntity())

  val activeTasks: StateFlow<List<TaskEntity>> = repository.observeActiveTasks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userSubmissions: StateFlow<List<TaskSubmissionEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUserSubmissions(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userTransactions: StateFlow<List<TransactionEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeTransactions(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userDeposits: StateFlow<List<DepositEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUserDeposits(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userWithdrawals: StateFlow<List<WithdrawalEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUserWithdrawals(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userNotifications: StateFlow<List<NotificationEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeNotifications(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val unreadNotificationCount: StateFlow<Int> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUnreadNotificationCount(id) else flowOf(0)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val userTickets: StateFlow<List<SupportTicketEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUserTickets(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val referrals: StateFlow<List<UserEntity>> = currentUser.flatMapLatest { user ->
    if (user != null) repository.observeReferrals(user.referralCode) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val marketplaceServices: StateFlow<List<MarketplaceServiceEntity>> = repository.observeMarketplaceServices()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allAds: StateFlow<List<AdvertisementEntity>> = repository.observeAllAds()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeAds: StateFlow<List<AdvertisementEntity>> = repository.observeActiveAds()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val advertisers: StateFlow<List<AdvertiserEntity>> = repository.observeAllAdvertisers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeAdvertisers: StateFlow<List<AdvertiserEntity>> = repository.observeActiveAdvertisers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val adPackages: StateFlow<List<AdPackageEntity>> = repository.observeAllPackages()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeAdPackages: StateFlow<List<AdPackageEntity>> = repository.observeActivePackages()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val adCampaigns: StateFlow<List<AdCampaignEntity>> = repository.observeAllCampaigns()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeAdCampaigns: StateFlow<List<AdCampaignEntity>> = repository.observeActiveCampaigns()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val adContactSettings: StateFlow<AdContactSettingsEntity?> = repository.observeAdContactSettings()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val adAuditLogs: StateFlow<List<AdAuditLogEntity>> = repository.observeAdAuditLogs()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Admin Observations
  val allUsers: StateFlow<List<UserEntity>> = repository.observeAllUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTasks: StateFlow<List<TaskEntity>> = repository.observeAllTasks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingSubmissions: StateFlow<List<TaskSubmissionEntity>> = repository.observePendingSubmissions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingDeposits: StateFlow<List<DepositEntity>> = repository.observePendingDeposits()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingWithdrawals: StateFlow<List<WithdrawalEntity>> = repository.observePendingWithdrawals()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingActivations: StateFlow<List<ActivationRequestEntity>> = repository.observePendingActivations()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userGmailSubmissions: StateFlow<List<GmailSubmissionEntity>> = currentUser.flatMapLatest { user ->
    if (user != null) repository.observeUserGmailSubmissions(user.id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingGmailSubmissions: StateFlow<List<GmailSubmissionEntity>> = repository.observePendingGmailSubmissions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allGmailSubmissions: StateFlow<List<GmailSubmissionEntity>> = repository.observeAllGmailSubmissions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTickets: StateFlow<List<SupportTicketEntity>> = repository.observeAllTickets()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val auditLogs: StateFlow<List<AuditLogEntity>> = repository.observeAuditLogs()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTransactions: StateFlow<List<TransactionEntity>> = repository.observeAllTransactions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allRedeemCodes: StateFlow<List<com.example.data.entity.RedeemCodeEntity>> = repository.observeAllRedeemCodes()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val userRedeemUsages: StateFlow<List<com.example.data.entity.RedeemCodeUsageEntity>> = _currentUserId.flatMapLatest { id ->
    if (id != null) repository.observeUserRedeemUsages(id) else flowOf(emptyList())
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalUsersCount: StateFlow<Int> = repository.observeTotalUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val activeUsersCount: StateFlow<Int> = repository.observeActiveUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  // Status message for Snackbars
  private val _snackbarMessage = MutableSharedFlow<String>()
  val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

  fun showMessage(msg: String) {
    viewModelScope.launch {
      _snackbarMessage.emit(msg)
    }
  }

  // Auth Operations
  fun login(identifier: String, pass: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
      when (val res = repository.loginUser(identifier, pass)) {
        is AuthResult.Success -> {
          _currentUserId.value = res.user.id
          showMessage("স্বাগতম, ${res.user.fullName}!")
          if (res.user.role == "admin") {
            navigateTo(Screen.AdminPortal)
          } else {
            navigateTo(Screen.Dashboard)
          }
          onSuccess()
        }
        is AuthResult.Error -> {
          showMessage(res.message)
        }
      }
    }
  }

  fun register(
    name: String,
    username: String,
    email: String,
    phone: String,
    pass: String,
    confirmPass: String,
    refCode: String,
    onSuccess: () -> Unit
  ) {
    viewModelScope.launch {
      when (val res = repository.registerUser(name, username, email, phone, pass, confirmPass, refCode)) {
        is AuthResult.Success -> {
          _currentUserId.value = res.user.id
          showMessage("রেজিস্ট্রেশন সফল! স্বাগতম কাজসেতুতে।")
          if (res.user.role == "admin") {
            navigateTo(Screen.AdminPortal)
          } else {
            navigateTo(Screen.Dashboard)
          }
          onSuccess()
        }
        is AuthResult.Error -> {
          showMessage(res.message)
        }
      }
    }
  }

  fun logout() {
    _currentUserId.value = null
    backStack.clear()
    _currentScreen.value = Screen.Landing
    showMessage("লগআউট সম্পন্ন হয়েছে।")
  }

  fun switchUser(userId: String) {
    _currentUserId.value = userId
    viewModelScope.launch {
      // Small delay for flow update
      val user = currentUser.value
      if (user?.role == "admin") {
        navigateTo(Screen.AdminPortal)
      } else {
        navigateTo(Screen.Dashboard)
      }
    }
  }

  // User Actions
  fun submitActivation(method: String, senderNumber: String, trxId: String) {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      when (val res = repository.submitActivationPayment(uid, method, senderNumber, trxId)) {
        is OperationResult.Success -> {
          showMessage(res.message)
          navigateBack()
        }
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun submitGmailForSale(
    gmail: String,
    pass: String,
    recovery: String,
    notes: String,
    onSuccess: () -> Unit = {}
  ) {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      when (val res = repository.submitGmailForSale(uid, gmail, pass, recovery, notes)) {
        is OperationResult.Success -> {
          showMessage(res.message)
          onSuccess()
        }
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun submitTaskProof(
    taskId: String,
    proof: String,
    screenshotUri: String = "",
    onComplete: () -> Unit = {}
  ) {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      when (val res = repository.submitTaskProof(taskId, uid, proof, screenshotUri)) {
        is OperationResult.Success -> {
          showMessage(res.message)
          onComplete()
        }
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun requestDeposit(method: String, senderNumber: String, trxId: String, amount: Double) {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      when (val res = repository.requestDeposit(uid, method, senderNumber, trxId, amount)) {
        is OperationResult.Success -> {
          showMessage(res.message)
          navigateBack()
        }
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun requestWithdrawal(method: String, receiverNumber: String, amount: Double) {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      when (val res = repository.requestWithdrawal(uid, method, receiverNumber, amount)) {
        is OperationResult.Success -> {
          showMessage(res.message)
          navigateBack()
        }
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun createSupportTicket(subject: String, category: String, message: String) {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      when (val res = repository.createSupportTicket(uid, subject, category, message)) {
        is OperationResult.Success -> {
          showMessage(res.message)
          navigateBack()
        }
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun markAllNotificationsRead() {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      repository.markAllNotificationsRead(uid)
      showMessage("সকল নোটিফিকেশন পড়া হিসেবে চিহ্নিত হয়েছে।")
    }
  }

  // Admin Actions
  fun adminApproveActivation(reqId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminApproveActivation(reqId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminRejectActivation(reqId: String, reason: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminRejectActivation(reqId, admin, reason)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminApproveGmail(subId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminApproveGmail(subId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminRejectGmail(subId: String, reason: String = "") {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminRejectGmail(subId, reason, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminReviewSubmission(subId: String, isApproved: Boolean, reason: String = "") {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminReviewSubmission(subId, admin, isApproved, reason)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminApproveDeposit(depId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminApproveDeposit(depId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminRejectDeposit(depId: String, note: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminRejectDeposit(depId, admin, note)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminApproveWithdrawal(withdrId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminApproveWithdrawal(withdrId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminRejectWithdrawal(withdrId: String, reason: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminRejectWithdrawal(withdrId, admin, reason)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminCreateTask(task: TaskEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminCreateTask(task, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminUpdateTask(task: TaskEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminUpdateTask(task, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminDeleteTask(taskId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminDeleteTask(taskId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminToggleTask(taskId: String, newStatus: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminToggleTaskStatus(taskId, newStatus, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminUpdateUserStatus(userId: String, status: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminUpdateUserStatus(userId, status, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminUpdateSettings(settings: SiteSettingsEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.updateSiteSettings(settings, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminReplyTicket(ticketId: String, reply: String, newStatus: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminReplyTicket(ticketId, reply, newStatus, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminBroadcast(title: String, msg: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.sendBroadcastNotification(title, msg, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  // Custom Ad & Sponsorship Actions
  fun adminCreateAd(ad: AdvertisementEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminCreateAd(ad, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminUpdateAd(ad: AdvertisementEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminUpdateAd(ad, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminToggleAd(adId: String, isActive: Boolean) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminToggleAd(adId, isActive, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminDeleteAd(adId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminDeleteAd(adId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun recordAdImpression(adId: String) {
    val sessionKey = _currentUserId.value ?: "guest_session"
    viewModelScope.launch {
      repository.recordAdImpression(adId, sessionKey)
    }
  }

  fun openAdTarget(context: android.content.Context, ad: AdvertisementEntity) {
    val sessionKey = _currentUserId.value ?: "guest_session"
    viewModelScope.launch {
      repository.recordAdClick(ad.id, sessionKey)
    }
    try {
      val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(ad.targetUrl))
      intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    } catch (e: Exception) {
      showMessage("লিংকটি ওপেন করা সম্ভব হয়নি: ${ad.targetUrl}")
    }
  }

  // Advertiser Actions
  fun adminCreateAdvertiser(advertiser: AdvertiserEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminCreateAdvertiser(advertiser, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminUpdateAdvertiser(advertiser: AdvertiserEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminUpdateAdvertiser(advertiser, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminDeleteAdvertiser(advertiserId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminDeleteAdvertiser(advertiserId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  // Ad Package Actions
  fun adminCreatePackage(pkg: AdPackageEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminCreatePackage(pkg, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminUpdatePackage(pkg: AdPackageEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminUpdatePackage(pkg, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminTogglePackage(pkgId: String, isActive: Boolean) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminTogglePackage(pkgId, isActive, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminDeletePackage(pkgId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminDeletePackage(pkgId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  // Ad Campaign Actions
  fun adminCreateCampaign(campaign: AdCampaignEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminCreateCampaign(campaign, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminMarkCampaignPaid(campaignId: String, method: String, trxId: String, notes: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminMarkCampaignPaymentPaid(campaignId, method, trxId, notes, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminUpdateCampaignStatus(campaignId: String, status: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminUpdateCampaignStatus(campaignId, status, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminDeleteCampaign(campaignId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminDeleteCampaign(campaignId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  // Ad Settings Action
  fun adminUpdateAdContactSettings(settings: AdContactSettingsEntity) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminUpdateAdContactSettings(settings, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  // Dynamic Contact Launchers reading directly from DB settings
  fun contactForSponsorship(context: android.content.Context, viaEmail: Boolean = false) {
    val settings = adContactSettings.value ?: AdContactSettingsEntity()
    try {
      if (viaEmail) {
        val email = settings.email
        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
          data = android.net.Uri.parse("mailto:$email")
          putExtra(android.content.Intent.EXTRA_SUBJECT, settings.emailSubject)
          putExtra(android.content.Intent.EXTRA_TEXT, "হ্যালো কাজসেতু টিম, আমি আপনার প্ল্যাটফর্মে বিজ্ঞাপন বা স্পন্সরশিপ দিতে আগ্রহী।")
          addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
      } else {
        val wp = settings.whatsappNumber.replace(Regex("[^0-9]"), "")
        val msg = settings.whatsappMessage
        val url = "https://wa.me/$wp?text=" + java.net.URLEncoder.encode(msg, "UTF-8")
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
          addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
      }
    } catch (e: Exception) {
      showMessage("যোগাযোগ মাধ্যম চালু করা যায়নি। WhatsApp: ${settings.whatsappNumber}")
    }
  }

  fun contactViaTelegram(context: android.content.Context) {
    val site = siteSettings.value
    val ad = adContactSettings.value
    val rawLink = site?.telegramChannelUrl?.takeIf { it.isNotBlank() }
      ?: ad?.telegramLink?.takeIf { it.isNotBlank() }
      ?: "https://t.me/KajSetuOfficial"
    openTelegramChannel(context, rawLink)
  }

  fun openTelegramChannel(context: android.content.Context, channelUrl: String? = null) {
    val site = siteSettings.value
    val rawLink = channelUrl?.takeIf { it.isNotBlank() }
      ?: site?.telegramChannelUrl?.takeIf { it.isNotBlank() }
      ?: "https://t.me/KajSetuOfficial"
    try {
      val link = if (rawLink.startsWith("http")) rawLink else "https://t.me/${rawLink.replace("@", "")}"
      val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(link)).apply {
        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      showMessage("টেলিগ্রাম লিঙ্ক ওপেন করা যায়নি: $rawLink")
    }
  }

  fun openExternalUrl(context: android.content.Context, url: String) {
    try {
      val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      showMessage("ভিডিও লিঙ্কটি ওপেন করা সম্ভব হয়নি: $url")
    }
  }

  // Redeem Bonus Actions
  fun redeemBonusCode(code: String, onSuccess: () -> Unit = {}) {
    val uid = _currentUserId.value ?: return
    viewModelScope.launch {
      when (val res = repository.redeemBonusCode(uid, code)) {
        is OperationResult.Success -> {
          showMessage(res.message)
          onSuccess()
        }
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminCreateRedeemCode(code: String, bonusAmount: Double, maxUses: Int, onSuccess: () -> Unit = {}) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminCreateRedeemCode(code, bonusAmount, maxUses, admin)) {
        is OperationResult.Success -> {
          showMessage(res.message)
          onSuccess()
        }
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminToggleRedeemCode(codeId: String, isActive: Boolean) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminToggleRedeemCode(codeId, isActive, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }

  fun adminDeleteRedeemCode(codeId: String) {
    val admin = currentUser.value ?: return
    if (admin.role != "admin") return
    viewModelScope.launch {
      when (val res = repository.adminDeleteRedeemCode(codeId, admin)) {
        is OperationResult.Success -> showMessage(res.message)
        is OperationResult.Error -> showMessage(res.message)
      }
    }
  }
}

