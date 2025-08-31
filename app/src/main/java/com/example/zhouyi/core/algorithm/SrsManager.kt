package com.example.zhouyi.core.algorithm

import com.example.zhouyi.data.model.SrsState
import java.util.*

/**
 * 间隔复习管理器
 * 实现Leitner系统简化版
 */
class SrsManager {

    companion object {
        // Leitner系统的复习间隔（毫秒）
        val BUCKET_INTERVALS = mapOf(
            1 to 0L, // 盒1：立即复习
            2 to 24 * 60 * 60 * 1000L, // 盒2：1天后
            3 to 3 * 24 * 60 * 60 * 1000L, // 盒3：3天后
            4 to 7 * 24 * 60 * 60 * 1000L, // 盒4：7天后
            5 to 14 * 24 * 60 * 60 * 1000L // 盒5：14天后
        )

        const val MAX_BUCKET = 5
        const val DEFAULT_BUCKET = 2
        const val MIN_BUCKET = 1
    }

    /**
     * 处理答题结果
     * @param hexagramId 卦象ID
     * @param isCorrect 是否答对
     * @param currentTime 当前时间戳
     * @return 更新后的SRS状态
     */
    fun processAnswer(
        hexagramId: Int,
        isCorrect: Boolean,
        currentTime: Long = System.currentTimeMillis()
    ): SrsState {
        return if (isCorrect) {
            processCorrectAnswer(hexagramId, currentTime)
        } else {
            processWrongAnswer(hexagramId, currentTime)
        }
    }

    /**
     * 处理答对的情况
     * 答对→进1盒（最高5）
     */
    private fun processCorrectAnswer(hexagramId: Int, currentTime: Long): SrsState {
        // 这里应该从数据库获取当前状态，暂时创建新状态
        val currentState = SrsState(
            hexagramId = hexagramId,
            bucket = DEFAULT_BUCKET, // 默认从盒2开始
            dueTimestamp = currentTime,
            lastReviewTimestamp = currentTime,
            consecutiveCorrect = 0,
            totalReviews = 0
        )

        val newBucket = minOf(MAX_BUCKET, currentState.bucket + 1)
        val interval = BUCKET_INTERVALS[newBucket] ?: 0L
        val newDueTimestamp = currentTime + interval

        return currentState.copy(
            bucket = newBucket,
            dueTimestamp = newDueTimestamp,
            lastReviewTimestamp = currentTime,
            consecutiveCorrect = currentState.consecutiveCorrect + 1,
            totalReviews = currentState.totalReviews + 1
        )
    }

    /**
     * 处理答错的情况
     * 答错→回盒1
     */
    private fun processWrongAnswer(hexagramId: Int, currentTime: Long): SrsState {
        // 这里应该从数据库获取当前状态，暂时创建新状态
        val currentState = SrsState(
            hexagramId = hexagramId,
            bucket = DEFAULT_BUCKET, // 默认从盒2开始
            dueTimestamp = currentTime,
            lastReviewTimestamp = currentTime,
            consecutiveCorrect = 0,
            totalReviews = 0
        )

        val interval = BUCKET_INTERVALS[MIN_BUCKET] ?: 0L // 盒1间隔
        val newDueTimestamp = currentTime + interval

        return currentState.copy(
            bucket = MIN_BUCKET, // 回到盒1
            dueTimestamp = newDueTimestamp,
            lastReviewTimestamp = currentTime,
            consecutiveCorrect = 0, // 重置连续答对次数
            totalReviews = currentState.totalReviews + 1
        )
    }

    /**
     * 初始化新卦象的SRS状态
     * 新题默认放入盒2
     */
    fun initializeSrsState(
        hexagramId: Int,
        currentTime: Long = System.currentTimeMillis()
    ): SrsState {
        val interval = BUCKET_INTERVALS[DEFAULT_BUCKET] ?: 0L // 盒2间隔
        val dueTimestamp = currentTime + interval

        return SrsState(
            hexagramId = hexagramId,
            bucket = DEFAULT_BUCKET, // 新题默认放入盒2
            dueTimestamp = dueTimestamp,
            lastReviewTimestamp = 0L, // 从未复习过
            consecutiveCorrect = 0,
            totalReviews = 0
        )
    }

    /**
     * 检查卦象是否到了复习时间
     */
    fun isDue(srsState: SrsState, currentTime: Long = System.currentTimeMillis()): Boolean {
        return currentTime >= srsState.dueTimestamp
    }

    /**
     * 获取距离下次复习的时间
     */
    fun getTimeUntilDue(srsState: SrsState, currentTime: Long = System.currentTimeMillis()): Long {
        return maxOf(0L, srsState.dueTimestamp - currentTime)
    }

    /**
     * 获取距离下次复习的天数
     */
    fun getDaysUntilDue(srsState: SrsState, currentTime: Long = System.currentTimeMillis()): Int {
        val timeUntilDue = getTimeUntilDue(srsState, currentTime)
        return (timeUntilDue / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * 获取复习优先级
     * 优先级：盒序低→高；同盒按due_ts升序
     */
    fun getReviewPriority(srsStates: List<SrsState>): List<SrsState> {
        return srsStates.sortedWith(
            compareBy<SrsState> { it.bucket }
                .thenBy { it.dueTimestamp }
        )
    }

    /**
     * 获取今日需要复习的项目
     */
    fun getTodayDueItems(
        srsStates: List<SrsState>,
        currentTime: Long = System.currentTimeMillis()
    ): List<SrsState> {
        val todayEnd = getTodayEnd(currentTime)
        return srsStates.filter { it.dueTimestamp <= todayEnd }
    }

    /**
     * 获取今日结束时间戳
     */
    private fun getTodayEnd(currentTime: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    /**
     * 获取复习间隔描述
     */
    fun getIntervalDescription(bucket: Int): String {
        return when (bucket) {
            1 -> "立即复习"
            2 -> "1天后"
            3 -> "3天后"
            4 -> "7天后"
            5 -> "14天后"
            else -> "未知"
        }
    }

    /**
     * 获取SRS统计信息
     */
    fun getSrsStatistics(srsStates: List<SrsState>): SrsStatistics {
        val bucketCounts = srsStates.groupBy { it.bucket }
            .mapValues { it.value.size }

        val totalItems = srsStates.size
        val dueItems = srsStates.count { isDue(it) }
        val todayDueItems = getTodayDueItems(srsStates).size
        val masteredItems = srsStates.count { it.bucket == MAX_BUCKET }

        return SrsStatistics(
            totalItems = totalItems,
            dueItems = dueItems,
            todayDueItems = todayDueItems,
            masteredItems = masteredItems,
            bucketDistribution = bucketCounts
        )
    }

    /**
     * 检查卦象是否已掌握（在最高级盒子中）
     */
    fun isMastered(srsState: SrsState): Boolean {
        return srsState.bucket == MAX_BUCKET
    }

    /**
     * 获取学习进度百分比
     */
    fun getLearningProgress(srsStates: List<SrsState>): LearningProgress {
        val totalItems = srsStates.size
        if (totalItems == 0) {
            return LearningProgress(0f, 0f, 0f)
        }

        val masteredItems = srsStates.count { isMastered(it) }
        val learningItems = srsStates.count { it.bucket in 2..4 }
        val reviewItems = srsStates.count { it.bucket == MIN_BUCKET }

        val masteredPercentage = (masteredItems * 100.0f / totalItems)
        val learningPercentage = (learningItems * 100.0f / totalItems)
        val reviewPercentage = (reviewItems * 100.0f / totalItems)

        return LearningProgress(
            masteredPercentage = masteredPercentage,
            learningPercentage = learningPercentage,
            reviewPercentage = reviewPercentage
        )
    }

    /**
     * 获取复习建议
     * 根据SRS状态生成复习建议
     */
    fun getReviewRecommendation(srsStates: List<SrsState>): ReviewRecommendation {
        val todayDueItems = getTodayDueItems(srsStates)
        val overdueItems = srsStates.filter { isDue(it) && it.dueTimestamp < getTodayEnd(System.currentTimeMillis()) }

        val recommendation = when {
            overdueItems.isNotEmpty() -> "有${overdueItems.size}个项目已逾期，建议优先复习"
            todayDueItems.isNotEmpty() -> "今日有${todayDueItems.size}个项目需要复习"
            else -> "今日暂无复习项目"
        }

        val priorityItems = getReviewPriority(srsStates.filter { isDue(it) })

        return ReviewRecommendation(
            recommendation = recommendation,
            todayDueCount = todayDueItems.size,
            overdueCount = overdueItems.size,
            priorityItems = priorityItems.take(10) // 最多显示10个优先项目
        )
    }

    /**
     * 验证SRS系统有效性
     * 检查复习间隔是否合理
     */
    fun validateSrsSystem(srsStates: List<SrsState>): SrsValidationResult {
        val now = System.currentTimeMillis()
        val dueItems = srsStates.filter { isDue(it, now) }
        val overdueItems = dueItems.filter { it.dueTimestamp < now - 24 * 60 * 60 * 1000L } // 逾期1天以上

        val averageBucket = srsStates.map { it.bucket }.average()
        val averageReviews = srsStates.map { it.totalReviews }.average()

        return SrsValidationResult(
            totalItems = srsStates.size,
            dueItems = dueItems.size,
            overdueItems = overdueItems.size,
            averageBucket = averageBucket,
            averageReviews = averageReviews,
            isHealthy = overdueItems.size <= srsStates.size * 0.1 // 逾期项目不超过10%
        )
    }
}

/**
 * SRS统计信息数据类
 */
data class SrsStatistics(
    val totalItems: Int,
    val dueItems: Int,
    val todayDueItems: Int,
    val masteredItems: Int,
    val bucketDistribution: Map<Int, Int>
)

/**
 * 学习进度数据类
 */
data class LearningProgress(
    val masteredPercentage: Float,
    val learningPercentage: Float,
    val reviewPercentage: Float
)

/**
 * 复习建议数据类
 */
data class ReviewRecommendation(
    val recommendation: String,
    val todayDueCount: Int,
    val overdueCount: Int,
    val priorityItems: List<SrsState>
)

/**
 * SRS系统验证结果
 */
data class SrsValidationResult(
    val totalItems: Int,
    val dueItems: Int,
    val overdueItems: Int,
    val averageBucket: Double,
    val averageReviews: Double,
    val isHealthy: Boolean
)
