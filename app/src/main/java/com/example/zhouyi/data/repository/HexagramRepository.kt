package com.example.zhouyi.data.repository

import android.content.Context
import com.example.zhouyi.data.database.AppDatabase
import com.example.zhouyi.data.database.HexagramDao
import com.example.zhouyi.data.model.Hexagram
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * 六十四卦数据仓库
 * 负责数据的初始化和业务逻辑
 */
class HexagramRepository(private val context: Context) {

    private val hexagramDao: HexagramDao = AppDatabase.getDatabase(context).hexagramDao()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val hexagramListType = Types.newParameterizedType(List::class.java, Hexagram::class.java)
    private val hexagramListAdapter = moshi.adapter<List<Hexagram>>(hexagramListType)

    /**
     * 获取所有卦象
     */
    fun getAllHexagrams(): Flow<List<Hexagram>> {
        return hexagramDao.getAllHexagrams()
    }

    /**
     * 根据ID获取卦象
     */
    suspend fun getHexagramById(id: Int): Hexagram? {
        return withContext(Dispatchers.IO) {
            hexagramDao.getHexagramById(id)
        }
    }

    /**
     * 根据上卦获取卦象
     */
    suspend fun getHexagramsByUpperTrigram(upperTrigram: String): List<Hexagram> {
        return withContext(Dispatchers.IO) {
            hexagramDao.getHexagramsByUpperTrigram(upperTrigram)
        }
    }

    /**
     * 根据下卦获取卦象
     */
    suspend fun getHexagramsByLowerTrigram(lowerTrigram: String): List<Hexagram> {
        return withContext(Dispatchers.IO) {
            hexagramDao.getHexagramsByLowerTrigram(lowerTrigram)
        }
    }

    /**
     * 初始化数据
     * 从assets中的JSON文件加载六十四卦数据
     */
    suspend fun initializeData() {
        withContext(Dispatchers.IO) {
            val count = hexagramDao.getHexagramCount()
            if (count == 0) {
                try {
                    val jsonString = context.assets.open("hexagrams.json").bufferedReader().use { it.readText() }
                    val hexagrams = hexagramListAdapter.fromJson(jsonString) ?: emptyList()
                    if (hexagrams.isNotEmpty()) {
                        hexagramDao.insertHexagrams(hexagrams)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 检查数据是否已初始化
     */
    suspend fun isDataInitialized(): Boolean {
        return withContext(Dispatchers.IO) {
            hexagramDao.getHexagramCount() == 64
        }
    }

    // 兼容旧接口
    suspend fun initializeData(ctx: Context) = initializeData()
}
