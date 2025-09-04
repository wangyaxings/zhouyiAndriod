package com.example.zhouyi.data.model

import java.time.LocalDate

/**
 * 打卡统计数据模型
 */
data class CheckInStatistics(
    val totalCheckInDays: Int, // 总打卡天数
    val totalQuestions: Int, // 总答题数量
    val currentStreak: Int, // 当前连续打卡天数
    val longestStreak: Int, // 最长连续打卡天数
    val lastCheckInDate: LocalDate?, // 最后打卡日期
    val thisMonthCheckIns: Int, // 本月打卡次数
    val thisYearCheckIns: Int // 本年打卡次数
) {
    companion object {
        fun empty(): CheckInStatistics {
            return CheckInStatistics(
                totalCheckInDays = 0,
                totalQuestions = 0,
                currentStreak = 0,
                longestStreak = 0,
                lastCheckInDate = null,
                thisMonthCheckIns = 0,
                thisYearCheckIns = 0
            )
        }
    }
}
