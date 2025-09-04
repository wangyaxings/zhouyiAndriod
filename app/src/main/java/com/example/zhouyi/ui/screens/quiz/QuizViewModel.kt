package com.example.zhouyi.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zhouyi.core.algorithm.OptionGenerator
import com.example.zhouyi.core.algorithm.QuestionGenerator
import com.example.zhouyi.core.algorithm.SrsManager
import com.example.zhouyi.data.model.Hexagram
import com.example.zhouyi.data.preferences.AppPreferences
import com.example.zhouyi.data.repository.AttemptRepository
import com.example.zhouyi.data.repository.CheckInRepository
import com.example.zhouyi.data.repository.HexagramRepository
import com.example.zhouyi.data.repository.SrsRepository
import com.example.zhouyi.data.repository.WrongBookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * 练习页面ViewModel
 * 管理答题逻辑和状态
 */
class QuizViewModel(
    private val hexagramRepository: HexagramRepository,
    private val attemptRepository: AttemptRepository,
    private val wrongBookRepository: WrongBookRepository,
    private val srsRepository: SrsRepository,
    private val checkInRepository: CheckInRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val questionGenerator = QuestionGenerator()
    private val optionGenerator = OptionGenerator()
    private val srsManager = SrsManager()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var allHexagrams: List<Hexagram> = emptyList()
    private var currentQuestionIndex = 0
    private var totalQuestions = 0
    private var correctCount = 0
    private var currentQuestion: Hexagram? = null
    private var currentOptions: List<Hexagram> = emptyList()
    private var correctOptionIndex = 0

    /**
     * 开始练习
     */
    fun startQuiz() {
        viewModelScope.launch {
            try {
                // 加载所有卦象
                allHexagrams = hexagramRepository.getAllHexagrams().first()

                // 获取用户设置
                val dailyGoal = preferences.dailyGoal.first()
                val showNumber = preferences.showNumber.first()
                val reinforcementMode = preferences.reinforcementMode.first()

                // 设置题目生成器
                questionGenerator.setReinforcementMode(reinforcementMode)

                // 设置总题数
                totalQuestions = dailyGoal

                // 生成第一题
                generateNextQuestion()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showNumber = showNumber,
                    totalQuestions = totalQuestions
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "启动练习失败",
                    isLoading = false
                )
            }
        }
    }

    /**
     * 生成下一题
     */
    private fun generateNextQuestion() {
        if (currentQuestionIndex >= totalQuestions) {
            // 练习完成
            _uiState.value = _uiState.value.copy(
                isQuizComplete = true
            )
            return
        }

        // 生成题目
        val questionId = questionGenerator.getNextQuestion()
        currentQuestion = allHexagrams.find { it.id == questionId }

        if (currentQuestion != null) {
            // 生成选项
            val optionsWithPosition = optionGenerator.generateOptionsWithCorrectPosition(
                currentQuestion!!,
                allHexagrams
            )

            currentOptions = optionsWithPosition.options
            correctOptionIndex = optionsWithPosition.correctPosition

            _uiState.value = _uiState.value.copy(
                currentQuestion = currentQuestion,
                currentOptions = currentOptions,
                currentQuestionIndex = currentQuestionIndex,
                showingResult = false,
                selectedOption = null,
                correctOption = null,
                isCorrect = false
            )
        }
    }

    /**
     * 选择选项
     */
    fun selectOption(selectedIndex: Int) {
        if (currentQuestion == null || uiState.value.showingResult) {
            return
        }

        val isCorrect = selectedIndex == correctOptionIndex

        // 更新统计
        if (isCorrect) {
            correctCount++
        }

        // 记录答题
        recordAttempt(selectedIndex, isCorrect)

        // 更新SRS状态
        updateSrsState(isCorrect)

        // 更新UI状态
        _uiState.value = _uiState.value.copy(
            selectedOption = selectedIndex,
            correctOption = correctOptionIndex,
            isCorrect = isCorrect,
            showingResult = true,
            correctCount = correctCount,
            accuracy = calculateAccuracy()
        )

        // 如果答错，添加到错题本
        if (!isCorrect) {
            addToWrongBook()
        }
    }

    /**
     * 下一题
     */
    fun nextQuestion() {
        currentQuestionIndex++

        // 检查是否完成练习
        if (currentQuestionIndex >= totalQuestions) {
            // 练习完成，自动打卡
            autoCheckIn()
        } else {
            generateNextQuestion()
        }
    }

    /**
     * 自动打卡
     */
    private fun autoCheckIn() {
        viewModelScope.launch {
            try {
                // 计算本次练习的答题数量和正确数量
                val questionCount = totalQuestions
                val correctCount = correctCount

                // 调用打卡
                checkInRepository.checkIn(questionCount, correctCount)

                // 更新UI状态为完成
                _uiState.value = _uiState.value.copy(
                    isQuizComplete = true
                )
            } catch (e: Exception) {
                // 打卡失败，但不影响练习完成
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isQuizComplete = true
                )
            }
        }
    }

    /**
     * 记录答题
     */
    private fun recordAttempt(selectedIndex: Int, isCorrect: Boolean) {
        viewModelScope.launch {
            try {
                val attempt = com.example.zhouyi.data.model.Attempt(
                    hexagramId = currentQuestion!!.id,
                    timestamp = System.currentTimeMillis(),
                    isCorrect = isCorrect,
                    selectedOption = selectedIndex,
                    correctOption = correctOptionIndex,
                    options = currentOptions.map { it.id },
                    mode = "practice"
                )

                attemptRepository.insertAttempt(attempt)
            } catch (e: Exception) {
                // 记录失败，但不影响答题流程
                e.printStackTrace()
            }
        }
    }

    /**
     * 更新SRS状态
     */
    private fun updateSrsState(isCorrect: Boolean) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val newSrsState = srsManager.processAnswer(
                    currentQuestion!!.id,
                    isCorrect,
                    currentTime
                )

                srsRepository.updateSrsState(newSrsState)
            } catch (e: Exception) {
                // SRS更新失败，但不影响答题流程
                e.printStackTrace()
            }
        }
    }

    /**
     * 添加到错题本
     */
    private fun addToWrongBook() {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val wrongBook = com.example.zhouyi.data.model.WrongBook(
                    hexagramId = currentQuestion!!.id,
                    wrongCount = 1,
                    lastWrongTimestamp = currentTime,
                    firstWrongTimestamp = currentTime
                )

                wrongBookRepository.insertWrongBook(wrongBook)
            } catch (e: Exception) {
                // 错题本添加失败，但不影响答题流程
                e.printStackTrace()
            }
        }
    }

    /**
     * 计算正确率
     */
    private fun calculateAccuracy(): Double {
        return if (currentQuestionIndex + 1 > 0) {
            (correctCount * 100.0) / (currentQuestionIndex + 1)
        } else {
            0.0
        }
    }

    /**
     * 重新开始练习
     */
    fun restartQuiz() {
        currentQuestionIndex = 0
        correctCount = 0
        questionGenerator.reset()
        optionGenerator.reset()

        _uiState.value = QuizUiState()
        startQuiz()
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 练习页面UI状态
 */
data class QuizUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentQuestion: Hexagram? = null,
    val currentOptions: List<Hexagram> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val selectedOption: Int? = null,
    val correctOption: Int? = null,
    val isCorrect: Boolean = false,
    val showingResult: Boolean = false,
    val correctCount: Int = 0,
    val accuracy: Double = 0.0,
    val showNumber: Boolean = true,
    val isQuizComplete: Boolean = false
)
