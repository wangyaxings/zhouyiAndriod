package com.example.zhouyi.core.service

import android.content.Context
import com.example.zhouyi.data.repository.AttemptRepository
import com.example.zhouyi.data.repository.HexagramRepository
import com.example.zhouyi.data.repository.SrsRepository
import com.example.zhouyi.data.repository.WrongBookRepository
import kotlinx.coroutines.flow.first

/**
 * 统计服务
 * 提供各种学习统计数据
 */
class StatisticsService(context: Context) {

    private val attemptRepository = AttemptRepository(context)
    private val hexagramRepository = HexagramRepository(context)
    private val srsRepository = SrsRepository(context)
    private val wrongBookRepository = WrongBookRepository(context)

    /**
     * 获取总体统计
     */
    suspend fun getOverallStats(): OverallStats {
        val allHexagrams = hexagramRepository.getAllHexagrams().first()
        val totalHexagrams = allHexagrams.size

        val totalAttempts = attemptRepository.getAllAttempts().first().size
        val totalCorrect = attemptRepository.getAllAttempts().first().count { it.isCorrect }
        val overallAccuracy = if (totalAttempts > 0) totalCorrect.toFloat() / totalAttempts else 0f

        val wrongCount = wrongBookRepository.getWrongBookCount()
        val masteredCount = srsRepository.getSrsStatistics().bucketCounts[5] ?: 0

        return OverallStats(
            totalHexagrams = totalHexagrams,
            totalAttempts = totalAttempts,
            totalCorrect = totalCorrect,
            overallAccuracy = overallAccuracy,
            wrongCount = wrongCount,
            masteredCount = masteredCount,
            learningProgress = (masteredCount.toFloat() / totalHexagrams) * 100
        )
    }

    /**
     * 获取今日统计
     */
    suspend fun getTodayStats(): DailyStats {
        val today = System.currentTimeMillis()
        val oneDayAgo = today - (24 * 60 * 60 * 1000)

        val totalAttempts = attemptRepository.getTotalCountSince(oneDayAgo)
        val correctAttempts = attemptRepository.getCorrectCountSince(oneDayAgo)
        val accuracy = attemptRepository.getAccuracySince(oneDayAgo)
        val studiedHexagrams = attemptRepository.getStudiedHexagramsSince(oneDayAgo).size

        return DailyStats(
            date = today,
            totalAttempts = totalAttempts,
            correctAttempts = correctAttempts,
            accuracy = accuracy,
            studiedHexagrams = studiedHexagrams
        )
    }

    /**
     * 获取最近7天统计
     */
    suspend fun getLast7DaysStats(): List<DailyStats> {
        val today = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000L

        return (0..6).map { daysAgo ->
            val startTime = today - (daysAgo * oneDayInMillis)
            val endTime = if (daysAgo == 0) today else startTime + oneDayInMillis

            val totalAttempts = attemptRepository.getAllAttempts().first()
                .filter { it.timestamp >= startTime && it.timestamp < endTime }.size
            val correctAttempts = attemptRepository.getAllAttempts().first()
                .filter { it.timestamp >= startTime && it.timestamp < endTime && it.isCorrect }.size
            val accuracy = if (totalAttempts > 0) correctAttempts.toFloat() / totalAttempts else 0f
            val studiedHexagrams = attemptRepository.getAllAttempts().first()
                .filter { it.timestamp >= startTime && it.timestamp < endTime }
                .map { it.hexagramId }.distinct().size

            DailyStats(
                date = startTime,
                totalAttempts = totalAttempts,
                correctAttempts = correctAttempts,
                accuracy = accuracy,
                studiedHexagrams = studiedHexagrams
            )
        }.reversed()
    }

    /**
     * 获取SRS统计
     */
    suspend fun getSrsStats(): SrsStats {
        val bucketCounts = srsRepository.getSrsStatistics()
        val totalInSrs = bucketCounts.values.sum()
        val masteredCount = bucketCounts[5] ?: 0
        val dueCount = srsRepository.getDueHexagramIds().size

        return SrsStats(
            bucketCounts = bucketCounts,
            totalInSrs = totalInSrs,
            masteredCount = masteredCount,
            dueCount = dueCount,
            masteryRate = if (totalInSrs > 0) (masteredCount.toFloat() / totalInSrs) * 100 else 0f
        )
    }

    /**
     * 获取错题统计
     */
    suspend fun getWrongBookStats(): WrongBookStats {
        val allWrongBooks = wrongBookRepository.getAllWrongBook().first()
        val totalWrong = allWrongBooks.size

        val wrongByFrequency = allWrongBooks.groupBy { it.wrongCount }
        val mostWrongCount = allWrongBooks.maxByOrNull { it.wrongCount }?.wrongCount ?: 0
        val averageWrongCount = if (totalWrong > 0) allWrongBooks.sumOf { it.wrongCount }.toFloat() / totalWrong else 0f

        return WrongBookStats(
            totalWrong = totalWrong,
            wrongByFrequency = wrongByFrequency,
            mostWrongCount = mostWrongCount,
            averageWrongCount = averageWrongCount
        )
    }

    /**
     * 获取学习进度
     */
    suspend fun getLearningProgress(): LearningProgress {
        val allHexagrams = hexagramRepository.getAllHexagrams().first()
        val totalHexagrams = allHexagrams.size

        val studiedHexagrams = attemptRepository.getAllAttempts().first()
            .map { it.hexagramId }.distinct().size

        val masteredHexagrams = srsRepository.getSrsStatistics().bucketCounts[5] ?: 0
        val wrongHexagrams = wrongBookRepository.getWrongBookCount()

        return LearningProgress(
            totalHexagrams = totalHexagrams,
            studiedHexagrams = studiedHexagrams,
            masteredHexagrams = masteredHexagrams,
            wrongHexagrams = wrongHexagrams,
            studyRate = (studiedHexagrams.toFloat() / totalHexagrams) * 100,
            masteryRate = (masteredHexagrams.toFloat() / totalHexagrams) * 100
        )
    }

    /**
     * 总体统计数据类
     */
    data class OverallStats(
        val totalHexagrams: Int,
        val totalAttempts: Int,
        val totalCorrect: Int,
        val overallAccuracy: Float,
        val wrongCount: Int,
        val masteredCount: Int,
        val learningProgress: Float
    )

    /**
     * 每日统计数据类
     */
    data class DailyStats(
        val date: Long,
        val totalAttempts: Int,
        val correctAttempts: Int,
        val accuracy: Float,
        val studiedHexagrams: Int
    )

    /**
     * SRS统计数据类
     */
    data class SrsStats(
        val bucketCounts: Map<Int, Int>,
        val totalInSrs: Int,
        val masteredCount: Int,
        val dueCount: Int,
        val masteryRate: Float
    )

    /**
     * 错题统计数据类
     */
    data class WrongBookStats(
        val totalWrong: Int,
        val wrongByFrequency: Map<Int, List<com.example.zhouyi.data.model.WrongBook>>,
        val mostWrongCount: Int,
        val averageWrongCount: Float
    )

    /**
     * 学习进度数据类
     */
    data class LearningProgress(
        val totalHexagrams: Int,
        val studiedHexagrams: Int,
        val masteredHexagrams: Int,
        val wrongHexagrams: Int,
        val studyRate: Float,
        val masteryRate: Float
    )
}
