package com.example.zhouyi.ui.screens.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zhouyi.data.model.Hexagram
import com.example.zhouyi.ui.components.HexagramCanvas
import com.example.zhouyi.ui.components.SmallHexagramCanvas
import com.example.zhouyi.ui.components.HexagramSequenceSongView

/**
 * 练习页面
 * 实现5选1答题功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 显示打卡完成对话框
    if (uiState.isQuizComplete) {
        QuizCompletionDialog(
            correctCount = uiState.correctCount,
            totalQuestions = uiState.totalQuestions,
            accuracy = uiState.accuracy,
            onDismiss = {
                viewModel.restartQuiz()
            },
            onViewCalendar = {
                onNavigateToCalendar()
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.startQuiz()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("练习模式") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 显示进度
                    Text(
                        text = "${uiState.currentQuestionIndex + 1}/${uiState.totalQuestions}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 进度条
            LinearProgressIndicator(
                progress = if (uiState.totalQuestions > 0) {
                    (uiState.currentQuestionIndex + 1).toFloat() / uiState.totalQuestions
                } else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // 卦象显示区域（包含卦序歌）
            AnimatedVisibility(
                visible = uiState.currentQuestion != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                uiState.currentQuestion?.let { question ->
                    QuestionDisplayWithSong(
                        question = question,
                        showAnswer = uiState.showingResult,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }
            }

            // 选项区域
            AnimatedVisibility(
                visible = uiState.currentQuestion != null && !uiState.showingResult,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OptionsDisplay(
                    options = uiState.currentOptions,
                    showNumber = uiState.showNumber,
                    onOptionSelected = { selectedIndex ->
                        viewModel.selectOption(selectedIndex)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 结果反馈区域
            AnimatedVisibility(
                visible = uiState.showingResult,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                uiState.currentQuestion?.let { question ->
                    ResultFeedback(
                        question = question,
                        selectedOption = uiState.selectedOption,
                        correctOption = uiState.correctOption,
                        options = uiState.currentOptions,
                        isCorrect = uiState.isCorrect,
                        onContinue = {
                            viewModel.nextQuestion()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 统计信息
            if (uiState.showingResult) {
                QuizStats(
                    correctCount = uiState.correctCount,
                    totalAnswered = uiState.currentQuestionIndex + 1,
                    accuracy = uiState.accuracy,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 题目显示区域（包含卦序歌）
 */
@Composable
private fun QuestionDisplayWithSong(
    question: Hexagram,
    showAnswer: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 卦象显示在左侧
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "请选择这个卦象的名称",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 卦象绘制（轻微淡入+上移动效，按题目ID切换）
                AnimatedContent(
                    targetState = question.id,
                    transitionSpec = {
                        (fadeIn(tween(220)) + slideInVertically { it / 8 }) togetherWith
                            (fadeOut(tween(180)) + slideOutVertically { -it / 12 })
                    }, label = "hexagram-transition"
                ) {
                    HexagramCanvas(
                        hexagram = question,
                        modifier = Modifier
                            .width(120.dp)
                            .padding(bottom = 8.dp)
                    )
                }
            }

            // 卦序歌显示在右侧
            HexagramSequenceSongView(
                currentHexagramId = question.id,
                showAnswer = showAnswer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 选项显示区域
 */
@Composable
private fun OptionsDisplay(
    options: List<Hexagram>,
    showNumber: Boolean,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEachIndexed { index, hexagram ->
            OptionButton(
                option = hexagram,
                optionLabel = ('A' + index).toString(),
                showNumber = showNumber,
                onClick = { onOptionSelected(index) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 选项按钮
 */
@Composable
private fun OptionButton(
    option: Hexagram,
    optionLabel: String,
    showNumber: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = optionLabel,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 16.dp)
            )

            Text(
                text = option.nameZh,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * 结果反馈区域
 */
@Composable
private fun ResultFeedback(
    question: Hexagram,
    selectedOption: Int?,
    correctOption: Int?,
    options: List<Hexagram>,
    isCorrect: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 结果图标
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = if (isCorrect) "正确" else "错误",
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 16.dp),
                tint = if (isCorrect) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )

            // 结果文本
            Text(
                text = if (isCorrect) "回答正确！" else "回答错误",
                style = MaterialTheme.typography.headlineSmall,
                color = if (isCorrect) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 卦象信息
            Text(
                text = question.getFullName(),
                style = MaterialTheme.typography.titleLarge,
                color = if (isCorrect) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = question.getTrigramDescription(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCorrect) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 并排对比：你的选择 vs 正确答案
            val selectedHexagram = selectedOption?.let { options.getOrNull(it) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "你的选择",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(6.dp))
                    SmallHexagramCanvas(
                        hexagram = selectedHexagram ?: question,
                        modifier = Modifier.width(80.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "正确答案",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(6.dp))
                    SmallHexagramCanvas(
                        hexagram = question,
                        modifier = Modifier.width(80.dp)
                    )
                }
            }

            // 继续按钮
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCorrect) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            ) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("继续")
            }
        }
    }
}

/**
 * 练习统计
 */
@Composable
private fun QuizStats(
    correctCount: Int,
    totalAnswered: Int,
    accuracy: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "正确",
                value = correctCount.toString(),
                color = MaterialTheme.colorScheme.primary
            )
            StatItem(
                label = "总数",
                value = totalAnswered.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            StatItem(
                label = "正确率",
                value = "${String.format("%.1f", accuracy)}%",
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * 统计项
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
