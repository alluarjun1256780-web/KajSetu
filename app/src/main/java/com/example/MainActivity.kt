package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.KajSetuViewModel
import com.example.ui.Screen
import com.example.ui.screens.*
import com.example.ui.theme.KajSetuTheme
import com.example.util.FirebaseAppCheckManager

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseAppCheckManager.init(this)
    enableEdgeToEdge()
    setContent {
      KajSetuTheme {
        KajSetuApp()
      }
    }
  }
}

@Composable
fun KajSetuApp(viewModel: KajSetuViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  // Listen to snackbar notifications
  LaunchedEffect(Unit) {
    viewModel.snackbarMessage.collect { msg ->
      snackbarHostState.showSnackbar(msg)
    }
  }

  // Handle hardware Back button
  BackHandler(enabled = currentScreen !is Screen.Landing && currentScreen !is Screen.Dashboard) {
    viewModel.navigateBack()
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    androidx.compose.foundation.layout.Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (val screen = currentScreen) {
        is Screen.Landing -> {
          val context = androidx.compose.ui.platform.LocalContext.current
          LandingScreen(
            onNavigateToAuth = { viewModel.navigateTo(Screen.Auth) },
            onNavigateToDashboard = { viewModel.navigateTo(Screen.Dashboard) },
            onNavigateToTasks = { viewModel.navigateTo(Screen.Tasks) },
            onNavigateToServices = { viewModel.navigateTo(Screen.Marketplace) },
            onNavigateToPolicies = { viewModel.navigateTo(Screen.Policies) },
            onNavigateToSupport = { viewModel.navigateTo(Screen.Support) },
            isLoggedIn = currentUser != null,
            onContactWhatsApp = { viewModel.contactForSponsorship(context, viaEmail = false) },
            onContactEmail = { viewModel.contactForSponsorship(context, viaEmail = true) },
            onContactTelegram = { viewModel.contactViaTelegram(context) },
            onNavigateToAdvertise = { viewModel.navigateTo(Screen.Advertise) }
          )
        }

        is Screen.Auth -> {
          AuthScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Dashboard -> {
          UserDashboardScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) }
          )
        }

        is Screen.Tasks -> {
          TasksScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.TaskDetail -> {
          TaskDetailScreen(
            taskId = screen.taskId,
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.MyTasks -> {
          MyTasksScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Wallet -> {
          WalletScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Deposit -> {
          DepositScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Withdraw -> {
          WithdrawScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Activation -> {
          ActivationScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Referral -> {
          ReferralScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Profile -> {
          ProfileScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Marketplace -> {
          MarketplaceScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Notifications -> {
          NotificationsScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Support -> {
          SupportScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Policies -> {
          PoliciesScreen(
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.AdminPortal -> {
          if (currentUser?.role == "admin") {
            AdminPortalScreen(
              viewModel = viewModel,
              onBack = { viewModel.navigateTo(Screen.Dashboard) }
            )
          } else {
            LaunchedEffect(Unit) {
              viewModel.showMessage("অ্যাডমিন অ্যাক্সেস শুধুমাত্র অনুমোদিত অ্যাডমিনের জন্য সংরক্ষিত।")
              viewModel.navigateTo(Screen.Dashboard)
            }
          }
        }

        is Screen.Advertise -> {
          AdvertiseScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.GmailSell -> {
          GmailSellScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.RedeemBonus -> {
          RedeemBonusScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() }
          )
        }

        is Screen.Tutorials -> {
          TutorialsScreen(
            viewModel = viewModel,
            onNavigate = { dest -> viewModel.navigateTo(dest) },
            onBack = { viewModel.navigateBack() }
          )
        }
      }
    }
  }
}
