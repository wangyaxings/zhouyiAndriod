package com.example.zhouyi.data.repository

import android.content.Context
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.database.SrsStateDao
import com.example.zhouyi.data.model.SrsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * 间隔复习数据仓库
 */
class SrsRepository(context: Context) {

    private val srsStateDao: SrsStateDao = AppDatabase.getDatabase(context).srsStateDao()

    /**
     * 获取所有SRS状态
     */
    fun getAllSrsStates(): Flow<List<SrsState>> {
        return srsStateDao.getAllSrsStates()
    }

    /**
     * 根据卦象ID获取SRS状态
     */
    suspend fun getSrsStateByHexagram(hexagramId: Int): SrsState? {
        return withContext(Dispatchers.IO) {
            srsStateDao.getSrsStateByHexagram(hexagramId)
        }
    }

    /**
     * 根据盒子编号获取SRS状态
     */
    suspend fun getSrsStatesByBucket(bucket: Int): List<SrsState> {
        return withContext(Dispatchers.IO) {
            srsStateDao.getSrsStatesByBucket(bucket)
        }
    }

    /**
     * 获取需要复习的SRS状态
     */
    suspend fun getDueSrsStates(timestamp: Long): List<SrsState> {
        return withContext(Dispatchers.IO) {
            srsStateDao.getDueSrsStates(timestamp)
        }
    }

    /**
     * 根据盒子编号获取SRS状态数量
     */
    suspend fun getSrsStateCountByBucket(bucket: Int): Int {
        return withContext(Dispatchers.IO) {
            srsStateDao.getSrsStateCountByBucket(bucket)
        }
    }

    /**
     * 插入SRS状态
     */
    suspend fun insertSrsState(srsState: SrsState) {
        withContext(Dispatchers.IO) {
            srsStateDao.insertSrsState(srsState)
        }
    }

    /**
     * 更新SRS状态
     */
    suspend fun updateSrsState(srsState: SrsState) {
        withContext(Dispatchers.IO) {
            srsStateDao.updateSrsState(srsState)
        }
    }

    /**
     * 删除SRS状态
     */
    suspend fun deleteSrsState(srsState: SrsState) {
        withContext(Dispatchers.IO) {
            srsStateDao.deleteSrsState(srsState)
        }
    }

    /**
     * 根据卦象ID删除SRS状态
     */
    suspend fun deleteSrsStateByHexagram(hexagramId: Int) {
        withContext(Dispatchers.IO) {
            srsStateDao.deleteSrsStateByHexagram(hexagramId)
        }
    }

    /**
     * 处理答题结果，更新SRS状态
     */
    suspend fun processAnswer(hexagramId: Int, isCorrect: Boolean) {
        withContext(Dispatchers.IO) {
            val currentState = srsStateDao.getSrsStateByHexagram(hexagramId)
            val newState = if (isCorrect) {
                // 答对：进入下一个盒子
                val currentBucket = currentState?.bucket ?: 2
                val newBucket = minOf(currentBucket + 1, 5)
                val interval = SrsState.getIntervalForBucket(newBucket)
                val dueTimestamp = System.currentTimeMillis() + interval

                SrsState(
                    hexagramId = hexagramId,
                    bucket = newBucket,
                    dueTimestamp = dueTimestamp
                )
            } else {
                // 答错：回到盒子1
                SrsState(
                    hexagramId = hexagramId,
                    bucket = 1,
                    dueTimestamp = System.currentTimeMillis()
                )
            }

            srsStateDao.insertSrsState(newState)
        }
    }

    /**
     * 获取需要复习的卦象ID列表
     */
    suspend fun getDueHexagramIds(): List<Int> {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            srsStateDao.getDueSrsStates(now).map { it.hexagramId }
        }
    }

    /**
     * 获取SRS统计信息
     */
    suspend fun getSrsStatistics(): Map<Int, Int> {
        return withContext(Dispatchers.IO) {
            val bucketCounts = mutableMapOf<Int, Int>()
            for (bucket in 1..5) {
                bucketCounts[bucket] = srsStateDao.getSrsStateCountByBucket(bucket)
            }
            bucketCounts
        }
    }
}
