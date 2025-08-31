package com.example.zhouyi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 六十四卦数据模型
 * 对应式样书中的 hexagram 表
 */
@Entity(tableName = "hexagrams")
data class Hexagram(
    @PrimaryKey
    val id: Int, // 1-64，对应文王卦序
    val nameZh: String, // 中文卦名，如"未济"
    val upperTrigram: String, // 上卦，如"坎"
    val lowerTrigram: String, // 下卦，如"离"
    val upperElement: String, // 上卦五行/自然意象，如"水"
    val lowerElement: String, // 下卦五行/自然意象，如"火"
    val kingWenIndex: Int, // 文王卦序，与id同步
    val linesBits: String // 六爻二进制表示，自上而下，如"101010"
) {
    /**
     * 获取完整的卦名显示（如"水火未济"）
     */
    fun getFullName(): String {
        return "$upperElement$lowerElement$nameZh"
    }

    /**
     * 获取带编号的卦名（如"64 未济"）
     */
    fun getDisplayName(showNumber: Boolean = true): String {
        return if (showNumber) "$id $nameZh" else nameZh
    }

    /**
     * 获取卦象描述（如"坎离未济"）
     */
    fun getTrigramDescription(): String {
        return "$upperTrigram$lowerTrigram$nameZh"
    }

    /**
     * 获取自上而下的六爻布尔数组（阳爻=true，阴爻=false）
     * 由字段 linesBits 解析，期望长度为6，不足时补齐为阴爻
     */
    fun getLines(): List<Boolean> {
        if (linesBits.isEmpty()) return List(6) { false }
        val bits = linesBits.trim()
        val list = bits.map { ch -> ch == '1' }
        return when {
            list.size == 6 -> list
            list.size > 6 -> list.take(6)
            else -> list + List(6 - list.size) { false }
        }
    }
}
