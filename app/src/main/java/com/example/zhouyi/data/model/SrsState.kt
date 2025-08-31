package com.example.zhouyi.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 间隔复习状态数据模型
 * 对应式样书中的 srs_state 表，实现Leitner系统
 */
@Entity(
    tableName = "srs_states",
    foreignKeys = [
        ForeignKey(
            entity = Hexagram::class,
            parentColumns = ["id"],
            childColumns = ["hexagramId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SrsState(
    @PrimaryKey
    val hexagramId: Int, // 关联的卦象ID
    val bucket: Int = 2, // Leitner盒子编号 (1-5)，新题默认放入盒2
    val dueTimestamp: Long, // 下次复习时间戳
    val lastReviewTimestamp: Long = 0L, // 上次复习时间戳
    val consecutiveCorrect: Int = 0, // 连续答对次数
    val totalReviews: Int = 0 // 总复习次数
) {
    companion object {
        // Leitner系统的复习间隔（毫秒）
        val BUCKET_INTERVALS = mapOf(
            1 to 0L, // 盒1：立即复习
            2 to 24 * 60 * 60 * 1000L, // 盒2：1天后
            3 to 3 * 24 * 60 * 60 * 1000L, // 盒3：3天后
            4 to 7 * 24 * 60 * 60 * 1000L, // 盒4：7天后
            5 to 14 * 24 * 60 * 60 * 1000L // 盒5：14天后
        )
    }

    /**
     * 检查是否到了复习时间
     */
    fun isDue(): Boolean {
        return System.currentTimeMillis() >= dueTimestamp
    }

    /**
     * 获取距离下次复习的天数
     */
    fun getDaysUntilDue(): Int {
        val remaining = dueTimestamp - System.currentTimeMillis()
        return if (remaining > 0) {
            (remaining / (1000 * 60 * 60 * 24)).toInt()
        } else {
            0
        }
    }

    /**
     * 获取复习间隔描述
     */
    fun getIntervalDescription(): String {
        return when (bucket) {
            1 -> "立即复习"
            2 -> "1天后"
            3 -> "3天后"
            4 -> "7天后"
            5 -> "14天后"
            else -> "未知"
        }
    }

    /**
     * 计算下次复习时间戳
     */
    fun calculateNextDueTimestamp(): Long {
        val interval = BUCKET_INTERVALS[bucket] ?: 0L
        return System.currentTimeMillis() + interval
    }
}
