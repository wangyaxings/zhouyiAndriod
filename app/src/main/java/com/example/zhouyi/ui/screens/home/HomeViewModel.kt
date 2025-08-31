package com.example.zhouyi.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zhouyi.data.preferences.AppPreferences
import com.example.zhouyi.data.repository.AttemptRepository
import com.example.zhouyi.data.repository.SrsRepository
import com.example.zhouyi.data.repository.WrongBookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * 主页面ViewModel
 * 管理主页面的状态和数据
 */
class HomeViewModel(
    private val attemptRepository: AttemptRepository,
    private val wrongBookRepository: WrongBookRepository,
    private val srsRepository: SrsRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /**
     * 加载页面数据
     */
    private fun loadData() {
        viewModelScope.launch {
            // 合并多个数据流
            combine(
                attemptRepository.getTodayStatistics(),
                attemptRepository.getOverallStatistics(),
                wrongBookRepository.getWrongBookCount(),
                srsRepository.getTodayDueCount(),
                preferences.dailyGoal,
                preferences.firstLaunch
            ) { todayStats, overallStats, wrongCount, dueCount, dailyGoal, firstLaunch ->
                HomeUiState(
                    todayProgress = createTodayProgress(todayStats, dailyGoal),
                    learningStats = createLearningStats(overallStats),
                    reviewReminder = createReviewReminder(dueCount),
                    wrongCount = wrongCount,
                    dueCount = dueCount,
                    isLoading = false,
                    isFirstLaunch = firstLaunch
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    /**
     * 创建今日进度
     */
    private fun createTodayProgress(
        todayStats: com.example.zhouyi.data.database.AttemptStatistics,
        dailyGoal: Int
    ): TodayProgress? {
        return if (todayStats.total > 0) {
            TodayProgress(
                completedQuestions = todayStats.total,
                dailyGoal = dailyGoal,
                completionRate = (todayStats.total.toFloat() / dailyGoal).coerceAtMost(1f),
                accuracy = todayStats.accuracy
            )
        } else {
            TodayProgress(
                completedQuestions = 0,
                dailyGoal = dailyGoal,
                completionRate = 0f,
                accuracy = 0f
            )
        }
    }

    /**
     * 创建学习统计
     */
    private fun createLearningStats(
        overallStats: com.example.zhouyi.data.database.AttemptStatistics
    ): LearningStats? {
        return if (overallStats.total > 0) {
            LearningStats(
                totalQuestions = overallStats.total,
                accuracy = overallStats.accuracy,
                masteredCount = 0 // 需要从SRS数据获取
            )
        } else {
            null
        }
    }

    /**
     * 创建复习提醒
     */
    private fun createReviewReminder(dueCount: Int): ReviewReminder? {
        return if (dueCount > 0) {
            ReviewReminder(
                message = "今日有 $dueCount 个项目需要复习",
                dueCount = dueCount
            )
        } else {
            null
        }
    }

    /**
     * 刷新数据
     */
    fun refreshData() {
        loadData()
    }

    /**
     * 标记首次启动完成
     */
    fun markFirstLaunchComplete() {
        viewModelScope.launch {
            preferences.setFirstLaunch(false)
        }
    }
}

/**
 * 主页面UI状态
 */
data class HomeUiState(
    val todayProgress: TodayProgress? = null,
    val learningStats: LearningStats? = null,
    val reviewReminder: ReviewReminder? = null,
    val wrongCount: Int = 0,
    val dueCount: Int = 0,
    val isLoading: Boolean = true,
    val isFirstLaunch: Boolean = true
)

/**
 * 今日进度
 */
data class TodayProgress(
    val completedQuestions: Int,
    val dailyGoal: Int,
    val completionRate: Float,
    val accuracy: Double
)

/**
 * 学习统计
 */
data class LearningStats(
    val totalQuestions: Int,
    val accuracy: Double,
    val masteredCount: Int
)

/**
 * 复习提醒
 */
data class ReviewReminder(
    val message: String,
    val dueCount: Int
)
