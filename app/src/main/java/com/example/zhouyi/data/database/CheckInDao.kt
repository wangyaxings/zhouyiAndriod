package com.example.zhouyi.data.database

import androidx.room.*
import com.example.zhouyi.data.model.CheckInRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 打卡记录数据库访问接口
 */
@Dao
interface CheckInDao {
    
    /**
     * 插入或更新打卡记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCheckIn(checkIn: CheckInRecord)
    
    /**
     * 获取指定日期的打卡记录
     */
    @Query("SELECT * FROM check_in_records WHERE date = :date")
    suspend fun getCheckInByDate(date: LocalDate): CheckInRecord?
    
    /**
     * 获取指定月份的打卡记录
     */
    @Query("SELECT * FROM check_in_records WHERE strftime('%Y-%m', date) = strftime('%Y-%m', :yearMonth)")
    fun getCheckInsByMonth(yearMonth: String): Flow<List<CheckInRecord>>
    
    /**
     * 获取所有打卡记录
     */
    @Query("SELECT * FROM check_in_records ORDER BY date DESC")
    fun getAllCheckIns(): Flow<List<CheckInRecord>>
    
    /**
     * 获取有效打卡记录（答题数量>=20）
     */
    @Query("SELECT * FROM check_in_records WHERE questionCount >= 20 ORDER BY date DESC")
    fun getValidCheckIns(): Flow<List<CheckInRecord>>
    
    /**
     * 获取总打卡天数
     */
    @Query("SELECT COUNT(*) FROM check_in_records WHERE questionCount >= 20")
    suspend fun getTotalCheckInDays(): Int
    
    /**
     * 获取总答题数量
     */
    @Query("SELECT SUM(questionCount) FROM check_in_records")
    suspend fun getTotalQuestions(): Int
    
    /**
     * 获取最后打卡日期
     */
    @Query("SELECT date FROM check_in_records WHERE questionCount >= 20 ORDER BY date DESC LIMIT 1")
    suspend fun getLastCheckInDate(): LocalDate?
    
    /**
     * 删除指定日期的打卡记录
     */
    @Query("DELETE FROM check_in_records WHERE date = :date")
    suspend fun deleteCheckInByDate(date: LocalDate)
    
    /**
     * 清空所有打卡记录
     */
    @Query("DELETE FROM check_in_records")
    suspend fun clearAllCheckIns()
}
