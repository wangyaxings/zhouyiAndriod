package com.example.zhouyi.data.repository

import android.content.Context
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.database.AttemptDao
import com.example.zhouyi.data.model.Attempt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * 答题记录数据仓库
 */
class AttemptRepository(context: Context) {

    private val attemptDao: AttemptDao = AppDatabase.getDatabase(context).attemptDao()

    /**
     * 获取所有答题记录
     */
    fun getAllAttempts(): Flow<List<Attempt>> {
        return attemptDao.getAllAttempts()
    }

    /**
     * 根据卦象ID获取答题记录
     */
    fun getAttemptsByHexagram(hexagramId: Int): Flow<List<Attempt>> {
        return attemptDao.getAttemptsByHexagram(hexagramId)
    }

    /**
     * 获取指定时间之后的答题记录
     */
    fun getAttemptsSince(startTimestamp: Long): Flow<List<Attempt>> {
        return attemptDao.getAttemptsSince(startTimestamp)
    }

    /**
     * 获取指定日期的答题记录
     */
    fun getAttemptsByDate(date: Long): Flow<List<Attempt>> {
        return attemptDao.getAttemptsByDate(date)
    }

    /**
     * 获取指定时间之后的正确答题数
     */
    suspend fun getCorrectCountSince(startTimestamp: Long): Int {
        return withContext(Dispatchers.IO) {
            attemptDao.getCorrectCountSince(startTimestamp)
        }
    }

    /**
     * 获取指定时间之后的总答题数
     */
    suspend fun getTotalCountSince(startTimestamp: Long): Int {
        return withContext(Dispatchers.IO) {
            attemptDao.getTotalCountSince(startTimestamp)
        }
    }

    /**
     * 获取指定时间之后学习过的卦象ID列表
     */
    suspend fun getStudiedHexagramsSince(startTimestamp: Long): List<Int> {
        return withContext(Dispatchers.IO) {
            attemptDao.getStudiedHexagramsSince(startTimestamp)
        }
    }

    /**
     * 插入答题记录
     */
    suspend fun insertAttempt(attempt: Attempt) {
        withContext(Dispatchers.IO) {
            attemptDao.insertAttempt(attempt)
        }
    }

    /**
     * 删除指定时间之前的答题记录
     */
    suspend fun deleteAttemptsBefore(timestamp: Long) {
        withContext(Dispatchers.IO) {
            attemptDao.deleteAttemptsBefore(timestamp)
        }
    }

    /**
     * 计算正确率
     */
    suspend fun getAccuracySince(startTimestamp: Long): Float {
        return withContext(Dispatchers.IO) {
            val total = attemptDao.getTotalCountSince(startTimestamp)
            if (total == 0) 0f else {
                val correct = attemptDao.getCorrectCountSince(startTimestamp)
                correct.toFloat() / total
            }
        }
    }
}
