package com.example.zhouyi.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 错题本数据模型
 * 对应式样书中的 wrong_book 表
 */
@Entity(
    tableName = "wrong_book",
    foreignKeys = [
        ForeignKey(
            entity = Hexagram::class,
            parentColumns = ["id"],
            childColumns = ["hexagramId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WrongBook(
    @PrimaryKey
    val hexagramId: Int, // 关联的卦象ID
    val wrongCount: Int = 1, // 错误次数
    val lastWrongTimestamp: Long, // 最后一次答错的时间戳
    val firstWrongTimestamp: Long = lastWrongTimestamp, // 首次答错的时间戳
    val lastReviewTimestamp: Long = 0L // 最后一次复习的时间戳
) {
    /**
     * 获取错误频率（错误次数/时间）
     */
    fun getWrongFrequency(): Float {
        val daysSinceFirst = (System.currentTimeMillis() - firstWrongTimestamp) / (1000 * 60 * 60 * 24)
        return if (daysSinceFirst > 0) wrongCount.toFloat() / daysSinceFirst else wrongCount.toFloat()
    }

    /**
     * 获取距离上次答错的天数
     */
    fun getDaysSinceLastWrong(): Int {
        return ((System.currentTimeMillis() - lastWrongTimestamp) / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * 获取距离上次复习的天数
     */
    fun getDaysSinceLastReview(): Int {
        return if (lastReviewTimestamp > 0) {
            ((System.currentTimeMillis() - lastReviewTimestamp) / (1000 * 60 * 60 * 24)).toInt()
        } else {
            -1 // 从未复习过
        }
    }
}
