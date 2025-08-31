package com.example.zhouyi.ui.screens.statistics

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
 * 统计页面
 * 显示学习统计和进度信息
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习统计") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
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
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // 总体统计卡片
                uiState.overallStats?.let { stats ->
                    OverallStatsCard(
                        stats = stats,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // 学习进度卡片
                if (uiState.learningProgress != null) {
                    LearningProgressCard(
                        progress = uiState.learningProgress!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // SRS状态卡片
                if (uiState.srsStats != null) {
                    SrsStatsCard(
                        stats = uiState.srsStats!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // 时间统计卡片
                if (uiState.timeStats != null) {
                    TimeStatsCard(
                        stats = uiState.timeStats!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // 错题统计卡片
                if (uiState.wrongStats != null) {
                    WrongStatsCard(
                        stats = uiState.wrongStats!!,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * 总体统计卡片
 */
@Composable
private fun OverallStatsCard(
    stats: OverallStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "总体统计",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "总题数",
                    value = stats.totalQuestions.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "正确率",
                    value = "${String.format("%.1f", stats.accuracy)}%",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    label = "学习天数",
                    value = stats.studyDays.toString(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * 学习进度卡片
 */
@Composable
private fun LearningProgressCard(
    progress: LearningProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "学习进度",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 已掌握进度
            ProgressItem(
                label = "已掌握",
                percentage = progress.masteredPercentage,
                count = progress.masteredCount,
                total = progress.totalCount,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 学习中进度
            ProgressItem(
                label = "学习中",
                percentage = progress.learningPercentage,
                count = progress.learningCount,
                total = progress.totalCount,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 复习中进度
            ProgressItem(
                label = "复习中",
                percentage = progress.reviewPercentage,
                count = progress.reviewCount,
                total = progress.totalCount,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

/**
 * 进度项
 */
@Composable
private fun ProgressItem(
    label: String,
    percentage: Float,
    count: Int,
    total: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count/$total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${String.format("%.1f", percentage)}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * SRS状态卡片
 */
@Composable
private fun SrsStatsCard(
    stats: SrsStats,
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
            Text(
                text = "间隔复习状态",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "总项目",
                    value = stats.totalItems.toString(),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                StatItem(
                    label = "待复习",
                    value = stats.dueItems.toString(),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                StatItem(
                    label = "已掌握",
                    value = stats.masteredItems.toString(),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            if (stats.recommendation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stats.recommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * 时间统计卡片
 */
@Composable
private fun TimeStatsCard(
    stats: TimeStats,
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
                text = "时间统计",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "今日答题",
                    value = stats.todayQuestions.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatItem(
                    label = "本周答题",
                    value = stats.weekQuestions.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatItem(
                    label = "本月答题",
                    value = stats.monthQuestions.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (stats.averageTimePerQuestion > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "平均答题时间: ${String.format("%.1f", stats.averageTimePerQuestion)}秒",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * 错题统计卡片
 */
@Composable
private fun WrongStatsCard(
    stats: WrongStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "错题统计",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "错题总数",
                    value = stats.totalWrong.toString(),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                StatItem(
                    label = "高频错题",
                    value = stats.highFrequencyWrong.toString(),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                StatItem(
                    label = "已解决",
                    value = stats.resolvedWrong.toString(),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            if (stats.mostWrongHexagram != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "最多错题: ${stats.mostWrongHexagram} (${stats.mostWrongCount}次)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
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
            color = color.copy(alpha = 0.8f)
        )
    }
}
