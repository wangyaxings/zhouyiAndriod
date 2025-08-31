package com.example.zhouyi.ui.screens.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zhouyi.core.service.StatisticsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 统计ViewModel
 */
class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val statisticsService = StatisticsService(application)

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // 加载总体统计
                val overallStats = statisticsService.getOverallStats()

                // 加载学习进度
                val learningProgress = statisticsService.getLearningProgress()

                // 加载SRS统计
                val srsStats = statisticsService.getSrsStats()

                // 加载最近7天统计
                val last7DaysStats = statisticsService.getLast7DaysStats()

                _uiState.value = _uiState.value.copy(
                    overallStats = overallStats,
                    learningProgress = learningProgress,
                    srsStats = srsStats,
                    last7DaysStats = last7DaysStats,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "加载统计数据失败",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 统计UI状态
 */
data class StatisticsUiState(
    val overallStats: StatisticsService.OverallStats? = null,
    val learningProgress: StatisticsService.LearningProgress? = null,
    val srsStats: StatisticsService.SrsStats? = null,
    val last7DaysStats: List<StatisticsService.DailyStats> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
