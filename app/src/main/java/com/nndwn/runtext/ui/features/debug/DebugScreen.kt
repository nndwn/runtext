package com.nndwn.runtext.ui.features.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nndwn.runtext.AppFlavor
import com.nndwn.runtext.ui.utils.launchInAppReview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(viewModel: DebugViewModel = hiltViewModel(), onBack: () -> Unit = {}) {
  val context = LocalContext.current

  val hasTipped by viewModel.hasTipped.collectAsStateWithLifecycle()
  val accumulatedSupportTime by viewModel.accumulatedSupportTime.collectAsStateWithLifecycle()
  val accumulatedReviewTime by viewModel.accumulatedReviewTime.collectAsStateWithLifecycle()
  val hasRequestedReview by viewModel.hasRequestedReview.collectAsStateWithLifecycle()
  val shouldShowSupportDialog by viewModel.shouldShowSupportDialog.collectAsStateWithLifecycle()
  val shouldShowReviewPrompt by viewModel.shouldShowReviewPrompt.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Debug Panel") },
        navigationIcon = {
          IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
        },
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // 📊 Live Status Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text("📊 Live Debug Status", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
          HorizontalDivider()

          Text("App Flavor: ${AppFlavor.current}")
          Text("Has Tipped: $hasTipped")
          Text("Support Usage Time: ${accumulatedSupportTime / 1000}s / 900s (Dialog Active: $shouldShowSupportDialog)")
          Text("Review Usage Time: ${accumulatedReviewTime / 1000}s / 3600s")
          Text("Has Requested Review: $hasRequestedReview")
          Text("Review Prompt Active: $shouldShowReviewPrompt", fontWeight = FontWeight.Bold)
        }
      }

      // 🧪 Test Actions
      Text("🧪 Visual & Trigger Tests", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

      Button(
        onClick = { launchInAppReview(context) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
      ) {
        Text("🚀 Direct Test Launch In-App Review Flow")
      }

      Button(
        onClick = { viewModel.forceShowReviewPrompt() },
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text("⏱️ Force Set Review Time (1 Hour Ready)")
      }

      Button(
        onClick = { viewModel.forceShowSupportDialog() },
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text("☕ Force Set Support Time (15 Mins Ready)")
      }

      OutlinedButton(
        onClick = { viewModel.toggleTipped(hasTipped) },
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text("☕ Toggle Has Tipped Status (Current: $hasTipped)")
      }

      OutlinedButton(
        onClick = { viewModel.resetReviewStatus() },
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text("🔄 Reset Review Status (Allow Re-test)")
      }

      Button(
        onClick = { viewModel.resetDataStore() },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
      ) {
        Text("⚠️ Reset All DataStore")
      }
    }
  }
}
