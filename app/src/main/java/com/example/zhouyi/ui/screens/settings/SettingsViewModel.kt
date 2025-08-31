package com.example.zhouyi.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zhouyi.data.preferences.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 设置ViewModel
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val appPreferences = AppPreferences(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            try {
                // 收集所有设置
                appPreferences.dailyGoal.collect { dailyGoal ->
                    appPreferences.showNumber.collect { showNumber ->
                        appPreferences.autoNext.collect { autoNext ->
                            appPreferences.vibration.collect { vibration ->
                                appPreferences.sound.collect { sound ->
                                    appPreferences.darkTheme.collect { darkTheme ->
                                        appPreferences.fontSize.collect { fontSize ->
                                            appPreferences.studyStreak.collect { studyStreak ->
                                                _uiState.value = _uiState.value.copy(
                                                    dailyGoal = dailyGoal,
                                                    showNumber = showNumber,
                                                    autoNext = autoNext,
                                                    vibration = vibration,
                                                    sound = sound,
                                                    darkTheme = darkTheme,
                                                    fontSize = fontSize,
                                                    studyStreak = studyStreak
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "加载设置失败"
                )
            }
        }
    }

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch {
            try {
                appPreferences.setDailyGoal(goal)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "设置每日目标失败"
                )
            }
        }
    }

    fun setShowNumber(show: Boolean) {
        viewModelScope.launch {
            try {
                appPreferences.setShowNumber(show)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "设置显示编号失败"
                )
            }
        }
    }

    fun setAutoNext(auto: Boolean) {
        viewModelScope.launch {
            try {
                appPreferences.setAutoNext(auto)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "设置自动下一题失败"
                )
            }
        }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch {
            try {
                appPreferences.setVibration(enabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "设置震动失败"
                )
            }
        }
    }

    fun setSound(enabled: Boolean) {
        viewModelScope.launch {
            try {
                appPreferences.setSound(enabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "设置音效失败"
                )
            }
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            try {
                appPreferences.setDarkTheme(enabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "设置深色主题失败"
                )
            }
        }
    }

    fun setFontSize(size: Int) {
        viewModelScope.launch {
            try {
                appPreferences.setFontSize(size)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "设置字体大小失败"
                )
            }
        }
    }

    fun resetStudyStats() {
        viewModelScope.launch {
            try {
                appPreferences.resetStudyStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "重置学习统计失败"
                )
            }
        }
    }

    fun showDailyGoalDialog() {
        _uiState.value = _uiState.value.copy(showDailyGoalDialog = true)
    }

    fun hideDailyGoalDialog() {
        _uiState.value = _uiState.value.copy(showDailyGoalDialog = false)
    }

    fun showFontSizeDialog() {
        _uiState.value = _uiState.value.copy(showFontSizeDialog = true)
    }

    fun hideFontSizeDialog() {
        _uiState.value = _uiState.value.copy(showFontSizeDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 设置UI状态
 */
data class SettingsUiState(
    val dailyGoal: Int = 30,
    val showNumber: Boolean = true,
    val autoNext: Boolean = false,
    val vibration: Boolean = true,
    val sound: Boolean = false,
    val darkTheme: Boolean = false,
    val fontSize: Int = 16,
    val studyStreak: Int = 0,
    val showDailyGoalDialog: Boolean = false,
    val showFontSizeDialog: Boolean = false,
    val error: String? = null
)
