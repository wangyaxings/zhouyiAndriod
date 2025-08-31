package com.example.zhouyi.data.database

import androidx.room.*
import com.example.zhouyi.data.model.Attempt
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * 答题记录数据访问接口
 */
@Dao
interface AttemptDao {

    /**
     * 获取所有答题记录
     */
    @Query("SELECT * FROM attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<Attempt>>

    /**
     * 根据卦象ID获取答题记录
     */
    @Query("SELECT * FROM attempts WHERE hexagramId = :hexagramId ORDER BY timestamp DESC")
    suspend fun getAttemptsByHexagramId(hexagramId: Int): List<Attempt>

    /**
     * 获取指定时间范围内的答题记录
     */
    @Query("SELECT * FROM attempts WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    suspend fun getAttemptsByTimeRange(startTime: Long, endTime: Long): List<Attempt>

    /**
     * 获取今日答题记录
     */
    @Query("SELECT * FROM attempts WHERE date(timestamp/1000, 'unixepoch') = date('now') ORDER BY timestamp DESC")
    suspend fun getTodayAttempts(): List<Attempt>

    /**
     * 获取最近7天的答题记录
     */
    @Query("SELECT * FROM attempts WHERE timestamp >= :sevenDaysAgo ORDER BY timestamp DESC")
    suspend fun getRecentAttempts(sevenDaysAgo: Long): List<Attempt>

    /**
     * 获取指定模式的答题记录
     */
    @Query("SELECT * FROM attempts WHERE mode = :mode ORDER BY timestamp DESC")
    suspend fun getAttemptsByMode(mode: String): List<Attempt>

    /**
     * 获取答题统计信息
     */
    @Query("SELECT COUNT(*) as total, SUM(CASE WHEN isCorrect THEN 1 ELSE 0 END) as correct FROM attempts")
    suspend fun getAttemptStatistics(): AttemptStatistics

    /**
     * 获取今日答题统计
     */
    @Query("SELECT COUNT(*) as total, SUM(CASE WHEN isCorrect THEN 1 ELSE 0 END) as correct FROM attempts WHERE date(timestamp/1000, 'unixepoch') = date('now')")
    suspend fun getTodayStatistics(): AttemptStatistics

    /**
     * 插入答题记录
     */
    @Insert
    suspend fun insertAttempt(attempt: Attempt)

    /**
     * 批量插入答题记录
     */
    @Insert
    suspend fun insertAttempts(attempts: List<Attempt>)

    /**
     * 更新答题记录
     */
    @Update
    suspend fun updateAttempt(attempt: Attempt)

    /**
     * 删除答题记录
     */
    @Delete
    suspend fun deleteAttempt(attempt: Attempt)

    /**
     * 清空所有答题记录
     */
    @Query("DELETE FROM attempts")
    suspend fun deleteAllAttempts()

    /**
     * 获取答题记录总数
     */
    @Query("SELECT COUNT(*) FROM attempts")
    suspend fun getAttemptCount(): Int

    /**
     * 获取指定卦象的答题正确率
     */
    @Query("SELECT (SUM(CASE WHEN isCorrect THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) as accuracy FROM attempts WHERE hexagramId = :hexagramId")
    suspend fun getAccuracyByHexagramId(hexagramId: Int): Double?

    /**
     * 获取最近答错的卦象ID列表
     */
    @Query("SELECT DISTINCT hexagramId FROM attempts WHERE isCorrect = 0 ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentWrongHexagramIds(limit: Int): List<Int>
}

/**
 * 答题统计信息数据类
 */
data class AttemptStatistics(
    val total: Int,
    val correct: Int
) {
    val accuracy: Double
        get() = if (total > 0) (correct * 100.0 / total) else 0.0
}
