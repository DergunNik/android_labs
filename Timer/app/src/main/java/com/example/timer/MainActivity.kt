package com.example.timer

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.timer.domain.repository.SettingsRepository
import com.example.timer.presentation.navigation.SetupNavGraph
import com.example.timer.ui.theme.TimerTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun attachBaseContext(newBase: Context?) {
        var context = newBase
        if (context != null) {
            val prefs = context.getSharedPreferences("timer_prefs", Context.MODE_PRIVATE)
            val languageCode = prefs.getString("language_code", "en") ?: "en"

            if (languageCode.isNotEmpty()) {
                val locale = Locale.forLanguageTag(languageCode)
                val config = Configuration(context.resources.configuration)
                config.setLocale(locale)
                context = context.createConfigurationContext(config)
            }
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by settingsRepository.isDarkMode.collectAsState()

            TimerTheme(darkTheme = isDarkMode) {
                val navController = androidx.navigation.compose.rememberNavController()
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavGraph(navController = navController)
                }
            }
        }
    }
}