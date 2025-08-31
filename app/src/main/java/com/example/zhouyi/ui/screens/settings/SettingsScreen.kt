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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 设置页面
 * 管理用户偏好设置
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            SettingsSection(
                title = "学习设置",
                icon = Icons.Default.School
            ) {
                // 每日目标
                SettingsItem(
                    title = "每日目标",
                    subtitle = "${uiState.dailyGoal} 题",
                    icon = Icons.Default.Target,
                    onClick = {
                        viewModel.showDailyGoalDialog()
                    }
                )

                // 强化模式
                SettingsSwitch(
                    title = "强化模式",
                    subtitle = "优先复习错题",
                    icon = Icons.Default.Psychology,
                    checked = uiState.reinforcementMode,
                    onCheckedChange = { checked ->
                        viewModel.setReinforcementMode(checked)
                    }
                )

                // 显示编号
                SettingsSwitch(
                    title = "显示编号",
                    subtitle = "在选项中显示卦象编号",
                    icon = Icons.Default.Numbers,
                    checked = uiState.showNumber,
                    onCheckedChange = { checked ->
                        viewModel.setShowNumber(checked)
                    }
                )

                // 自动下一题
                SettingsSwitch(
                    title = "自动下一题",
                    subtitle = "答题后自动进入下一题",
                    icon = Icons.Default.PlayArrow,
                    checked = uiState.autoNext,
                    onCheckedChange = { checked ->
                        viewModel.setAutoNext(checked)
                    }
                )
            }

            // 界面设置
            SettingsSection(
                title = "界面设置",
                icon = Icons.Default.Palette
            ) {
                // 深色主题
                SettingsSwitch(
                    title = "深色主题",
                    subtitle = "使用深色界面",
                    icon = Icons.Default.DarkMode,
                    checked = uiState.darkTheme,
                    onCheckedChange = { checked ->
                        viewModel.setDarkTheme(checked)
                    }
                )

                // 字体大小
                SettingsItem(
                    title = "字体大小",
                    subtitle = getFontSizeText(uiState.fontSize),
                    icon = Icons.Default.TextFields,
                    onClick = {
                        viewModel.showFontSizeDialog()
                    }
                )
            }

            // 反馈设置
            SettingsSection(
                title = "反馈设置",
                icon = Icons.Default.Notifications
            ) {
                // 震动反馈
                SettingsSwitch(
                    title = "震动反馈",
                    subtitle = "答题时提供震动反馈",
                    icon = Icons.Default.Vibration,
                    checked = uiState.vibrationEnabled,
                    onCheckedChange = { checked ->
                        viewModel.setVibrationEnabled(checked)
                    }
                )

                // 音效反馈
                SettingsSwitch(
                    title = "音效反馈",
                    subtitle = "答题时播放音效",
                    icon = Icons.Default.VolumeUp,
                    checked = uiState.soundEnabled,
                    onCheckedChange = { checked ->
                        viewModel.setSoundEnabled(checked)
                    }
                )
            }

            // 数据管理
            SettingsSection(
                title = "数据管理",
                icon = Icons.Default.Storage
            ) {
                // 重置学习数据
                SettingsItem(
                    title = "重置学习数据",
                    subtitle = "清除所有答题记录和进度",
                    icon = Icons.Default.Refresh,
                    onClick = {
                        viewModel.showResetDataDialog()
                    }
                )

                // 重置设置
                SettingsItem(
                    title = "重置设置",
                    subtitle = "恢复所有设置为默认值",
                    icon = Icons.Default.Restore,
                    onClick = {
                        viewModel.showResetSettingsDialog()
                    }
                )
            }

            // 关于
            SettingsSection(
                title = "关于",
                icon = Icons.Default.Info
            ) {
                SettingsItem(
                    title = "版本信息",
                    subtitle = "周易六十四卦 v1.0.0",
                    icon = Icons.Default.Apps,
                    onClick = { }
                )

                SettingsItem(
                    title = "开发者信息",
                    subtitle = "基于间隔复习算法的学习应用",
                    icon = Icons.Default.Person,
                    onClick = { }
                )
            }
        }
    }

    // 每日目标对话框
    if (uiState.showDailyGoalDialog) {
        DailyGoalDialog(
            currentGoal = uiState.dailyGoal,
            onConfirm = { goal ->
                viewModel.setDailyGoal(goal)
                viewModel.hideDailyGoalDialog()
            },
            onDismiss = {
                viewModel.hideDailyGoalDialog()
            }
        )
    }

    // 字体大小对话框
    if (uiState.showFontSizeDialog) {
        FontSizeDialog(
            currentSize = uiState.fontSize,
            onConfirm = { size ->
                viewModel.setFontSize(size)
                viewModel.hideFontSizeDialog()
            },
            onDismiss = {
                viewModel.hideFontSizeDialog()
            }
        )
    }

    // 重置数据确认对话框
    if (uiState.showResetDataDialog) {
        ResetDataDialog(
            onConfirm = {
                viewModel.resetLearningData()
                viewModel.hideResetDataDialog()
            },
            onDismiss = {
                viewModel.hideResetDataDialog()
            }
        )
    }

    // 重置设置确认对话框
    if (uiState.showResetSettingsDialog) {
        ResetSettingsDialog(
            onConfirm = {
                viewModel.resetSettings()
                viewModel.hideResetSettingsDialog()
            },
            onDismiss = {
                viewModel.hideResetSettingsDialog()
            }
        )
    }
}

/**
 * 设置分组
 */
@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            content()
        }
    }
}

/**
 * 设置项
 */
@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "设置",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 设置开关
 */
@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * 获取字体大小文本
 */
private fun getFontSizeText(size: Int): String {
    return when (size) {
        1 -> "小"
        2 -> "中"
        3 -> "大"
        else -> "中"
    }
}
