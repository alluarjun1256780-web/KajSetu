package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    UserEntity::class,
    WalletEntity::class,
    TransactionEntity::class,
    TaskEntity::class,
    MicroTaskEntity::class,
    TaskSubmissionEntity::class,
    DepositEntity::class,
    WithdrawalEntity::class,
    ActivationRequestEntity::class,
    NotificationEntity::class,
    SupportTicketEntity::class,
    AuditLogEntity::class,
    SiteSettingsEntity::class,
    MarketplaceServiceEntity::class,
    AdvertisementEntity::class,
    AdvertiserEntity::class,
    AdPackageEntity::class,
    AdCampaignEntity::class,
    AdContactSettingsEntity::class,
    AdEventEntity::class,
    AdAuditLogEntity::class,
    GmailSubmissionEntity::class,
    RedeemCodeEntity::class,
    RedeemCodeUsageEntity::class
  ],
  version = 5,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun walletDao(): WalletDao
  abstract fun transactionDao(): TransactionDao
  abstract fun taskDao(): TaskDao
  abstract fun microTaskDao(): MicroTaskDao
  abstract fun taskSubmissionDao(): TaskSubmissionDao
  abstract fun depositDao(): DepositDao
  abstract fun withdrawalDao(): WithdrawalDao
  abstract fun activationRequestDao(): ActivationRequestDao
  abstract fun gmailSubmissionDao(): GmailSubmissionDao
  abstract fun notificationDao(): NotificationDao
  abstract fun supportTicketDao(): SupportTicketDao
  abstract fun auditLogDao(): AuditLogDao
  abstract fun siteSettingsDao(): SiteSettingsDao
  abstract fun marketplaceServiceDao(): MarketplaceServiceDao
  abstract fun advertisementDao(): AdvertisementDao
  abstract fun advertiserDao(): AdvertiserDao
  abstract fun adPackageDao(): AdPackageDao
  abstract fun adCampaignDao(): AdCampaignDao
  abstract fun adContactSettingsDao(): AdContactSettingsDao
  abstract fun adEventDao(): AdEventDao
  abstract fun adAuditLogDao(): AdAuditLogDao
  abstract fun redeemCodeDao(): RedeemCodeDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "kajsetu_database.db"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback())
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          CoroutineScope(Dispatchers.IO).launch {
            populateInitialData(database)
          }
        }
      }
    }

    private suspend fun populateInitialData(db: AppDatabase) {
      val userDao = db.userDao()
      val walletDao = db.walletDao()
      val taskDao = db.taskDao()
      val settingsDao = db.siteSettingsDao()
      val serviceDao = db.marketplaceServiceDao()
      val notificationDao = db.notificationDao()
      val transactionDao = db.transactionDao()
      val auditLogDao = db.auditLogDao()

      // 1. Initial Site Settings
      settingsDao.insertOrUpdateSettings(SiteSettingsEntity())

      // 2. Admin Users
      val admin = UserEntity(
        id = "admin_001",
        fullName = "অ্যাডমিন সুপারভাইজার",
        username = "kajsetu_admin",
        email = "admin@kajsetu.com",
        phone = "01700000000",
        passwordHash = "admin123",
        role = "admin",
        accountStatus = "active",
        referralCode = "KAJADMIN1"
      )
      userDao.insertUser(admin)
      walletDao.insertWallet(WalletEntity(userId = admin.id, balance = 5000.0))

      val adminOwner = UserEntity(
        id = "admin_owner_001",
        fullName = "প্রধান অ্যাডমিন",
        username = "neymarjrfan",
        email = "neymarjrfan337@gmail.com",
        phone = "01711000000",
        passwordHash = "admin123",
        role = "admin",
        accountStatus = "active",
        referralCode = "KAJOWNER1"
      )
      userDao.insertUser(adminOwner)
      walletDao.insertWallet(WalletEntity(userId = adminOwner.id, balance = 10000.0))

      // 3. Active User (Rahim)
      val userRahim = UserEntity(
        id = "user_rahim_001",
        fullName = "আব্দুর রহিম",
        username = "rahim99",
        email = "rahim@example.com",
        phone = "01711223344",
        passwordHash = "user123",
        role = "user",
        accountStatus = "active",
        referralCode = "RAHIM2026",
        referredBy = "KAJADMIN1"
      )
      userDao.insertUser(userRahim)
      walletDao.insertWallet(
        WalletEntity(
          userId = userRahim.id,
          balance = 350.0,
          taskEarnings = 250.0,
          referralEarnings = 100.0,
          totalDeposit = 0.0,
          totalWithdrawal = 0.0
        )
      )

      // Initial transactions for Rahim
      transactionDao.insertTransaction(
        TransactionEntity(
          userId = userRahim.id,
          type = "TASK_REWARD",
          amount = 150.0,
          balanceBefore = 0.0,
          balanceAfter = 150.0,
          referenceId = "TASK_INIT_1",
          status = "COMPLETED",
          description = "টাস্ক সম্পন্ন পুরস্কার: ইউটিউব ভিডিও লাইক ও কমেন্ট"
        )
      )
      transactionDao.insertTransaction(
        TransactionEntity(
          userId = userRahim.id,
          type = "REFERRAL_REWARD",
          amount = 100.0,
          balanceBefore = 150.0,
          balanceAfter = 250.0,
          referenceId = "REF_BONUS",
          status = "COMPLETED",
          description = "সফল রেফারেল বোনাস (৪ জন বন্ধু যোগদান)"
        )
      )
      transactionDao.insertTransaction(
        TransactionEntity(
          userId = userRahim.id,
          type = "TASK_REWARD",
          amount = 100.0,
          balanceBefore = 250.0,
          balanceAfter = 350.0,
          referenceId = "TASK_INIT_2",
          status = "COMPLETED",
          description = "টাস্ক সম্পন্ন পুরস্কার: গুগল ম্যাপ ৫-স্টার রিভিউ"
        )
      )

      // 4. Inactive User (Karim)
      val userKarim = UserEntity(
        id = "user_karim_002",
        fullName = "করিম উল্লাহ",
        username = "karim_bd",
        email = "karim@example.com",
        phone = "01811223344",
        passwordHash = "user123",
        role = "user",
        accountStatus = "inactive",
        referralCode = "KARIM2026"
      )
      userDao.insertUser(userKarim)
      walletDao.insertWallet(WalletEntity(userId = userKarim.id, balance = 0.0))

      // 5. Initial Micro Tasks
      val initialTasks = listOf(
        TaskEntity(
          id = "task_01",
          title = "ইউটিউব ভিডিও দেখে লাইক ও সুন্দর মন্তব্য করুন",
          description = "আমাদের ক্লায়েন্টের অফিশিয়াল টেকনোলজি চ্যানেলের ভিডিও সম্পূর্ণ দেখুন এবং সুন্দর একটি ইতিবাচক মন্তব্য করুন।",
          instructions = "১. লিঙ্কে গিয়ে কমপক্ষে ২ মিনিট ভিডিওটি দেখুন।\n২. ভিডিওতে লাইক দিন এবং সাবস্ক্রাইব করুন।\n৩. একটি প্রাসঙ্গিক মন্তব্য করুন।\n৪. আপনার ইউটিউব ইউজারনেম ও কমেন্টের স্ক্রিনশট জমা দিন।",
          rewardAmount = 15.0,
          category = "সোশ্যাল মিডিয়া",
          taskUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
          maxSubmissions = 200,
          currentSubmissions = 42,
          proofType = "ইউটিউব ইউজারনেম ও স্ক্রিনশট"
        ),
        TaskEntity(
          id = "task_02",
          title = "ফেসবুক বিজনেস পেইজে লাইক, ফলো এবং রিভিউ দিন",
          description = "একটি স্বনামধন্য ই-কমার্স ফেসবুক পেইজে লাইক দিয়ে পেজটির সার্ভিসের প্রশংসা করে একটি ৫-স্টার রিভিউ প্রদান করুন।",
          instructions = "১. ফেসবুক পেজটিতে যান এবং Follow/Like বাটনে চাপ দিন।\n২. Review সেকশনে গিয়ে Recommend করুন এবং ২-৩ লাইনের রিভিউ লিখুন।\n৩. প্রমাণ হিসেবে আপনার ফেসবুক প্রোফাইলের নাম ও স্ক্রিনশট দিন।",
          rewardAmount = 18.0,
          category = "সোশ্যাল মিডিয়া",
          taskUrl = "https://www.facebook.com/",
          maxSubmissions = 150,
          currentSubmissions = 30,
          proofType = "ফেসবুক প্রোফাইল নাম ও স্ক্রিনশট"
        ),
        TaskEntity(
          id = "task_03",
          title = "গুগল প্লে স্টোর অ্যাপ ডাউনলোড ও ৫ স্টার রেটিং",
          description = "একটি এডুকেশন অ্যাপ ডাউনলোড করে ফোনে রাখুন এবং প্লে স্টোরে একটি প্রশংসাসূচক রিভিউ পোস্ট করুন।",
          instructions = "১. প্লে-স্টোর থেকে অ্যাপ ইনস্টল করুন।\n২. ৫ স্টার রেটিং দিয়ে ইতিবাচক রিভিউ লিখুন।\n৩. আপনার প্লে স্টোরের ডিসপ্লে নাম এবং রিভিউ পোস্টের স্ক্রিনশট জমা দিন।",
          rewardAmount = 30.0,
          category = "অ্যাপ রিভিউ",
          taskUrl = "https://play.google.com/store/apps",
          maxSubmissions = 100,
          currentSubmissions = 18,
          proofType = "প্লে-স্টোর ইউজারনেম ও স্ক্রিনশট"
        ),
        TaskEntity(
          id = "task_04",
          title = "টেলিগ্রাম চ্যানেলে জয়েন হয়ে সক্রিয় থাকুন",
          description = "আমাদের পার্টনার ক্রিপ্টো ও টেক আপডেট টেলিগ্রাম কমিউনিটিতে জয়েন করে নোটিফিকেশন অন রাখুন।",
          instructions = "১. টেলিগ্রাম চ্যানেলে জয়েন করুন।\n২. সর্বশেষ পোস্টটিতে একটি রিঅ্যাক্ট দিন।\n৩. টেলিগ্রাম ইউজারনেম (@username) প্রুফ বক্সে লিখুন।",
          rewardAmount = 10.0,
          category = "কমিউনিটি",
          taskUrl = "https://t.me/KajSetuBot",
          maxSubmissions = 300,
          currentSubmissions = 95,
          proofType = "টেলিগ্রাম @ইউজারনেম ও স্ক্রিনশট"
        ),
        TaskEntity(
          id = "task_05",
          title = "একটি মার্কেটপ্লেসে ফ্রি সাইন আপ ও ইমেইল ভেরিফাই",
          description = "একটি ফ্রিল্যান্সিং সার্ভিস ওয়েবসাইটে নতুন একাউন্ট খুলে ইমেইল ইনবক্সে পাওয়া লিঙ্কে ক্লিক করে একাউন্ট নিশ্চিত করুন।",
          instructions = "১. প্রদত্ত রেফারেল লিঙ্কে প্রবেশ করুন।\n২. সঠিক তথ্য দিয়ে রেজিস্ট্রেশন সম্পন্ন করুন।\n৩. ইনবক্সের ভেরিফিকেশন লিংকে ক্লিক করে ড্যাশবোর্ডে লগইন করুন।\n৪. নিবন্ধিত ইমেইল ঠিকানা জমা দিন।",
          rewardAmount = 25.0,
          category = "সাইন আপ",
          taskUrl = "https://example.com/signup",
          maxSubmissions = 80,
          currentSubmissions = 12,
          proofType = "নিবন্ধিত ইমেইল ঠিকানা"
        )
      )
      initialTasks.forEach { taskDao.insertTask(it) }

      // 5b. Initial MicroTasks for MicroTaskDao
      val microTaskDao = db.microTaskDao()
      val initialMicroTasks = initialTasks.map { t ->
        MicroTaskEntity(
          id = "micro_" + t.id,
          title = t.title,
          description = t.description,
          rewardAmount = t.rewardAmount,
          deadline = t.deadline,
          category = t.category,
          instructions = t.instructions,
          taskUrl = t.taskUrl,
          maxSubmissions = t.maxSubmissions,
          currentSubmissions = t.currentSubmissions,
          proofType = t.proofType,
          status = t.status,
          createdAt = t.createdAt
        )
      }
      microTaskDao.insertAll(initialMicroTasks)

      // 6. Initial Marketplace Services
      val initialServices = listOf(
        MarketplaceServiceEntity(
          id = "svc_01",
          title = "প্রফেশনাল ফেসবুক পেইজ সেটআপ ও অর্গানিক বুস্টিং গাইড",
          category = "ডিজিটাল মার্কেটিং",
          price = 450.0,
          turnaroundTime = "২৪ ঘন্টা",
          description = "আপনার অনলাইন ব্যবসার জন্য কমপ্লিট ফেসবুক বিজনেস পেইজ ক্রিয়েশন, ব্যানার, অটো-মেসেঞ্জার সেটআপ ও মার্কেটিং কনসাল্টেশন।",
          providerName = "KajSetu Certified Team",
          rating = 5.0
        ),
        MarketplaceServiceEntity(
          id = "svc_02",
          title = "হাই-কনভার্টিং ইউটিউব থাম্বনেইল ও ফেসবুক পোস্ট ডিজাইন",
          category = "গ্রাফিক্স ডিজাইন",
          price = 250.0,
          turnaroundTime = "১২ ঘন্টা",
          description = "৩টি আকর্ষণীয় ও ক্লিক-থ্রু রেট বৃদ্ধি করার মতো প্রিমিয়াম সোশ্যাল মিডিয়া ব্যানার ও থাম্বনেইল প্যাকেজ।",
          providerName = "ডিজাইন স্টুডিও বিডি",
          rating = 4.9
        ),
        MarketplaceServiceEntity(
          id = "svc_03",
          title = "এসইও ফ্রেন্ডলি বাংলা/ইংরেজি কন্টেন্ট রাইটিং (১০০০ শব্দ)",
          category = "কন্টেন্ট রাইটিং",
          price = 500.0,
          turnaroundTime = "৪৮ ঘন্টা",
          description = "গুগল র‍্যাংকিং উপযোগী ইউনিক, প্লেজারিজম-মুক্ত ব্লগের তথ্যবহুল আর্টিকেল।",
          providerName = "বাংলা কন্টেন্ট হাব",
          rating = 4.8
        )
      )
      initialServices.forEach { serviceDao.insertService(it) }

      // 7. Initial Notifications
      notificationDao.insertNotification(
        NotificationEntity(
          userId = "ALL",
          title = "কাজসেতু প্ল্যাটফর্মে আপনাকে স্বাগতম!",
          message = "স্মার্ট আয়ের নতুন যুগে আপনাকে শুভেচ্ছা। কাজ শুরু করার পূর্বে আপনার প্রোফাইল সম্পূর্ণ ও অ্যাকাউন্ট ভেরিফাই করুন।",
          type = "INFO"
        )
      )
      notificationDao.insertNotification(
        NotificationEntity(
          userId = userRahim.id,
          title = "অ্যাকাউন্ট ভেরিফিকেশন সফল হয়েছে!",
          message = "আপনার অ্যাকাউন্ট সক্রিয় করা হয়েছে। এখন আপনি সকল প্রিমিয়াম মাইক্রো টাস্কে অংশগ্রহণ করতে পারবেন।",
          type = "SUCCESS"
        )
      )

      // 8. Initial Audit Log
      auditLogDao.insertLog(
        AuditLogEntity(
          adminId = admin.id,
          adminUsername = admin.username,
          action = "SYSTEM_INITIALIZED",
          targetType = "SYSTEM",
          targetId = "PLATFORM",
          details = "কাজসেতু সিস্টেম সফলভাবে ইনিশিয়ালাইজ ও সুরক্ষিত করা হয়েছে।"
        )
      )

      // 9. Initial Sponsored Ads & Ad System Setup
      val adDao = db.advertisementDao()
      val adContactDao = db.adContactSettingsDao()
      val adPkgDao = db.adPackageDao()
      val advertiserDao = db.advertiserDao()
      val adCampaignDao = db.adCampaignDao()

      // Default Ad Contact Settings
      adContactDao.insertOrUpdateSettings(
        AdContactSettingsEntity(
          whatsappNumber = "+8801712345678",
          whatsappMessage = "হ্যালো, আমি কাজসেতু প্ল্যাটফর্মে বিজ্ঞাপন ও স্পনসরশিপ দিতে আগ্রহী।",
          email = "sponsor@kajsetu.com",
          emailSubject = "বিজ্ঞাপন প্রচার সংক্রান্ত অনুসন্ধান - কাজসেতু",
          telegramLink = "https://t.me/KajSetuOfficial",
          adLabelText = "Sponsored"
        )
      )

      // Default Ad Packages
      val pkgBasic = AdPackageEntity(
        id = "pkg_basic",
        name = "বেসিক প্যাক (Basic)",
        durationDays = 7,
        price = 500.0,
        position = "DASHBOARD",
        description = "৭ দিন মেয়াদি ড্যাশবোর্ড ও টাস্ক ডিসপ্লে বিজ্ঞাপন",
        features = "ব্যানার বিজ্ঞাপন, সরাসরি টার্গেট লিংক, বেসিক ক্লিক রিপোর্ট"
      )
      val pkgStandard = AdPackageEntity(
        id = "pkg_standard",
        name = "স্ট্যান্ডার্ড প্যাক (Standard)",
        durationDays = 15,
        price = 900.0,
        position = "HEADER",
        description = "১৫ দিন মেয়াদি টপ হেডার ও টাস্কের মাঝে ডিসপ্লে বিজ্ঞাপন",
        features = "হাই-ভিজিবিলিটি টপ ব্যানার, ডেলি ক্লিক ট্র্যাকিং, সোশ্যাল সাপোর্ট"
      )
      val pkgPremium = AdPackageEntity(
        id = "pkg_premium",
        name = "প্রিমিয়াম ভিআইপি প্যাক (Premium)",
        durationDays = 30,
        price = 1500.0,
        position = "HOMEPAGE",
        description = "৩০ দিন মেয়াদি পুরো সাইট জুড়ে সর্বোচ্চ প্রায়োরিটি স্পনসরশিপ",
        features = "সর্বোচ্চ প্রায়োরিটি রোটেশন, আনলিমিটেড ক্লিক ট্র্যাকিং, ভিআইপি প্রমোশন"
      )
      adPkgDao.insertPackage(pkgBasic)
      adPkgDao.insertPackage(pkgStandard)
      adPkgDao.insertPackage(pkgPremium)

      // Default Advertisers
      val advSkill = AdvertiserEntity(
        id = "adv_01",
        companyName = "ডিজিটাল স্কিল একাডেমি বিডি",
        contactPerson = "আহমেদ হাসান",
        phone = "+8801812345678",
        email = "info@skillacademybd.com",
        telegramUsername = "SkillAcademyBD",
        website = "https://skillacademybd.com",
        notes = "ফ্রিল্যান্সিং কোর্স স্পনসর"
      )
      val advBot = AdvertiserEntity(
        id = "adv_02",
        companyName = "কাজসেতু কমিউনিটি বট",
        contactPerson = "সিস্টেম অ্যাডমিন",
        phone = "+8801700000000",
        email = "bot@kajsetu.com",
        telegramUsername = "KajSetuBot",
        website = "https://t.me/KajSetuBot",
        notes = "অভ্যন্তরীণ টেলিগ্রাম বট প্রমোশন"
      )
      advertiserDao.insertAdvertiser(advSkill)
      advertiserDao.insertAdvertiser(advBot)

      // Default Campaigns
      val campaign1 = AdCampaignEntity(
        id = "camp_01",
        campaignName = "টেলিগ্রাম অফিসিয়াল বট ক্যাম্পেইন",
        advertiserId = advBot.id,
        advertiserName = advBot.companyName,
        packageId = pkgPremium.id,
        packageName = pkgPremium.name,
        price = 1500.0,
        position = "HEADER",
        pricingType = "FIXED",
        status = "ACTIVE",
        paymentStatus = "PAID",
        paymentMethod = "MANUAL",
        transactionId = "INTERNAL_SPONSOR_01",
        paymentNotes = "অফিসিয়াল সিস্টেম ক্যাম্পেইন"
      )
      val campaign2 = AdCampaignEntity(
        id = "camp_02",
        campaignName = "স্কিল একাডেমি বিডি কোর্স স্পনসর",
        advertiserId = advSkill.id,
        advertiserName = advSkill.companyName,
        packageId = pkgStandard.id,
        packageName = pkgStandard.name,
        price = 900.0,
        position = "BETWEEN_TASKS",
        pricingType = "FIXED",
        status = "ACTIVE",
        paymentStatus = "PAID",
        paymentMethod = "BKASH",
        transactionId = "BK78291048",
        paymentNotes = "বিকাশ মার্চেন্ট পেমেন্ট সম্পন্ন"
      )
      adCampaignDao.insertCampaign(campaign1)
      adCampaignDao.insertCampaign(campaign2)

      // Initial Sponsored Ads
      val initialAds = listOf(
        AdvertisementEntity(
          id = "ad_01",
          title = "আমাদের অফিসিয়াল টেলিগ্রাম বট",
          subtitle = "নতুন কাজ আসা মাত্র নোটিফিকেশন পেতে ও দ্রুত পেমেন্ট আপডেট জানতে জয়েন করুন।",
          description = "স্বয়ংক্রিয় নোটিফিকেশন ও দ্রুততম কাজ আপডেট টেলিগ্রাম বট।",
          advertiserName = "কাজসেতু কমিউনিটি বট",
          targetUrl = "https://t.me/KajSetuBot",
          position = "HEADER",
          campaignType = "TELEGRAM_BOT",
          pricingType = "FIXED",
          price = 1500.0,
          priority = 10,
          buttonText = "টেলিগ্রাম বটে যান",
          clickCount = 142,
          impressionCount = 1250,
          isActive = true
        ),
        AdvertisementEntity(
          id = "ad_02",
          title = "ডিজিটাল স্কিল একাডেমি বিডি",
          subtitle = "অনলাইনে ফ্রিল্যান্সিং শিখে ঘরে বসে প্রতি মাসে ২০,০০০+ টাকা আয় করার স্পেশাল কোর্স!",
          description = "প্রফেশনাল ডিজিটাল মার্কেটিং ও গ্রাফিক ডিজাইন কোর্স অফার।",
          advertiserName = "ডিজিটাল স্কিল একাডেমি বিডি",
          targetUrl = "https://t.me/SkillAcademyBD",
          position = "BETWEEN_TASKS",
          campaignType = "BANNER",
          pricingType = "FIXED",
          price = 900.0,
          priority = 8,
          buttonText = "কোর্স অফার দেখুন",
          clickCount = 89,
          impressionCount = 980,
          isActive = true
        ),
        AdvertisementEntity(
          id = "ad_03",
          title = "কাজসেতুতে বিজ্ঞাপন দিন ও গ্রাহক বাড়ান",
          subtitle = "আপনার ব্যবসা, ইউটিউব চ্যানেল বা টেলিগ্রাম গ্রুপের প্রমোশন করুন হাজারো তরুণের কাছে।",
          description = "টার্গেটেড অডিয়েন্সের কাছে দ্রুত ব্র্যান্ড প্রচার করুন সাশ্রয়ী মূল্যে।",
          advertiserName = "কাজসেতু এডভার্টাইজিং",
          targetUrl = "https://wa.me/8801712345678",
          position = "DASHBOARD",
          campaignType = "EXTERNAL_LINK",
          pricingType = "FIXED",
          price = 500.0,
          priority = 9,
          buttonText = "বিজ্ঞাপন দিতে যোগাযোগ",
          clickCount = 56,
          impressionCount = 740,
          isActive = true
        ),
        AdvertisementEntity(
          id = "ad_04",
          title = "প্রো হোস্টিং ও ডোমেইন বিডি",
          subtitle = "মাত্র ৯৯ টাকায় ডোমেইন এবং নির্ভরযোগ্য এসএসডি ক্লাউড হোস্টিং।",
          description = "২৪/৭ সাপোর্ট ও ফ্রি এসএসএল সার্টিফিকেট সহ ফাস্ট হোস্টিং।",
          advertiserName = "দেশি হোস্টিং",
          targetUrl = "https://t.me/DeshiHosting",
          position = "MARKETPLACE",
          campaignType = "BANNER",
          pricingType = "FIXED",
          price = 500.0,
          priority = 7,
          buttonText = "হোস্টিং প্ল্যান দেখুন",
          clickCount = 38,
          impressionCount = 420,
          isActive = true
        )
      )
      initialAds.forEach { adDao.insertAd(it) }

      // Initial Redeem Codes for demonstration & daily bonuses
      val redeemCodeDao = db.redeemCodeDao()
      val sampleCodes = listOf(
        RedeemCodeEntity(
          code = "KAJSETU50",
          bonusAmount = 50.0,
          maxUses = 100,
          usedCount = 12,
          isActive = true,
          createdBy = "admin"
        ),
        RedeemCodeEntity(
          code = "TELEGRAM20",
          bonusAmount = 20.0,
          maxUses = 50,
          usedCount = 28,
          isActive = true,
          createdBy = "admin"
        ),
        RedeemCodeEntity(
          code = "BONUS100",
          bonusAmount = 100.0,
          maxUses = 30,
          usedCount = 30,
          isActive = true,
          createdBy = "admin"
        )
      )
      sampleCodes.forEach { redeemCodeDao.insertCode(it) }
    }
  }
}
