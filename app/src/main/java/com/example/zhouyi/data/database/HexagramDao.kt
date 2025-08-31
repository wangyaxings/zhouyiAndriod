package com.example.zhouyi.data.database

import androidx.room.*
import com.example.zhouyi.data.model.Hexagram
import kotlinx.coroutines.flow.Flow

/**
 * 六十四卦数据访问接口
 */
@Dao
interface HexagramDao {

    /**
     * 获取所有卦象
     */
    @Query("SELECT * FROM hexagrams ORDER BY id")
    fun getAllHexagrams(): Flow<List<Hexagram>>

    /**
     * 根据ID获取卦象
     */
    @Query("SELECT * FROM hexagrams WHERE id = :hexagramId")
    suspend fun getHexagramById(hexagramId: Int): Hexagram?

    /**
     * 获取指定ID列表的卦象
     */
    @Query("SELECT * FROM hexagrams WHERE id IN (:hexagramIds)")
    suspend fun getHexagramsByIds(hexagramIds: List<Int>): List<Hexagram>

    /**
     * 根据上卦获取卦象
     */
    @Query("SELECT * FROM hexagrams WHERE upperTrigram = :upperTrigram")
    suspend fun getHexagramsByUpperTrigram(upperTrigram: String): List<Hexagram>

    /**
     * 根据下卦获取卦象
     */
    @Query("SELECT * FROM hexagrams WHERE lowerTrigram = :lowerTrigram")
    suspend fun getHexagramsByLowerTrigram(lowerTrigram: String): List<Hexagram>

    /**
     * 获取所有上卦列表
     */
    @Query("SELECT DISTINCT upperTrigram FROM hexagrams ORDER BY upperTrigram")
    suspend fun getAllUpperTrigrams(): List<String>

    /**
     * 获取所有下卦列表
     */
    @Query("SELECT DISTINCT lowerTrigram FROM hexagrams ORDER BY lowerTrigram")
    suspend fun getAllLowerTrigrams(): List<String>

    /**
     * 插入单个卦象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHexagram(hexagram: Hexagram)

    /**
     * 批量插入卦象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHexagrams(hexagrams: List<Hexagram>)

    /**
     * 更新卦象
     */
    @Update
    suspend fun updateHexagram(hexagram: Hexagram)

    /**
     * 删除卦象
     */
    @Delete
    suspend fun deleteHexagram(hexagram: Hexagram)

    /**
     * 清空所有卦象数据
     */
    @Query("DELETE FROM hexagrams")
    suspend fun deleteAllHexagrams()

    /**
     * 获取卦象总数
     */
    @Query("SELECT COUNT(*) FROM hexagrams")
    suspend fun getHexagramCount(): Int

    /**
     * 搜索卦象（按卦名模糊搜索）
     */
    @Query("SELECT * FROM hexagrams WHERE nameZh LIKE '%' || :query || '%' ORDER BY id")
    suspend fun searchHexagrams(query: String): List<Hexagram>
}
