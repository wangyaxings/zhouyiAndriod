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

/**
 * 应用程序入口：初始化数据库、偏好设置与基础数据
 */
class ZhouyiApplication : Application() {

    companion object {
        private lateinit var instance: ZhouyiApplication
        fun getInstance(): ZhouyiApplication = instance
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var database: AppDatabase
        private set

    lateinit var preferences: AppPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getDatabase(this)
        preferences = AppPreferences(this)

        initializeDataIfNeeded()
    }

    private fun initializeDataIfNeeded() {
        applicationScope.launch {
            val hexagramCount = database.hexagramDao().getHexagramCount()
            if (hexagramCount == 0) {
                initializeHexagramData()
            }
        }
    }

    private suspend fun initializeHexagramData() {
        try {
            val repo = HexagramRepository(this)
            repo.initializeData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAppContext(): Context = applicationContext
}

