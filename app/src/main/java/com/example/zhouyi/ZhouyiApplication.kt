package com.example.zhouyi

import android.app.Application
import android.content.Context
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.preferences.AppPreferences
import com.example.zhouyi.data.repository.HexagramRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 应用程序入口：初始化数据库、偏好设置与基础数据
 */
class ZhouyiApplication : Application() {

    companion object {
        private lateinit var instance: ZhouyiApplication
        fun getInstance(): ZhouyiApplication = instance
    }

    // Use Dispatchers.IO for background tasks, especially database operations
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var database: AppDatabase
        private set

    lateinit var preferences: AppPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getDatabase(applicationContext) // Use applicationContext
        preferences = AppPreferences(applicationContext) // Use applicationContext

        initializeDataIfNeeded()
    }

    private fun initializeDataIfNeeded() {
        applicationScope.launch { // This will now run on Dispatchers.Default
            val hexagramCount = database.hexagramDao().getHexagramCount() // This is a suspend function, runs on IO dispatcher via Room
            if (hexagramCount == 0) {
                // Ensure HexagramRepository also uses applicationContext if it stores the context
                initializeHexagramData()
            }
        }
    }

    private suspend fun initializeHexagramData() {
        try {
            // It's better if HexagramRepository takes applicationContext directly
            // or is constructed in a way that doesn't require context here if it's already available globally
            val repo = HexagramRepository(applicationContext) // Pass applicationContext
            withContext(Dispatchers.IO) { // Ensure data initialization is on a background thread
                repo.initializeData()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Consider logging this error to a crash reporting tool or Android's Logcat for better visibility
        }
    }

    fun getAppContext(): Context = applicationContext
}
