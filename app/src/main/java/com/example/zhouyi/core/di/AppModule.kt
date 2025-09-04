package com.example.zhouyi.core.di

import android.content.Context
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.repository.*
import com.example.zhouyi.data.preferences.AppPreferences

/**
 * 应用依赖注入模块
 */
object AppModule {
    
    /**
     * 获取数据库实例
     */
    fun provideDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    /**
     * 获取卦象仓库
     */
    fun provideHexagramRepository(context: Context): HexagramRepository {
        return HexagramRepository(context)
    }
    
    /**
     * 获取答题记录仓库
     */
    fun provideAttemptRepository(context: Context): AttemptRepository {
        return AttemptRepository(context)
    }
    
    /**
     * 获取错题本仓库
     */
    fun provideWrongBookRepository(context: Context): WrongBookRepository {
        return WrongBookRepository(context)
    }
    
    /**
     * 获取SRS仓库
     */
    fun provideSrsRepository(context: Context): SrsRepository {
        return SrsRepository(context)
    }
    
    /**
     * 获取打卡仓库
     */
    fun provideCheckInRepository(database: AppDatabase): CheckInRepository {
        return CheckInRepository(database.checkInDao())
    }
    
    /**
     * 获取应用偏好设置
     */
    fun provideAppPreferences(context: Context): AppPreferences {
        return AppPreferences(context)
    }
}
