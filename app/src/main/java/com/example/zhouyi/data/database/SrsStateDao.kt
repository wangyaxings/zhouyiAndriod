package com.example.zhouyi.data.database

import androidx.room.*
import com.example.zhouyi.data.model.SrsState
import kotlinx.coroutines.flow.Flow

/**
 * 间隔复习状态数据访问接口
 */
@Dao
interface SrsStateDao {

    /**
     * 获取所有SRS状态
     */
    @Query("SELECT * FROM srs_states ORDER BY bucket ASC, dueTimestamp ASC")
    fun getAllSrsStates(): Flow<List<SrsState>>

    /**
     * 根据卦象ID获取SRS状态
     */
    @Query("SELECT * FROM srs_states WHERE hexagramId = :hexagramId")
    suspend fun getSrsStateByHexagramId(hexagramId: Int): SrsState?

    /**
     * 获取指定盒子的SRS状态
     */
    @Query("SELECT * FROM srs_states WHERE bucket = :bucket ORDER BY dueTimestamp ASC")
    suspend fun getSrsStatesByBucket(bucket: Int): List<SrsState>

    /**
     * 获取到期的复习项目
     */
    @Query("SELECT * FROM srs_states WHERE dueTimestamp <= :currentTime ORDER BY dueTimestamp ASC")
    suspend fun getDueSrsStates(currentTime: Long): List<SrsState>

    /**
     * 获取今日需要复习的项目
     */
    @Query("SELECT * FROM srs_states WHERE dueTimestamp <= :todayEnd ORDER BY bucket ASC, dueTimestamp ASC")
    suspend fun getTodayDueSrsStates(todayEnd: Long): List<SrsState>

    /**
     * 获取指定盒子中到期的项目
     */
    @Query("SELECT * FROM srs_states WHERE bucket = :bucket AND dueTimestamp <= :currentTime ORDER BY dueTimestamp ASC")
    suspend fun getDueSrsStatesByBucket(bucket: Int, currentTime: Long): List<SrsState>

    /**
     * 获取SRS统计信息
     */
    @Query("SELECT bucket, COUNT(*) as count FROM srs_states GROUP BY bucket ORDER BY bucket")
    suspend fun getSrsStatistics(): List<SrsBucketStatistics>

    /**
     * 获取今日到期复习数量
     */
    @Query("SELECT COUNT(*) FROM srs_states WHERE dueTimestamp <= :todayEnd")
    suspend fun getTodayDueCount(todayEnd: Long): Int

    /**
     * 插入SRS状态
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSrsState(srsState: SrsState)

    /**
     * 批量插入SRS状态
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSrsStates(srsStates: List<SrsState>)

    /**
     * 更新SRS状态
     */
    @Update
    suspend fun updateSrsState(srsState: SrsState)

    /**
     * 删除SRS状态
     */
    @Delete
    suspend fun deleteSrsState(srsState: SrsState)

    /**
     * 根据卦象ID删除SRS状态
     */
    @Query("DELETE FROM srs_states WHERE hexagramId = :hexagramId")
    suspend fun deleteSrsStateByHexagramId(hexagramId: Int)

    /**
     * 清空所有SRS状态
     */
    @Query("DELETE FROM srs_states")
    suspend fun deleteAllSrsStates()

    /**
     * 更新卦象的SRS状态（答对时）
     */
    @Query("UPDATE srs_states SET bucket = CASE WHEN bucket < 5 THEN bucket + 1 ELSE bucket END, dueTimestamp = :dueTimestamp, lastReviewTimestamp = :reviewTimestamp, consecutiveCorrect = consecutiveCorrect + 1, totalReviews = totalReviews + 1 WHERE hexagramId = :hexagramId")
    suspend fun updateSrsStateOnCorrect(hexagramId: Int, dueTimestamp: Long, reviewTimestamp: Long)

    /**
     * 更新卦象的SRS状态（答错时）
     */
    @Query("UPDATE srs_states SET bucket = 1, dueTimestamp = :dueTimestamp, lastReviewTimestamp = :reviewTimestamp, consecutiveCorrect = 0, totalReviews = totalReviews + 1 WHERE hexagramId = :hexagramId")
    suspend fun updateSrsStateOnWrong(hexagramId: Int, dueTimestamp: Long, reviewTimestamp: Long)

    /**
     * 初始化卦象的SRS状态（新题）
     */
    @Query("INSERT OR REPLACE INTO srs_states (hexagramId, bucket, dueTimestamp, lastReviewTimestamp, consecutiveCorrect, totalReviews) VALUES (:hexagramId, 2, :dueTimestamp, 0, 0, 0)")
    suspend fun initializeSrsState(hexagramId: Int, dueTimestamp: Long)

    /**
     * 获取SRS状态总数
     */
    @Query("SELECT COUNT(*) FROM srs_states")
    suspend fun getSrsStateCount(): Int

    /**
     * 获取指定盒子的SRS状态数量
     */
    @Query("SELECT COUNT(*) FROM srs_states WHERE bucket = :bucket")
    suspend fun getSrsStateCountByBucket(bucket: Int): Int
}

/**
 * SRS盒子统计信息数据类
 */
data class SrsBucketStatistics(
    val bucket: Int,
    val count: Int
)
