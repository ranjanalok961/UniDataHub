package com.assignmentwaala.unidatahub

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.assignmentwaala.unidatahub.presentation.App
import com.assignmentwaala.unidatahub.presentation.screens.SplashScreen
import com.assignmentwaala.unidatahub.ui.theme.UniDataHubTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var splashScreen = installSplashScreen()


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT
            ),
        )
        setContent {
            UniDataHubTheme {
               App()
            }
        }
    }
    companion object {
        val TAG = "UniDataHubDebug"
    }
}