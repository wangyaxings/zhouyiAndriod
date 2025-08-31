package com.example.zhouyi.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zhouyi.data.repository.AttemptRepository
import com.example.zhouyi.data.repository.SrsRepository
import com.example.zhouyi.data.repository.WrongBookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * 统计页面ViewModel
 * 管理统计数据和计算
 */
class StatisticsViewModel(
    private val attemptRepository: AttemptRepository,
    private val srsRepository: SrsRepository,
    private val wrongBookRepository: WrongBookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    /**
     * 加载统计数据
     */
    fun loadStatistics() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // 并行加载各种统计数据
                val overallStats = attemptRepository.getOverallStatistics().first()
                val srsStats = srsRepository.getSrsStatistics().first()
                val wrongStats = wrongBookRepository.getWrongBookStatistics().first()

                // 计算学习进度
                val learningProgress = calculateLearningProgress(srsStats)

                // 计算时间统计
                val timeStats = calculateTimeStats(overallStats)

                _uiState.value = _uiState.value.copy(
                    overallStats = OverallStats(
                        totalQuestions = overallStats.total,
                        accuracy = overallStats.accuracy,
                        studyDays = calculateStudyDays(overallStats)
                    ),
                    learningProgress = learningProgress,
                    srsStats = SrsStats(
                        totalItems = srsStats.totalItems,
                        dueItems = srsStats.dueItems,
                        masteredItems = srsStats.masteredItems,
                        recommendation = srsStats.recommendation ?: ""
                    ),
                    timeStats = timeStats,
                    wrongStats = WrongStats(
                        totalWrong = wrongStats.totalCount,
                        highFrequencyWrong = wrongStats.highFrequencyCount,
                        resolvedWrong = wrongStats.resolvedCount,
                        mostWrongHexagram = wrongStats.mostWrongHexagram,
                        mostWrongCount = wrongStats.mostWrongCount
                    ),
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

    /**
     * 计算学习进度
     */
    private fun calculateLearningProgress(srsStats: com.example.zhouyi.data.database.SrsStatistics): LearningProgress {
        val totalItems = srsStats.totalItems
        if (totalItems == 0) {
            return LearningProgress(0f, 0f, 0f, 0, 0, 0, 0)
        }

        val masteredCount = srsStats.masteredItems
        val learningCount = srsStats.totalItems - srsStats.dueItems - masteredCount
        val reviewCount = srsStats.dueItems

        val masteredPercentage = (masteredCount * 100.0f / totalItems)
        val learningPercentage = (learningCount * 100.0f / totalItems)
        val reviewPercentage = (reviewCount * 100.0f / totalItems)

        return LearningProgress(
            masteredPercentage = masteredPercentage,
            learningPercentage = learningPercentage,
            reviewPercentage = reviewPercentage,
            masteredCount = masteredCount,
            learningCount = learningCount,
            reviewCount = reviewCount,
            totalCount = totalItems
        )
    }

    /**
     * 计算时间统计
     */
    private fun calculateTimeStats(overallStats: com.example.zhouyi.data.database.AttemptStatistics): TimeStats {
        // 这里需要从答题记录中计算时间相关的统计
        // 暂时使用模拟数据
        return TimeStats(
            todayQuestions = overallStats.todayTotal ?: 0,
            weekQuestions = overallStats.weekTotal ?: 0,
            monthQuestions = overallStats.monthTotal ?: 0,
            averageTimePerQuestion = 15.5 // 模拟平均答题时间
        )
    }

    /**
     * 计算学习天数
     */
    private fun calculateStudyDays(overallStats: com.example.zhouyi.data.database.AttemptStatistics): Int {
        // 这里需要从答题记录中计算实际的学习天数
        // 暂时使用模拟数据
        return 30 // 模拟学习30天
    }

    /**
     * 刷新数据
     */
    fun refreshData() {
        loadStatistics()
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 统计页面UI状态
 */
data class StatisticsUiState(
    val overallStats: OverallStats? = null,
    val learningProgress: LearningProgress? = null,
    val srsStats: SrsStats? = null,
    val timeStats: TimeStats? = null,
    val wrongStats: WrongStats? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 总体统计
 */
data class OverallStats(
    val totalQuestions: Int,
    val accuracy: Double,
    val studyDays: Int
)

/**
 * 学习进度
 */
data class LearningProgress(
    val masteredPercentage: Float,
    val learningPercentage: Float,
    val reviewPercentage: Float,
    val masteredCount: Int,
    val learningCount: Int,
    val reviewCount: Int,
    val totalCount: Int
)

/**
 * SRS统计
 */
data class SrsStats(
    val totalItems: Int,
    val dueItems: Int,
    val masteredItems: Int,
    val recommendation: String
)

/**
 * 时间统计
 */
data class TimeStats(
    val todayQuestions: Int,
    val weekQuestions: Int,
    val monthQuestions: Int,
    val averageTimePerQuestion: Double
)

/**
 * 错题统计
 */
data class WrongStats(
    val totalWrong: Int,
    val highFrequencyWrong: Int,
    val resolvedWrong: Int,
    val mostWrongHexagram: String?,
    val mostWrongCount: Int
)
