package com.example.zhouyi.data.model

/**
 * 卦序歌数据模型
 * 包含六十四卦的卦序歌文本和对应的卦名映射
 */
object HexagramSequenceSong {

    /**
     * 上经卦序歌（前30卦）- 按七字一句分割
     */
    val UPPER_SEQUENCE_LINES = listOf(
        "乾坤屯蒙需讼师",
        "比小畜兮履泰否",
        "同人大有谦豫随",
        "蛊临观兮噬嗑贲",
        "剥复无妄大畜颐",
        "大过坎离三十备"
    )

    /**
     * 下经卦序歌（后34卦）- 按七字一句分割
     */
    val LOWER_SEQUENCE_LINES = listOf(
        "咸恒遁兮及大壮",
        "晋与明夷家人睽",
        "蹇解损益夬姤萃",
        "升困井革鼎震继",
        "艮渐归妹丰旅巽",
        "兑涣节兮中孚至",
        "小过既济兼未济",
        "是为下经三十四"
    )

    /**
     * 完整的卦序歌文本（用于查找位置）
     */
    const val FULL_SEQUENCE = "乾坤屯蒙需讼师，比小畜兮履泰否，同人大有谦豫随，蛊临观兮噬嗑贲，剥复无妄大畜颐，大过坎离三十备。咸恒遁兮及大壮，晋与明夷家人睽，蹇解损益夬姤萃，升困井革鼎震继，艮渐归妹丰旅巽，兑涣节兮中孚至，小过既济兼未济，是为下经三十四。"

    /**
     * 卦名到卦序的映射（从卦序歌中提取的卦名）
     * 按照文王卦序排列（1-64）
     */
    val HEXAGRAM_NAMES = listOf(
        "乾", "坤", "屯", "蒙", "需", "讼", "师", "比",
        "小畜", "履", "泰", "否", "同人", "大有", "谦", "豫",
        "随", "蛊", "临", "观", "噬嗑", "贲", "剥", "复",
        "无妄", "大畜", "颐", "大过", "坎", "离",
        "咸", "恒", "遁", "大壮", "晋", "明夷", "家人", "睽",
        "蹇", "解", "损", "益", "夬", "姤", "萃", "升",
        "困", "井", "革", "鼎", "震", "艮", "渐", "归妹",
        "丰", "旅", "巽", "兑", "涣", "节", "中孚", "小过",
        "既济", "未济"
    )

    /**
     * 根据卦序获取卦名（卦序歌中的简化名称）
     */
    fun getHexagramName(sequence: Int): String? {
        return if (sequence in 1..64) {
            HEXAGRAM_NAMES[sequence - 1]
        } else null
    }

    /**
     * 根据卦名获取卦序
     */
    fun getHexagramSequence(name: String): Int? {
        val index = HEXAGRAM_NAMES.indexOf(name)
        return if (index != -1) index + 1 else null
    }

    /**
     * 获取卦序歌中指定卦名的位置信息
     * 返回卦名在卦序歌中的开始和结束位置
     */
    fun getHexagramPositionInSong(hexagramId: Int): Pair<Int, Int>? {
        val name = getHexagramName(hexagramId) ?: return null

        // 在完整卦序歌中查找卦名位置
        val fullSong = FULL_SEQUENCE
        val startIndex = fullSong.indexOf(name)

        return if (startIndex != -1) {
            Pair(startIndex, startIndex + name.length)
        } else null
    }

    /**
     * 检查卦名是否在卦序歌中
     */
    fun isHexagramInSong(name: String): Boolean {
        return FULL_SEQUENCE.contains(name)
    }

    /**
     * 从完整的卦名中提取卦序歌中使用的简化名称
     * 例如："乾为天" -> "乾", "水雷屯" -> "屯", "风天小畜" -> "小畜"
     */
    fun extractSimpleName(fullName: String): String {
        // 处理特殊情况：纯卦（如"乾为天"、"坤为地"）
        if (fullName.contains("为")) {
            return fullName.substring(0, fullName.indexOf("为"))
        }

        // 处理复合卦名，提取最后一个字或两个字
        return when {
            fullName.endsWith("小畜") -> "小畜"
            fullName.endsWith("大畜") -> "大畜"
            fullName.endsWith("大过") -> "大过"
            fullName.endsWith("小过") -> "小过"
            fullName.endsWith("既济") -> "既济"
            fullName.endsWith("未济") -> "未济"
            fullName.endsWith("中孚") -> "中孚"
            fullName.endsWith("归妹") -> "归妹"
            fullName.endsWith("噬嗑") -> "噬嗑"
            fullName.endsWith("明夷") -> "明夷"
            fullName.endsWith("家人") -> "家人"
            fullName.endsWith("大壮") -> "大壮"
            fullName.endsWith("无妄") -> "无妄"
            fullName.endsWith("同人") -> "同人"
            fullName.endsWith("大有") -> "大有"
            fullName.endsWith("小畜") -> "小畜"
            fullName.endsWith("大畜") -> "大畜"
            fullName.endsWith("大过") -> "大过"
            fullName.endsWith("小过") -> "小过"
            fullName.endsWith("既济") -> "既济"
            fullName.endsWith("未济") -> "未济"
            fullName.endsWith("中孚") -> "中孚"
            fullName.endsWith("归妹") -> "归妹"
            fullName.endsWith("噬嗑") -> "噬嗑"
            fullName.endsWith("明夷") -> "明夷"
            fullName.endsWith("家人") -> "家人"
            fullName.endsWith("大壮") -> "大壮"
            fullName.endsWith("无妄") -> "无妄"
            fullName.endsWith("同人") -> "同人"
            fullName.endsWith("大有") -> "大有"
            else -> fullName.takeLast(1) // 默认取最后一个字
        }
    }
}
