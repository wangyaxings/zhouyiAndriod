package com.example.zhouyi.data.repository

import com.example.zhouyi.data.database.CheckInDao
import com.example.zhouyi.data.model.CheckInRecord
import com.example.zhouyi.data.model.CheckInStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * 打卡记录仓库
 * 提供打卡相关的业务逻辑
 */
class CheckInRepository(
    private val checkInDao: CheckInDao
) {

    /**
     * 获取指定月份的打卡记录
     */
    fun getCheckInsByMonth(year: Int, month: Int): Flow<List<CheckInRecord>> {
        val yearMonth = String.format("%04d-%02d", year, month)
        return checkInDao.getCheckInsByMonth(yearMonth)
    }

    /**
     * 获取所有打卡记录
     */
    fun getAllCheckIns(): Flow<List<CheckInRecord>> {
        return checkInDao.getAllCheckIns()
    }

    /**
     * 获取有效打卡记录
     */
    fun getValidCheckIns(): Flow<List<CheckInRecord>> {
        return checkInDao.getValidCheckIns()
    }

    /**
     * 获取指定日期的打卡记录
     */
    suspend fun getCheckInByDate(date: LocalDate): CheckInRecord? {
        return checkInDao.getCheckInByDate(date)
    }

    /**
     * 打卡（答题完成后调用）
     */
    suspend fun checkIn(questionCount: Int, correctCount: Int) {
        val today = LocalDate.now()
        val existingRecord = checkInDao.getCheckInByDate(today)

        if (existingRecord != null) {
            // 更新现有记录
            val updatedRecord = existingRecord.copy(
                questionCount = existingRecord.questionCount + questionCount,
                correctCount = existingRecord.correctCount + correctCount,
                checkInTime = System.currentTimeMillis()
            )
            checkInDao.insertOrUpdateCheckIn(updatedRecord)
        } else {
            // 创建新记录
            val newRecord = CheckInRecord(
                date = today,
                questionCount = questionCount,
                correctCount = correctCount
            )
            checkInDao.insertOrUpdateCheckIn(newRecord)
        }
    }

    /**
     * 获取打卡统计信息
     */
    suspend fun getCheckInStatistics(): CheckInStatistics {
        val totalCheckInDays = checkInDao.getTotalCheckInDays()
        val totalQuestions = checkInDao.getTotalQuestions() ?: 0
        val lastCheckInDate = checkInDao.getLastCheckInDate()

        // 计算连续打卡天数
        val currentStreak = calculateCurrentStreak()
        val longestStreak = calculateLongestStreak()

        // 计算本月和本年打卡次数
        val now = LocalDate.now()
        val thisMonthCheckIns = getCheckInsCountByPeriod(now.year, now.monthValue)
        val thisYearCheckIns = getCheckInsCountByPeriod(now.year, null)

        return CheckInStatistics(
            totalCheckInDays = totalCheckInDays,
            totalQuestions = totalQuestions,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastCheckInDate = lastCheckInDate,
            thisMonthCheckIns = thisMonthCheckIns,
            thisYearCheckIns = thisYearCheckIns
        )
    }

    /**
     * 计算当前连续打卡天数
     */
    private suspend fun calculateCurrentStreak(): Int {
        val validCheckIns = checkInDao.getValidCheckIns().first()
        if (validCheckIns.isEmpty()) return 0

        val sortedCheckIns = validCheckIns.sortedByDescending { it.date }
        var streak = 0
        var currentDate = LocalDate.now()

        for (checkIn in sortedCheckIns) {
            if (checkIn.date == currentDate || checkIn.date == currentDate.minusDays(1)) {
                streak++
                currentDate = checkIn.date
            } else {
                break
            }
        }

        return streak
    }

    /**
     * 计算最长连续打卡天数
     */
    private suspend fun calculateLongestStreak(): Int {
        val validCheckIns = checkInDao.getValidCheckIns().first()
        if (validCheckIns.isEmpty()) return 0

        val sortedCheckIns = validCheckIns.sortedBy { it.date }
        var maxStreak = 0
        var currentStreak = 1

        for (i in 1 until sortedCheckIns.size) {
            val prevDate = sortedCheckIns[i - 1].date
            val currentDate = sortedCheckIns[i].date

            if (currentDate.minusDays(1) == prevDate) {
                currentStreak++
            } else {
                maxStreak = maxOf(maxStreak, currentStreak)
                currentStreak = 1
            }
        }

        maxStreak = maxOf(maxStreak, currentStreak)
        return maxStreak
    }

    /**
     * 获取指定时期的打卡次数
     */
    private suspend fun getCheckInsCountByPeriod(year: Int, month: Int?): Int {
        val validCheckIns = checkInDao.getValidCheckIns().first()
        return validCheckIns.count { checkIn ->
            if (month != null) {
                checkIn.date.year == year && checkIn.date.monthValue == month
            } else {
                checkIn.date.year == year
            }
        }
    }

    /**
     * 删除指定日期的打卡记录
     */
    suspend fun deleteCheckInByDate(date: LocalDate) {
        checkInDao.deleteCheckInByDate(date)
    }

    /**
     * 清空所有打卡记录
     */
    suspend fun clearAllCheckIns() {
        checkInDao.clearAllCheckIns()
    }
}
