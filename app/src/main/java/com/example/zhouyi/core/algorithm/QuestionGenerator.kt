package com.example.zhouyi.core.algorithm

import com.example.zhouyi.data.model.Hexagram
import kotlin.random.Random

/**
 * 题目生成器
 * 实现等概率抽题算法（Deck洗牌法）
 */
class QuestionGenerator {

    private var currentDeck: MutableList<Int> = mutableListOf()
    private var currentIndex = 0
    private var isReinforcementMode = false
    private val wrongHexagramIds = mutableSetOf<Int>()
    private var currentRound = 0

    companion object {
        const val TOTAL_HEXAGRAMS = 64
        const val ROUND_SIZE = 64 // 一轮64题，确保每卦都出现一次
        const val MAX_REINFORCEMENT_ADJUSTMENT = 0.2 // 强化模式最大调整比例
    }

    /**
     * 设置强化模式
     * 在强化模式下，错题会稍微提前出现，但仍保持等概率
     */
    fun setReinforcementMode(enabled: Boolean) {
        isReinforcementMode = enabled
    }

    /**
     * 添加错题ID（用于强化模式）
     */
    fun addWrongHexagram(hexagramId: Int) {
        wrongHexagramIds.add(hexagramId)
    }

    /**
     * 清除错题ID列表
     */
    fun clearWrongHexagrams() {
        wrongHexagramIds.clear()
    }

    /**
     * 获取下一题
     * 使用Deck洗牌法确保等概率
     */
    fun getNextQuestion(): Int {
        // 如果当前轮次用完，重新洗牌
        if (currentIndex >= currentDeck.size) {
            shuffleDeck()
            currentRound++
        }

        val hexagramId = currentDeck[currentIndex]
        currentIndex++

        return hexagramId
    }

    /**
     * 洗牌算法（Fisher-Yates）
     * 确保一轮内每卦只出现一次，且顺序随机
     */
    private fun shuffleDeck() {
        // 初始化牌组（1-64）
        currentDeck = (1..TOTAL_HEXAGRAMS).toMutableList()

        // Fisher-Yates洗牌算法
        for (i in currentDeck.size - 1 downTo 1) {
            val j = Random.nextInt(i + 1)
            currentDeck[i] = currentDeck[j].also { currentDeck[j] = currentDeck[i] }
        }

        // 强化模式：将错题稍微前移（最多前移20%位置）
        if (isReinforcementMode && wrongHexagramIds.isNotEmpty()) {
            applyReinforcementAdjustment()
        }

        currentIndex = 0
    }

    /**
     * 强化模式调整
     * 将错题在本轮末尾做微调前移，不改变同轮唯一性
     */
    private fun applyReinforcementAdjustment() {
        val maxAdjustment = (TOTAL_HEXAGRAMS * MAX_REINFORCEMENT_ADJUSTMENT).toInt()
        var adjustmentCount = 0

        // 从后往前查找错题
        for (i in currentDeck.size - 1 downTo maxAdjustment) {
            if (adjustmentCount >= maxAdjustment) break

            if (currentDeck[i] in wrongHexagramIds) {
                // 找到前移位置（避免与其他错题冲突）
                var targetIndex = i - maxAdjustment + adjustmentCount
                while (targetIndex < i && currentDeck[targetIndex] in wrongHexagramIds) {
                    targetIndex++
                }

                if (targetIndex < i) {
                    // 交换位置
                    val temp = currentDeck[i]
                    currentDeck.removeAt(i)
                    currentDeck.add(targetIndex, temp)
                    adjustmentCount++
                }
            }
        }
    }

    /**
     * 重置生成器状态
     */
    fun reset() {
        currentDeck.clear()
        currentIndex = 0
        currentRound = 0
        wrongHexagramIds.clear()
    }

    /**
     * 获取当前轮次进度
     */
    fun getRoundProgress(): Int {
        return currentIndex
    }

    /**
     * 获取当前轮次编号
     */
    fun getCurrentRound(): Int {
        return currentRound
    }

    /**
     * 检查是否完成当前轮次
     */
    fun isRoundComplete(): Boolean {
        return currentIndex >= currentDeck.size
    }

    /**
     * 获取当前轮次剩余题目数
     */
    fun getRemainingQuestions(): Int {
        return maxOf(0, currentDeck.size - currentIndex)
    }

    /**
     * 获取当前轮次的所有题目ID（用于调试）
     */
    fun getCurrentRoundQuestions(): List<Int> {
        return currentDeck.toList()
    }

    /**
     * 获取当前轮次的完成百分比
     */
    fun getRoundProgressPercentage(): Float {
        return if (currentDeck.isNotEmpty()) {
            (currentIndex * 100.0f / currentDeck.size)
        } else {
            0f
        }
    }

    /**
     * 验证等概率性（用于测试）
     * 运行指定轮次后检查各卦出现频率
     */
    fun validateEqualProbability(rounds: Int): ProbabilityValidationResult {
        val frequencyMap = mutableMapOf<Int, Int>()

        // 初始化频率统计
        for (i in 1..TOTAL_HEXAGRAMS) {
            frequencyMap[i] = 0
        }

        // 运行指定轮次
        repeat(rounds * ROUND_SIZE) {
            val questionId = getNextQuestion()
            frequencyMap[questionId] = frequencyMap[questionId]!! + 1
        }

        // 计算统计信息
        val frequencies = frequencyMap.values.toList()
        val mean = frequencies.average()
        val variance = frequencies.map { (it - mean) * (it - mean) }.average()
        val standardDeviation = kotlin.math.sqrt(variance)
        val coefficientOfVariation = standardDeviation / mean

        return ProbabilityValidationResult(
            frequencyMap = frequencyMap,
            mean = mean,
            standardDeviation = standardDeviation,
            coefficientOfVariation = coefficientOfVariation,
            isAcceptable = coefficientOfVariation <= 0.05 // 5%阈值
        )
    }

    /**
     * 获取错题列表
     */
    fun getWrongHexagramIds(): Set<Int> {
        return wrongHexagramIds.toSet()
    }

    /**
     * 检查指定卦象是否在错题列表中
     */
    fun isWrongHexagram(hexagramId: Int): Boolean {
        return hexagramId in wrongHexagramIds
    }
}

/**
 * 等概率验证结果
 */
data class ProbabilityValidationResult(
    val frequencyMap: Map<Int, Int>,
    val mean: Double,
    val standardDeviation: Double,
    val coefficientOfVariation: Double,
    val isAcceptable: Boolean
)
