package com.example.zhouyi.ui.screens.home

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

/**
 * 主页面
 * 应用的主入口，包含各种功能模块的入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToQuiz: () -> Unit,
    onNavigateToWrongBook: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCheckIn: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("周易六十四卦") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
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
            // 欢迎标题
            Text(
                text = "欢迎学习周易",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "掌握六十四卦，传承中华文化",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // 今日进度卡片
            if (uiState.todayProgress != null) {
                TodayProgressCard(
                    progress = uiState.todayProgress!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }

            // 主要功能按钮
            MainActionButtons(
                onStartPractice = onNavigateToQuiz,
                onWrongBook = onNavigateToWrongBook,
                onStatistics = onNavigateToStatistics,
                onCheckIn = onNavigateToCheckIn,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 学习统计卡片
            if (uiState.learningStats != null) {
                LearningStatsCard(
                    stats = uiState.learningStats!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // 复习提醒卡片
            if (uiState.reviewReminder != null) {
                ReviewReminderCard(
                    reminder = uiState.reviewReminder!!,
                    onStartReview = onNavigateToQuiz,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 今日进度卡片
 */
@Composable
private fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "今日进度",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress.completionRate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${progress.completedQuestions}/${progress.dailyGoal} 题",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (progress.accuracy > 0) {
                Text(
                    text = "正确率: ${String.format("%.1f", progress.accuracy)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * 主要功能按钮
 */
@Composable
private fun MainActionButtons(
    onStartPractice: () -> Unit,
    onWrongBook: () -> Unit,
    onStatistics: () -> Unit,
    onCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 开始练习按钮
        Button(
            onClick = onStartPractice,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("开始练习", style = MaterialTheme.typography.titleMedium)
        }

        // 错题本按钮
        OutlinedButton(
            onClick = onWrongBook,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("错题本", style = MaterialTheme.typography.titleMedium)
        }

        // 统计按钮
        OutlinedButton(
            onClick = onStatistics,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.Analytics,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("学习统计", style = MaterialTheme.typography.titleMedium)
        }

        // 打卡按钮
        OutlinedButton(
            onClick = onCheckIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("打卡日历", style = MaterialTheme.typography.titleMedium)
        }
    }
}

/**
 * 学习统计卡片
 */
@Composable
private fun LearningStatsCard(
    stats: LearningStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "学习统计",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "总题数",
                    value = "${stats.totalQuestions}",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "正确率",
                    value = "${String.format("%.1f", stats.accuracy)}%",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "已掌握",
                    value = "${stats.masteredCount}",
                    modifier = Modifier.weight(1f)
                )
            }
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

/**
 * 复习提醒卡片
 */
@Composable
private fun ReviewReminderCard(
    reminder: ReviewReminder,
    onStartReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "复习提醒",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = reminder.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                }

                Button(
                    onClick = onStartReview,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("开始复习")
                }
            }
        }
    }
}
