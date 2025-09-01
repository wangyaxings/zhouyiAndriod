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
    val linesBits: String // 六爻二进制表示（历史字段，可能存在方向不一致，仅作备用）
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
     * 获取六爻布尔数组（用于绘制）
     * 规范对齐：依据八卦三爻码（下→上）生成，最终返回顺序为【自上而下】的6爻列表：上卦(3) + 下卦(3)
     * 八卦三爻码（下→上）：乾111、兑110、离101、震100、巽011、坎010、艮001、坤000。
     * 说明：不再直接信任 linesBits 的方向，优先根据上/下卦还原，确保不会出现上下卦颠倒。
     */
    fun getLines(): List<Boolean> {
        val upperB2T = trigramBottomToTopOrNull(upperTrigram)
        val lowerB2T = trigramBottomToTopOrNull(lowerTrigram)

        val (upper, lower) = if (upperB2T != null && lowerB2T != null) {
            upperB2T to lowerB2T
        } else {
            // 兜底：从 linesBits（规范为下→上：下3 + 上3）恢复
            val bits = linesBits.filter { it == '0' || it == '1' }
            if (bits.length == 6) {
                val b = bits.map { it == '1' }
                // linesBits 规范：下(0..2)、上(3..5)，均为下→上
                val lower = listOf(b[0], b[1], b[2])
                val upper = listOf(b[3], b[4], b[5])
                upper to lower
            } else {
                // 完全不可用时，返回全阴（坤）
                listOf(false, false, false) to listOf(false, false, false)
            }
        }

        // 将（下→上）的三爻码转换为（上→下）以用于绘制
        val upperTopDown = listOf(upper[2], upper[1], upper[0])
        val lowerTopDown = listOf(lower[2], lower[1], lower[0])

        return upperTopDown + lowerTopDown
    }

    /**
     * 将八卦名映射为三爻码（下→上）的布尔列表
     */
    private fun trigramBottomToTopOrNull(name: String): List<Boolean>? {
        return when (name.trim()) {
            "乾", "Qian", "qian" -> listOf(true, true, true)
            "兑", "Dui", "dui" -> listOf(true, true, false)
            "离", "Li", "li" -> listOf(true, false, true)
            "震", "Zhen", "zhen" -> listOf(true, false, false)
            "巽", "Xun", "xun" -> listOf(false, true, true)
            "坎", "Kan", "kan" -> listOf(false, true, false)
            "艮", "Gen", "gen" -> listOf(false, false, true)
            "坤", "Kun", "kun" -> listOf(false, false, false)
            else -> null
        }
    }

    /**
     * 获取上卦（外卦）的三爻
     * 返回自上而下的三爻
     */
    fun getUpperTrigramLines(): List<Boolean> {
        return getLines().take(3)
    }

    /**
     * 获取下卦（内卦）的三爻
     * 返回自上而下的三爻
     */
    fun getLowerTrigramLines(): List<Boolean> {
        return getLines().drop(3).take(3)
    }
}
