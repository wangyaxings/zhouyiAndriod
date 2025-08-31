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
                val overall = attemptRepository.getOverallStatistics().first()

                // SRS 统计
                val bucketCounts = srsRepository.getSrsStatistics() // Map<Int, Int>
                val totalItems = bucketCounts.values.sum()
                val masteredItems = bucketCounts[5] ?: 0
                val dueItems = srsRepository.getDueHexagramIds().size

                // 学习进度
                val learningProgress = calculateLearningProgress(totalItems, masteredItems, dueItems)

                // 时间统计（根据答题记录简单统计）
                val timeStats = calculateTimeStatsFromAttempts()

                // 错题统计
                val wrongBooks = wrongBookRepository.getAllWrongBook().first()
                val wrongStats = WrongStats(
                    totalWrong = wrongBooks.size,
                    highFrequencyWrong = wrongBooks.count { it.wrongCount >= 3 },
                    resolvedWrong = 0,
                    mostWrongHexagram = wrongBooks.maxByOrNull { it.wrongCount }?.hexagramId?.toString(),
                    mostWrongCount = wrongBooks.maxByOrNull { it.wrongCount }?.wrongCount ?: 0
                )

                _uiState.value = _uiState.value.copy(
                    overallStats = OverallStats(
                        totalQuestions = overall.total,
                        accuracy = overall.accuracy,
                        studyDays = calculateStudyDays()
                    ),
                    learningProgress = learningProgress,
                    srsStats = SrsStats(
                        totalItems = totalItems,
                        dueItems = dueItems,
                        masteredItems = masteredItems,
                        recommendation = if (dueItems > 0) "今日有 $dueItems 项待复习" else "继续保持！"
                    ),
                    timeStats = timeStats,
                    wrongStats = wrongStats,
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
    private fun calculateLearningProgress(totalItems: Int, masteredCount: Int, dueItems: Int): LearningProgress {
        if (totalItems == 0) return LearningProgress(0f, 0f, 0f, 0, 0, 0, 0)
        val learningCount = totalItems - dueItems - masteredCount
        val reviewCount = dueItems
        return LearningProgress(
            masteredPercentage = (masteredCount * 100.0f / totalItems),
            learningPercentage = (learningCount * 100.0f / totalItems),
            reviewPercentage = (reviewCount * 100.0f / totalItems),
            masteredCount = masteredCount,
            learningCount = learningCount,
            reviewCount = reviewCount,
            totalCount = totalItems
        )
    }

    /**
     * 计算时间统计
     */
    private suspend fun getAttemptsInRange(start: Long, end: Long): Int {
        val all = attemptRepository.getAllAttempts().first()
        return all.count { it.timestamp in start until end }
    }

    private suspend fun calculateTimeStatsFromAttempts(): TimeStats {
        val now = System.currentTimeMillis()
        val day = 24L * 60 * 60 * 1000
        val cal = java.util.Calendar.getInstance()

        // today
        val startOfDay = cal.apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        val today = getAttemptsInRange(startOfDay, startOfDay + day)

        // week (last 7 days)
        val week = getAttemptsInRange(now - 7 * day, now)

        // month (last 30 days)
        val month = getAttemptsInRange(now - 30 * day, now)

        return TimeStats(
            todayQuestions = today,
            weekQuestions = week,
            monthQuestions = month,
            averageTimePerQuestion = 0.0
        )
    }

    /**
     * 计算学习天数
     */
    private fun calculateStudyDays(): Int = 0

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
