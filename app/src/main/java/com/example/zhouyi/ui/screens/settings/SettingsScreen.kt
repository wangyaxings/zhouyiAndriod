package com.example.zhouyi.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 设置页面
 */
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
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
            // 学习设置
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "学习设置",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 每日目标
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "每日目标",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "设置每日练习的题目数量",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.showDailyGoalDialog() }
                        ) {
                            Text("${uiState.dailyGoal}题")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 显示编号
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "显示编号",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "在卦象名称前显示编号",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.showNumber,
                            onCheckedChange = { viewModel.setShowNumber(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 自动下一题
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "自动下一题",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "答题后自动进入下一题",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.autoNext,
                            onCheckedChange = { viewModel.setAutoNext(it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 界面设置
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "界面设置",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 深色主题
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "深色主题",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "使用深色主题模式",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.darkTheme,
                            onCheckedChange = { viewModel.setDarkTheme(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 字体大小
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "字体大小",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "调整应用字体大小",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.showFontSizeDialog() }
                        ) {
                            Text("${uiState.fontSize}sp")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 反馈设置
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "反馈设置",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 震动
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "震动反馈",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "答题时提供震动反馈",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.vibration,
                            onCheckedChange = { viewModel.setVibration(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 音效
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "音效反馈",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "答题时播放音效",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.sound,
                            onCheckedChange = { viewModel.setSound(it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 学习统计
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "学习统计",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "连续学习天数",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "当前连续学习天数",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${uiState.studyStreak}天",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.resetStudyStats() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("重置学习统计")
                    }
                }
            }
        }

        // 每日目标对话框
        if (uiState.showDailyGoalDialog) {
            DailyGoalDialog(
                currentGoal = uiState.dailyGoal,
                onDismiss = { viewModel.hideDailyGoalDialog() },
                onConfirm = { goal ->
                    viewModel.setDailyGoal(goal)
                    viewModel.hideDailyGoalDialog()
                }
            )
        }

        // 字体大小对话框
        if (uiState.showFontSizeDialog) {
            FontSizeDialog(
                currentSize = uiState.fontSize,
                onDismiss = { viewModel.hideFontSizeDialog() },
                onConfirm = { size ->
                    viewModel.setFontSize(size)
                    viewModel.hideFontSizeDialog()
                }
            )
        }
    }
}

@Composable
private fun DailyGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var goal by remember { mutableStateOf(currentGoal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置每日目标") },
        text = {
            Column {
                Text("选择每日练习的题目数量：")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(10, 20, 30, 50, 100).forEach { value ->
                        OutlinedButton(
                            onClick = { goal = value },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (goal == value) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Text("$value")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(goal) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun FontSizeDialog(
    currentSize: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var size by remember { mutableStateOf(currentSize) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置字体大小") },
        text = {
            Column {
                Text("选择字体大小：")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(14, 16, 18, 20, 22).forEach { value ->
                        OutlinedButton(
                            onClick = { size = value },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (size == value) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Text("${value}sp")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(size) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
