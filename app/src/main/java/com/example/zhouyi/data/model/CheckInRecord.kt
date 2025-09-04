package com.example.zhouyi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * 打卡记录数据模型
 */
@Entity(tableName = "check_in_records")
data class CheckInRecord(
    @PrimaryKey
    val date: LocalDate, // 打卡日期
    val questionCount: Int, // 当日答题数量
    val correctCount: Int, // 当日正确答题数量
    val checkInTime: Long = System.currentTimeMillis() // 打卡时间戳
) {
    /**
     * 判断是否为有效打卡（答题数量达到20题）
     */
    fun isValidCheckIn(): Boolean {
        return questionCount >= 20
    }
    
    /**
     * 获取打卡状态描述
     */
    fun getStatusText(): String {
        return if (isValidCheckIn()) "已打卡" else "未完成"
    }
}
