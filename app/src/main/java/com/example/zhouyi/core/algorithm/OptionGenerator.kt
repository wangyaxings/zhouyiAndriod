package com.example.zhouyi.core.algorithm

import com.example.zhouyi.data.model.Hexagram
import kotlin.random.Random

/**
 * 选项生成器
 * 实现4选1选项生成算法，包含干扰项策略
 */
class OptionGenerator {

    private val recentDistractors = mutableListOf<Int>() // 最近5题的干扰项
    private val maxRecentDistractors = 5
    private var correctPositionPointer = 0 // A-D 位置循环指针，确保位置均衡

    companion object {
        const val OPTION_COUNT = 4
        const val CORRECT_OPTION_INDEX = 0 // 正确答案在选项中的索引
        const val MAX_SAME_UPPER_DISTRACTORS = 2 // 同上卦最多干扰项数量
        const val MAX_SAME_LOWER_DISTRACTORS = 1 // 同下卦最多干扰项数量
    }

    /**
     * 生成选项
     * @param correctHexagram 正确答案的卦象
     * @param allHexagrams 所有卦象列表
     * @return 包含正确答案和干扰项的选项列表
     */
    fun generateOptions(
        correctHexagram: Hexagram,
        allHexagrams: List<Hexagram>
    ): List<Hexagram> {
        val distractors = generateDistractors(correctHexagram, allHexagrams)
        val options = mutableListOf<Hexagram>()

        // 添加正确答案
        options.add(correctHexagram)

        // 添加干扰项
        options.addAll(distractors)

        // 随机打乱选项顺序
        options.shuffle()

        return options
    }

    /**
     * 生成干扰项（总计3个，配合1个正确项=4个选项）
     * 策略：
     * 1. 同上卦优先抽2个
     * 2. 同下卦优先抽1个
     * 3. 若不足3个，则从全局补齐
     * 4. 与最近5题避免重复干扰项
     */
    private fun generateDistractors(
        correctHexagram: Hexagram,
        allHexagrams: List<Hexagram>
    ): List<Hexagram> {
        val distractors = mutableListOf<Hexagram>()
        val usedIds = mutableSetOf<Int>()
        usedIds.add(correctHexagram.id)

        // 1. 从同上卦集合随机取2个
        val sameUpperTrigram = allHexagrams.filter {
            it.upperTrigram == correctHexagram.upperTrigram &&
            it.id != correctHexagram.id &&
            it.id !in recentDistractors
        }

        val upperDistractors = sameUpperTrigram.shuffled().take(MAX_SAME_UPPER_DISTRACTORS)
        distractors.addAll(upperDistractors)
        usedIds.addAll(upperDistractors.map { it.id })

        // 2. 从同下卦集合随机取1个
        val sameLowerTrigram = allHexagrams.filter {
            it.lowerTrigram == correctHexagram.lowerTrigram &&
            it.id != correctHexagram.id &&
            it.id !in usedIds &&
            it.id !in recentDistractors
        }

        if (sameLowerTrigram.isNotEmpty()) {
            val lowerDistractor = sameLowerTrigram.random()
            distractors.add(lowerDistractor)
            usedIds.add(lowerDistractor.id)
        }

        // 3. 若不足3个，从全局补齐
        while (distractors.size < 3) {
            val remaining = allHexagrams.filter {
                it.id != correctHexagram.id && it.id !in usedIds
            }

            if (remaining.isNotEmpty()) {
                val additionalDistractor = remaining.random()
                distractors.add(additionalDistractor)
                usedIds.add(additionalDistractor.id)
            } else {
                break
            }
        }

        // 更新最近使用的干扰项
        updateRecentDistractors(distractors.map { it.id })

        return distractors.take(3) // 确保只返回3个干扰项
    }

    /**
     * 更新最近使用的干扰项列表
     */
    private fun updateRecentDistractors(newDistractors: List<Int>) {
        recentDistractors.addAll(newDistractors)

        // 保持列表长度不超过限制
        while (recentDistractors.size > maxRecentDistractors) {
            recentDistractors.removeAt(0)
        }
    }

    /**
     * 获取正确答案在选项中的位置
     * 使用循环指针确保A-E位置均衡分布
     */
    fun getCorrectOptionPosition(): Int {
        val position = correctPositionPointer
        correctPositionPointer = (correctPositionPointer + 1) % OPTION_COUNT
        return position
    }

    /**
     * 获取选项标签（A-D）
     */
    fun getOptionLabel(index: Int): String {
        return ('A' + index).toString()
    }

    /**
     * 重置生成器状态
     */
    fun reset() {
        recentDistractors.clear()
        correctPositionPointer = 0
    }

    /**
     * 获取最近使用的干扰项（用于调试）
     */
    fun getRecentDistractors(): List<Int> {
        return recentDistractors.toList()
    }

    /**
     * 获取当前正确位置指针（用于调试）
     */
    fun getCurrentPositionPointer(): Int {
        return correctPositionPointer
    }

    /**
     * 验证选项位置均衡性（用于测试）
     * 检查A-E位置作为正确答案的分布
     */
    fun validateOptionPositionBalance(iterations: Int): PositionBalanceValidationResult {
        val positionCount = mutableMapOf<String, Int>()

        // 初始化位置计数
        for (i in 0 until OPTION_COUNT) {
            positionCount[getOptionLabel(i)] = 0
        }

        // 运行指定次数
        repeat(iterations) {
            val position = getCorrectOptionPosition()
            val label = getOptionLabel(position)
            positionCount[label] = positionCount[label]!! + 1
        }

        // 计算统计信息
        val counts = positionCount.values.toList()
        val mean = counts.average()
        val variance = counts.map { (it - mean) * (it - mean) }.average()
        val standardDeviation = kotlin.math.sqrt(variance)
        val coefficientOfVariation = standardDeviation / mean

        return PositionBalanceValidationResult(
            positionCount = positionCount,
            mean = mean,
            standardDeviation = standardDeviation,
            coefficientOfVariation = coefficientOfVariation,
            isAcceptable = coefficientOfVariation <= 0.03 // 3%阈值
        )
    }

    /**
     * 生成选项并返回正确答案位置
     */
    fun generateOptionsWithCorrectPosition(
        correctHexagram: Hexagram,
        allHexagrams: List<Hexagram>
    ): OptionsWithPosition {
        val options = generateOptions(correctHexagram, allHexagrams).toMutableList()

        // 目标位置（用于均衡A-E分布）
        val targetPosition = getCorrectOptionPosition()

        // 确保正确答案出现在目标位置
        val currentIndex = options.indexOfFirst { it.id == correctHexagram.id }
        if (currentIndex != -1 && currentIndex != targetPosition && targetPosition in 0 until OPTION_COUNT && options.size >= OPTION_COUNT) {
            val tmp = options[targetPosition]
            options[targetPosition] = options[currentIndex]
            options[currentIndex] = tmp
        }

        return OptionsWithPosition(
            options = options,
            correctPosition = targetPosition,
            correctPositionLabel = getOptionLabel(targetPosition)
        )
    }

    /**
     * 检查干扰项质量
     * 评估干扰项的相似度和难度
     */
    fun evaluateDistractorQuality(
        correctHexagram: Hexagram,
        distractors: List<Hexagram>
    ): DistractorQualityReport {
        val sameUpperCount = distractors.count { it.upperTrigram == correctHexagram.upperTrigram }
        val sameLowerCount = distractors.count { it.lowerTrigram == correctHexagram.lowerTrigram }
        val sameElementCount = distractors.count {
            it.upperElement == correctHexagram.upperElement ||
            it.lowerElement == correctHexagram.lowerElement
        }

        val qualityScore = calculateQualityScore(sameUpperCount, sameLowerCount, sameElementCount)

        return DistractorQualityReport(
            sameUpperCount = sameUpperCount,
            sameLowerCount = sameLowerCount,
            sameElementCount = sameElementCount,
            qualityScore = qualityScore,
            isGoodQuality = qualityScore >= 0.7
        )
    }

    /**
     * 计算干扰项质量分数
     */
    private fun calculateQualityScore(
        sameUpperCount: Int,
        sameLowerCount: Int,
        sameElementCount: Int
    ): Double {
        // 质量评分算法：相似度越高，质量越好
        val upperScore = sameUpperCount * 0.3
        val lowerScore = sameLowerCount * 0.2
        val elementScore = sameElementCount * 0.1

        return (upperScore + lowerScore + elementScore).coerceIn(0.0, 1.0)
    }
}

/**
 * 选项位置均衡验证结果
 */
data class PositionBalanceValidationResult(
    val positionCount: Map<String, Int>,
    val mean: Double,
    val standardDeviation: Double,
    val coefficientOfVariation: Double,
    val isAcceptable: Boolean
)

/**
 * 选项和正确答案位置
 */
data class OptionsWithPosition(
    val options: List<Hexagram>,
    val correctPosition: Int,
    val correctPositionLabel: String
)

/**
 * 干扰项质量报告
 */
data class DistractorQualityReport(
    val sameUpperCount: Int,
    val sameLowerCount: Int,
    val sameElementCount: Int,
    val qualityScore: Double,
    val isGoodQuality: Boolean
)
