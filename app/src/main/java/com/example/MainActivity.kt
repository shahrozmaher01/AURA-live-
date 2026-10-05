package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.audio.BismaAudioManager
import com.example.data.BismaRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalVioletDark

enum class AppFlowState {
    SPLASH,
    AUTH,
    MAIN
}

class MainActivity : ComponentActivity() {
    private lateinit var repository: BismaRepository
    private lateinit var audioManager: BismaAudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = BismaRepository(applicationContext)
        audioManager = BismaAudioManager(applicationContext)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RoyalVioletDark
                ) {
                    BismaApp(
                        repository = repository,
                        audioManager = audioManager,
                        onRequestRecordAudio = { checkAndRequestMicPermission() }
                    )
                }
            }
        }
    }

    private fun checkAndRequestMicPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 101)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.stopVoiceCapture()
        audioManager.stopBgm()
    }
}

@Composable
fun BismaApp(
    repository: BismaRepository,
    audioManager: BismaAudioManager,
    onRequestRecordAudio: () -> Unit
) {
    var flowState by remember { mutableStateOf(AppFlowState.SPLASH) }
    val isLoggedIn by repository.isLoggedIn.collectAsState()

    // Permission launcher for microphone (runtime protected)
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission handled
    }

    LaunchedEffect(Unit) {
        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    when (flowState) {
        AppFlowState.SPLASH -> {
            SplashScreen(
                onSplashFinished = {
                    flowState = if (isLoggedIn) AppFlowState.MAIN else AppFlowState.AUTH
                }
            )
        }
        AppFlowState.AUTH -> {
            AuthScreen(
                onLoginSuccess = { flowState = AppFlowState.MAIN },
                onLogin = { u, p -> repository.login(u, p) },
                onRegister = { u, d, g -> repository.register(u, d, g) }
            )
        }
        AppFlowState.MAIN -> {
            MainScaffold(
                repository = repository,
                audioManager = audioManager,
                onLogout = { flowState = AppFlowState.AUTH }
            )
        }
    }
}
