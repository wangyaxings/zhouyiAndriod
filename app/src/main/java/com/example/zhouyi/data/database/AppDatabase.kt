package com.example.zhouyi.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.zhouyi.data.model.*

/**
 * 主数据库类
 * 整合所有实体和DAO
 */
@Database(
    entities = [
        Hexagram::class,
        Attempt::class,
        WrongBook::class,
        SrsState::class,
        CheckInRecord::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // DAO接口
    abstract fun hexagramDao(): HexagramDao
    abstract fun attemptDao(): AttemptDao
    abstract fun wrongBookDao(): WrongBookDao
    abstract fun srsStateDao(): SrsStateDao
    abstract fun checkInDao(): CheckInDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 获取数据库实例（单例模式）
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zhouyi_database"
                )
                .fallbackToDestructiveMigration() // 开发阶段允许破坏性迁移
                .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * 清空数据库实例（用于测试）
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
