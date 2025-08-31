package com.example.zhouyi.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 应用偏好设置
 * 管理用户的各种设置选项
 */
class AppPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

        // 偏好设置键
        private val DAILY_GOAL = intPreferencesKey("daily_goal")
        private val SHOW_NUMBER = booleanPreferencesKey("show_number")
        private val AUTO_NEXT = booleanPreferencesKey("auto_next")
        private val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val FONT_SIZE = intPreferencesKey("font_size")
        private val REINFORCEMENT_MODE = booleanPreferencesKey("reinforcement_mode")
        private val QUIZ_MODE = stringPreferencesKey("quiz_mode")
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    // 每日目标题量
    val dailyGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DAILY_GOAL] ?: 30
    }

    // 是否显示编号
    val showNumber: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SHOW_NUMBER] ?: true
    }

    // 是否自动下一题
    val autoNext: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_NEXT] ?: false
    }

    // 是否启用震动
    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[VIBRATION_ENABLED] ?: true
    }

    // 是否启用音效
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SOUND_ENABLED] ?: false
    }

    // 是否使用深色主题
    val darkTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_THEME] ?: false
    }

    // 字体大小
    val fontSize: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FONT_SIZE] ?: 1 // 1=小, 2=中, 3=大
    }

    // 强化模式
    val reinforcementMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REINFORCEMENT_MODE] ?: false
    }

    // 答题模式
    val quizMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[QUIZ_MODE] ?: "practice" // practice, timed, exam
    }

    // 是否首次启动
    val firstLaunch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH] ?: true
    }

    // 设置每日目标
    suspend fun setDailyGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_GOAL] = goal.coerceIn(1, 100)
        }
    }

    // 设置是否显示编号
    suspend fun setShowNumber(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_NUMBER] = show
        }
    }

    // 设置是否自动下一题
    suspend fun setAutoNext(auto: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_NEXT] = auto
        }
    }

    // 设置是否启用震动
    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    // 设置是否启用音效
    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    // 设置深色主题
    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME] = enabled
        }
    }

    // 设置字体大小
    suspend fun setFontSize(size: Int) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE] = size.coerceIn(1, 3)
        }
    }

    // 设置强化模式
    suspend fun setReinforcementMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REINFORCEMENT_MODE] = enabled
        }
    }

    // 设置答题模式
    suspend fun setQuizMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[QUIZ_MODE] = mode
        }
    }

    // 设置首次启动标志
    suspend fun setFirstLaunch(launched: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = launched
        }
    }

    // 重置所有设置到默认值
    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
            preferences[DAILY_GOAL] = 30
            preferences[SHOW_NUMBER] = true
            preferences[AUTO_NEXT] = false
            preferences[VIBRATION_ENABLED] = true
            preferences[SOUND_ENABLED] = false
            preferences[DARK_THEME] = false
            preferences[FONT_SIZE] = 1
            preferences[REINFORCEMENT_MODE] = false
            preferences[QUIZ_MODE] = "practice"
            preferences[FIRST_LAUNCH] = false
        }
    }
}
