package com.example.timer.domain.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("timer_prefs", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(isDark: Boolean) {
        prefs.edit { putBoolean("is_dark_mode", isDark) }
        _isDarkMode.value = isDark
    }
}