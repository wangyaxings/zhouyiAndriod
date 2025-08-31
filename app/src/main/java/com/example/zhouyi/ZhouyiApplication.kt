package com.example.zhouyi

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.preferences.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 应用类
 * 负责初始化数据库、偏好设置等全局组件
 */
class ZhouyiApplication : Application() {

    companion object {
        private lateinit var instance: ZhouyiApplication

        fun getInstance(): ZhouyiApplication = instance
    }

    // 应用级协程作用域
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // 数据库实例
    lateinit var database: AppDatabase
        private set

    // 偏好设置实例
    lateinit var preferences: AppPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 初始化数据库
        database = AppDatabase.getDatabase(this)

        // 初始化偏好设置
        preferences = AppPreferences(this)

        // 初始化数据（如果数据库为空）
        initializeDataIfNeeded()
    }

    /**
     * 如果数据库为空，初始化基础数据
     */
    private fun initializeDataIfNeeded() {
        applicationScope.launch {
            val hexagramCount = database.hexagramDao().getHexagramCount()
            if (hexagramCount == 0) {
                // 数据库为空，需要初始化数据
                initializeHexagramData()
            }
        }
    }

    /**
     * 初始化六十四卦数据
     */
    private suspend fun initializeHexagramData() {
        try {
            // 从assets读取JSON数据
            val jsonString = assets.open("hexagrams.json").bufferedReader().use { it.readText() }

            // 解析JSON数据（这里需要实现JSON解析逻辑）
            // 暂时跳过，在Repository层实现

        } catch (e: Exception) {
            // 初始化失败，记录错误
            e.printStackTrace()
        }
    }

    /**
     * 获取应用上下文
     */
    fun getAppContext(): Context = applicationContext
}
