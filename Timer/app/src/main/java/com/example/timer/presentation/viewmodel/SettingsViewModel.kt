package com.example.timer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.timer.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = settingsRepository.isDarkMode
    val languageCode: StateFlow<String> = settingsRepository.languageCode

    fun toggleTheme(isDark: Boolean) {
        settingsRepository.setDarkMode(isDark)
    }

    fun changeLanguage(code: String) {
        settingsRepository.setLanguage(code)
    }
}