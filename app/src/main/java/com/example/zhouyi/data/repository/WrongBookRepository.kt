package com.example.zhouyi.data.repository

import android.content.Context
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.database.WrongBookDao
import com.example.zhouyi.data.model.WrongBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * 错题本数据仓库
 */
class WrongBookRepository(context: Context) {

    private val wrongBookDao: WrongBookDao = AppDatabase.getDatabase(context).wrongBookDao()

    /**
     * 获取所有错题
     */
    fun getAllWrongBook(): Flow<List<WrongBook>> {
        return wrongBookDao.getAllWrongBook()
    }

    /**
     * 根据卦象ID获取错题记录
     */
    suspend fun getWrongBookByHexagram(hexagramId: Int): WrongBook? {
        return withContext(Dispatchers.IO) {
            wrongBookDao.getWrongBookByHexagram(hexagramId)
        }
    }

    /**
     * 获取错题总数
     */
    suspend fun getWrongBookCount(): Int {
        return withContext(Dispatchers.IO) {
            wrongBookDao.getWrongBookCount()
        }
    }

    /**
     * 获取最近的错题
     */
    suspend fun getRecentWrongBook(timestamp: Long): List<WrongBook> {
        return withContext(Dispatchers.IO) {
            wrongBookDao.getRecentWrongBook(timestamp)
        }
    }

    /**
     * 添加错题
     */
    suspend fun addWrongBook(wrongBook: WrongBook) {
        withContext(Dispatchers.IO) {
            wrongBookDao.insertWrongBook(wrongBook)
        }
    }

    /**
     * 更新错题
     */
    suspend fun updateWrongBook(wrongBook: WrongBook) {
        withContext(Dispatchers.IO) {
            wrongBookDao.updateWrongBook(wrongBook)
        }
    }

    /**
     * 删除错题
     */
    suspend fun deleteWrongBook(wrongBook: WrongBook) {
        withContext(Dispatchers.IO) {
            wrongBookDao.deleteWrongBook(wrongBook)
        }
    }

    /**
     * 根据卦象ID删除错题
     */
    suspend fun deleteWrongBookByHexagram(hexagramId: Int) {
        withContext(Dispatchers.IO) {
            wrongBookDao.deleteWrongBookByHexagram(hexagramId)
        }
    }

    /**
     * 处理答题错误
     * 如果已存在则更新错误次数，否则创建新记录
     */
    suspend fun handleWrongAnswer(hexagramId: Int) {
        withContext(Dispatchers.IO) {
            val existing = wrongBookDao.getWrongBookByHexagram(hexagramId)
            val now = System.currentTimeMillis()

            if (existing != null) {
                // 更新错误次数和时间戳
                val updated = existing.copy(
                    wrongCount = existing.wrongCount + 1,
                    lastWrongTimestamp = now
                )
                wrongBookDao.updateWrongBook(updated)
            } else {
                // 创建新的错题记录
                val newWrongBook = WrongBook(
                    hexagramId = hexagramId,
                    wrongCount = 1,
                    lastWrongTimestamp = now
                )
                wrongBookDao.insertWrongBook(newWrongBook)
            }
        }
    }

    /**
     * 获取错题ID列表
     */
    suspend fun getWrongHexagramIds(): List<Int> {
        return withContext(Dispatchers.IO) {
            wrongBookDao.getAllWrongBook().collect { wrongBooks ->
                return@collect wrongBooks.map { it.hexagramId }
            }
            emptyList()
        }
    }
}
