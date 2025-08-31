package com.example.zhouyi.ui.screens.wrongbook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

/**
 * 错题本页面
 * 显示错题列表和复习功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrongBookScreen(
    viewModel: WrongBookViewModel,
    onNavigateBack: () -> Unit,
    onStartReview: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadWrongBook()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("错题本") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.wrongItems.isNotEmpty()) {
                        IconButton(onClick = onStartReview) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "开始复习")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 统计信息
            if (uiState.wrongItems.isNotEmpty()) {
                WrongBookStats(
                    stats = uiState.stats,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            // 错题列表
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.wrongItems.isEmpty()) {
                EmptyWrongBook(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            } else {
                WrongBookList(
                    wrongItems = uiState.wrongItems,
                    onItemClick = { hexagram ->
                        viewModel.selectHexagram(hexagram)
                    },
                    onRemoveFromWrongBook = { hexagramId ->
                        viewModel.removeFromWrongBook(hexagramId)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * 错题本统计信息
 */
@Composable
private fun WrongBookStats(
    stats: WrongBookStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "错题总数",
                value = stats.totalCount.toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            StatItem(
                label = "高频错题",
                value = stats.highFrequencyCount.toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            StatItem(
                label = "最近错题",
                value = stats.recentCount.toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
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
            color = color.copy(alpha = 0.8f)
        )
    }
}

/**
 * 空错题本状态
 */
@Composable
private fun EmptyWrongBook(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Book,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "暂无错题",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "开始练习后，答错的题目会出现在这里",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

/**
 * 错题列表
 */
@Composable
private fun WrongBookList(
    wrongItems: List<WrongBookItem>,
    onItemClick: (Hexagram) -> Unit,
    onRemoveFromWrongBook: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(wrongItems) { item ->
            WrongBookItemCard(
                item = item,
                onClick = { onItemClick(item.hexagram) },
                onRemove = { onRemoveFromWrongBook(item.hexagram.id) }
            )
        }
    }
}

/**
 * 错题项卡片
 */
@Composable
private fun WrongBookItemCard(
    item: WrongBookItem,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 卦象显示
            SmallHexagramCanvas(
                hexagram = item.hexagram,
                modifier = Modifier.width(60.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 卦象信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.hexagram.getFullName(),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = item.hexagram.getTrigramDescription(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 错题统计
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "错${item.wrongCount}次",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = "最后错题: ${item.getLastWrongDaysText()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 操作按钮
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "练习",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "移除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
