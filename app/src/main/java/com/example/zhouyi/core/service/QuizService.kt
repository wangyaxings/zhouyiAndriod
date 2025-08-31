package com.example.zhouyi.core.service

import android.content.Context
import com.example.zhouyi.core.algorithm.OptionGenerator
import com.example.zhouyi.core.algorithm.QuestionGenerator
import com.example.zhouyi.core.algorithm.SrsManager
import com.example.zhouyi.data.model.Attempt
import com.example.zhouyi.data.model.Hexagram
import com.example.zhouyi.data.repository.AttemptRepository
import com.example.zhouyi.data.repository.HexagramRepository
import com.example.zhouyi.data.repository.SrsRepository
import com.example.zhouyi.data.repository.WrongBookRepository
import kotlinx.coroutines.flow.first

/**
 * 答题服务
 * 整合所有业务逻辑，提供完整的答题功能
 */
class QuizService(context: Context) {

    private val hexagramRepository = HexagramRepository(context)
    private val attemptRepository = AttemptRepository(context)
    private val wrongBookRepository = WrongBookRepository(context)
    private val srsRepository = SrsRepository(context)

    private val questionGenerator = QuestionGenerator()
    private val optionGenerator = OptionGenerator()
    private val srsManager = SrsManager()

    private var allHexagrams: List<Hexagram> = emptyList()
    private var recentOptions: MutableList<String> = mutableListOf()

    /**
     * 初始化服务
     */
    suspend fun initialize() {
        // 确保数据已初始化
        if (!hexagramRepository.isDataInitialized()) {
            hexagramRepository.initializeData(context)
        }

        // 获取所有卦象
        allHexagrams = hexagramRepository.getAllHexagrams().first()

        // 初始化题目生成器
        questionGenerator.initializeDeck()
    }

    /**
     * 生成下一题
     */
    suspend fun generateNextQuestion(): QuizQuestion? {
        if (allHexagrams.isEmpty()) {
            return null
        }

        val questionId = questionGenerator.getNextQuestion()
        val correctHexagram = allHexagrams.find { it.id == questionId } ?: return null

        val options = optionGenerator.generateOptions(
            correctHexagram = correctHexagram,
            allHexagrams = allHexagrams,
            recentOptions = recentOptions.takeLast(5)
        )

        // 更新最近选项列表
        recentOptions.addAll(options.map { it.text })
        if (recentOptions.size > 10) {
            recentOptions.removeAt(0)
        }

        return QuizQuestion(
            hexagram = correctHexagram,
            options = options,
            roundInfo = questionGenerator.getCurrentRoundInfo()
        )
    }

    /**
     * 提交答案
     */
    suspend fun submitAnswer(
        question: QuizQuestion,
        selectedOption: OptionGenerator.Option,
        mode: String = "practice"
    ): AnswerResult {
        val isCorrect = selectedOption.isCorrect
        val timestamp = System.currentTimeMillis()

        // 创建答题记录
        val attempt = Attempt(
            hexagramId = question.hexagram.id,
            timestamp = timestamp,
            isCorrect = isCorrect,
            selectedOption = selectedOption.text,
            correctOption = question.hexagram.getDisplayName(),
            options = question.options.map { it.text },
            mode = mode
        )

        // 保存答题记录
        attemptRepository.insertAttempt(attempt)

        // 处理错题
        if (!isCorrect) {
            wrongBookRepository.handleWrongAnswer(question.hexagram.id)
        }

        // 更新SRS状态
        srsRepository.processAnswer(question.hexagram.id, isCorrect)

        return AnswerResult(
            isCorrect = isCorrect,
            correctHexagram = question.hexagram,
            selectedOption = selectedOption,
            feedback = if (isCorrect) {
                "正确！${question.hexagram.getFullName()}"
            } else {
                "错误！正确答案是：${question.hexagram.getFullName()}"
            }
        )
    }

    /**
     * 生成错题练习
     */
    suspend fun generateWrongQuestion(): QuizQuestion? {
        val wrongIds = wrongBookRepository.getWrongHexagramIds()
        if (wrongIds.isEmpty()) {
            return null
        }

        val randomWrongId = wrongIds.random()
        val wrongHexagram = allHexagrams.find { it.id == randomWrongId } ?: return null

        val options = optionGenerator.generateOptions(
            correctHexagram = wrongHexagram,
            allHexagrams = allHexagrams,
            recentOptions = emptyList()
        )

        return QuizQuestion(
            hexagram = wrongHexagram,
            options = options,
            roundInfo = null,
            isWrongQuestion = true
        )
    }

    /**
     * 生成复习题目
     */
    suspend fun generateReviewQuestion(): QuizQuestion? {
        val dueIds = srsRepository.getDueHexagramIds()
        if (dueIds.isEmpty()) {
            return null
        }

        val randomDueId = dueIds.random()
        val dueHexagram = allHexagrams.find { it.id == randomDueId } ?: return null

        val options = optionGenerator.generateOptions(
            correctHexagram = dueHexagram,
            allHexagrams = allHexagrams,
            recentOptions = emptyList()
        )

        return QuizQuestion(
            hexagram = dueHexagram,
            options = options,
            roundInfo = null,
            isReviewQuestion = true
        )
    }

    /**
     * 获取今日统计
     */
    suspend fun getTodayStats(): TodayStats {
        val today = System.currentTimeMillis()
        val oneDayAgo = today - (24 * 60 * 60 * 1000)

        val totalAttempts = attemptRepository.getTotalCountSince(oneDayAgo)
        val correctAttempts = attemptRepository.getCorrectCountSince(oneDayAgo)
        val accuracy = attemptRepository.getAccuracySince(oneDayAgo)
        val studiedHexagrams = attemptRepository.getStudiedHexagramsSince(oneDayAgo).size

        return TodayStats(
            totalAttempts = totalAttempts,
            correctAttempts = correctAttempts,
            accuracy = accuracy,
            studiedHexagrams = studiedHexagrams
        )
    }

    /**
     * 重置生成器状态
     */
    fun resetGenerators() {
        questionGenerator.reset()
        optionGenerator.resetPositionPointer()
        recentOptions.clear()
    }

    /**
     * 答题问题数据类
     */
    data class QuizQuestion(
        val hexagram: Hexagram,
        val options: List<OptionGenerator.Option>,
        val roundInfo: QuestionGenerator.RoundInfo?,
        val isWrongQuestion: Boolean = false,
        val isReviewQuestion: Boolean = false
    )

    /**
     * 答题结果数据类
     */
    data class AnswerResult(
        val isCorrect: Boolean,
        val correctHexagram: Hexagram,
        val selectedOption: OptionGenerator.Option,
        val feedback: String
    )

    /**
     * 今日统计数据类
     */
    data class TodayStats(
        val totalAttempts: Int,
        val correctAttempts: Int,
        val accuracy: Float,
        val studiedHexagrams: Int
    )
}
