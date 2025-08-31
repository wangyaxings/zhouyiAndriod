package com.example.zhouyi.data.database

import androidx.room.*
import com.example.zhouyi.data.model.WrongBook
import kotlinx.coroutines.flow.Flow

/**
 * 错题本数据访问接口
 */
@Dao
interface WrongBookDao {

    /**
     * 获取所有错题记录
     */
    @Query("SELECT * FROM wrong_book ORDER BY wrongCount DESC, lastWrongTimestamp DESC")
    fun getAllWrongBooks(): Flow<List<WrongBook>>

    /**
     * 根据卦象ID获取错题记录
     */
    @Query("SELECT * FROM wrong_book WHERE hexagramId = :hexagramId")
    suspend fun getWrongBookByHexagramId(hexagramId: Int): WrongBook?

    /**
     * 获取错题数量
     */
    @Query("SELECT COUNT(*) FROM wrong_book")
    suspend fun getWrongBookCount(): Int

    /**
     * 获取最近7天的错题
     */
    @Query("SELECT * FROM wrong_book WHERE lastWrongTimestamp >= :sevenDaysAgo ORDER BY lastWrongTimestamp DESC")
    suspend fun getRecentWrongBooks(sevenDaysAgo: Long): List<WrongBook>

    /**
     * 获取高频错题（错误次数最多的）
     */
    @Query("SELECT * FROM wrong_book ORDER BY wrongCount DESC LIMIT :limit")
    suspend fun getHighFrequencyWrongBooks(limit: Int): List<WrongBook>

    /**
     * 获取错题ID列表
     */
    @Query("SELECT hexagramId FROM wrong_book ORDER BY wrongCount DESC")
    suspend fun getWrongHexagramIds(): List<Int>

    /**
     * 获取最近答错的卦象ID列表
     */
    @Query("SELECT hexagramId FROM wrong_book ORDER BY lastWrongTimestamp DESC LIMIT :limit")
    suspend fun getRecentWrongHexagramIds(limit: Int): List<Int>

    /**
     * 插入或更新错题记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrongBook(wrongBook: WrongBook)

    /**
     * 批量插入错题记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrongBooks(wrongBooks: List<WrongBook>)

    /**
     * 更新错题记录
     */
    @Update
    suspend fun updateWrongBook(wrongBook: WrongBook)

    /**
     * 删除错题记录
     */
    @Delete
    suspend fun deleteWrongBook(wrongBook: WrongBook)

    /**
     * 根据卦象ID删除错题记录
     */
    @Query("DELETE FROM wrong_book WHERE hexagramId = :hexagramId")
    suspend fun deleteWrongBookByHexagramId(hexagramId: Int)

    /**
     * 清空所有错题记录
     */
    @Query("DELETE FROM wrong_book")
    suspend fun deleteAllWrongBooks()

    /**
     * 增加错题错误次数
     */
    @Query("UPDATE wrong_book SET wrongCount = wrongCount + 1, lastWrongTimestamp = :timestamp WHERE hexagramId = :hexagramId")
    suspend fun incrementWrongCount(hexagramId: Int, timestamp: Long)

    /**
     * 更新复习时间戳
     */
    @Query("UPDATE wrong_book SET lastReviewTimestamp = :timestamp WHERE hexagramId = :hexagramId")
    suspend fun updateReviewTimestamp(hexagramId: Int, timestamp: Long)

    /**
     * 获取错题统计信息
     */
    @Query("SELECT COUNT(*) as total, AVG(wrongCount) as avgWrongCount, MAX(wrongCount) as maxWrongCount FROM wrong_book")
    suspend fun getWrongBookStatistics(): WrongBookStatistics
}

/**
 * 错题本统计信息数据类
 */
data class WrongBookStatistics(
    val total: Int,
    val avgWrongCount: Double,
    val maxWrongCount: Int
)
