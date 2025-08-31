package com.example.zhouyi.data.repository

import android.content.Context
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.database.WrongBookDao
import com.example.zhouyi.data.model.WrongBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * 错题本数据仓库
 */
class WrongBookRepository(context: Context) {

    private val wrongBookDao: WrongBookDao = AppDatabase.getDatabase(context).wrongBookDao()

    // Flow: 所有错题（用于统计、监听变化）
    fun getAllWrongBook(): Flow<List<WrongBook>> = wrongBookDao.getAllWrongBooks()

    // 一次性获取错题列表（用于页面加载）
    suspend fun getAllWrongBooks(): List<WrongBook> = wrongBookDao.getAllWrongBooks().first()

    // 错题总数（一次性）
    suspend fun getWrongBookCount(): Int = withContext(Dispatchers.IO) {
        wrongBookDao.getWrongBookCount()
    }

    // 观察错题总数（Flow）
    fun observeWrongBookCount(): Flow<Int> = wrongBookDao.getAllWrongBooks().map { it.size }

    // 根据卦象ID获取错题
    suspend fun getWrongBookByHexagram(hexagramId: Int): WrongBook? = withContext(Dispatchers.IO) {
        wrongBookDao.getWrongBookByHexagramId(hexagramId)
    }

    // 最近错题
    suspend fun getRecentWrongBooks(sevenDaysAgo: Long): List<WrongBook> = withContext(Dispatchers.IO) {
        wrongBookDao.getRecentWrongBooks(sevenDaysAgo)
    }

    // 添加或更新错题
    suspend fun handleWrongAnswer(hexagramId: Int) {
        withContext(Dispatchers.IO) {
            val existing = wrongBookDao.getWrongBookByHexagramId(hexagramId)
            val now = System.currentTimeMillis()
            if (existing != null) {
                wrongBookDao.incrementWrongCount(hexagramId, now)
            } else {
                val newWrong = WrongBook(
                    hexagramId = hexagramId,
                    wrongCount = 1,
                    lastWrongTimestamp = now
                )
                wrongBookDao.insertWrongBook(newWrong)
            }
        }
    }

    // 直接插入（用于测试/导入）
    suspend fun insertWrongBook(wrongBook: WrongBook) = withContext(Dispatchers.IO) {
        wrongBookDao.insertWrongBook(wrongBook)
    }

    // 删除指定错题
    suspend fun deleteWrongBook(hexagramId: Int) = withContext(Dispatchers.IO) {
        wrongBookDao.deleteWrongBookByHexagramId(hexagramId)
    }

    // 清空错题本
    suspend fun clearAllWrongBooks() = withContext(Dispatchers.IO) {
        wrongBookDao.deleteAllWrongBooks()
    }

    // 获取错题ID列表
    suspend fun getWrongHexagramIds(): List<Int> = withContext(Dispatchers.IO) {
        wrongBookDao.getWrongHexagramIds()
    }
}

