package com.example.zhouyi.data.repository

import android.content.Context
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.database.AttemptDao
import com.example.zhouyi.data.database.AttemptStatistics
import com.example.zhouyi.data.model.Attempt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * 答题记录数据仓库
 */
class AttemptRepository(context: Context) {

    private val attemptDao: AttemptDao = AppDatabase.getDatabase(context).attemptDao()

    // 获取所有答题记录（Flow）
    fun getAllAttempts(): Flow<List<Attempt>> = attemptDao.getAllAttempts()

    // 今日统计（Flow）
    fun getTodayStatistics(): Flow<AttemptStatistics> {
        return getAllAttempts().map { attempts ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis
            val endOfDay = startOfDay + 24L * 60 * 60 * 1000

            val today = attempts.filter { it.timestamp in startOfDay until endOfDay }
            val total = today.size
            val correct = today.count { it.isCorrect }
            AttemptStatistics(total = total, correct = correct)
        }
    }

    // 总体统计（Flow）
    fun getOverallStatistics(): Flow<AttemptStatistics> = getAllAttempts().map { attempts ->
        val total = attempts.size
        val correct = attempts.count { it.isCorrect }
        AttemptStatistics(total = total, correct = correct)
    }

    // 按卦象ID获取（一次性）
    suspend fun getAttemptsByHexagramId(hexagramId: Int): List<Attempt> =
        withContext(Dispatchers.IO) { attemptDao.getAttemptsByHexagramId(hexagramId) }

    // 按时间范围获取（一次性）
    suspend fun getAttemptsByTimeRange(startTime: Long, endTime: Long): List<Attempt> =
        withContext(Dispatchers.IO) { attemptDao.getAttemptsByTimeRange(startTime, endTime) }

    // 插入一条答题记录
    suspend fun insertAttempt(attempt: Attempt) {
        withContext(Dispatchers.IO) { attemptDao.insertAttempt(attempt) }
    }
}

