package com.example.zhouyi.ui.screens.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zhouyi.data.model.CheckInRecord
import com.example.zhouyi.data.model.CheckInStatistics
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

/**
 * 打卡日历界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    viewModel: CheckInViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadCheckInData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("打卡日历") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 统计数据卡片
            item {
                StatisticsCard(statistics = uiState.statistics)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 月份选择器
            item {
                MonthSelector(
                    currentYearMonth = uiState.currentYearMonth,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 日历网格
            item {
                CalendarGrid(
                    yearMonth = uiState.currentYearMonth,
                    checkInRecords = uiState.monthCheckIns,
                    onDateClick = { date -> viewModel.onDateClick(date) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 打卡说明
            item {
                CheckInInstructions()
            }
        }
    }
}

/**
 * 统计数据卡片
 */
@Composable
private fun StatisticsCard(statistics: CheckInStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "学习统计",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "已坚持天数",
                    value = "${statistics.totalCheckInDays}",
                    icon = Icons.Default.CalendarToday
                )

                StatItem(
                    label = "背单词总数",
                    value = "${statistics.totalQuestions}",
                    icon = Icons.Default.School
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "连续打卡",
                    value = "${statistics.currentStreak}天",
                    icon = Icons.Default.TrendingUp
                )

                StatItem(
                    label = "最长记录",
                    value = "${statistics.longestStreak}天",
                    icon = Icons.Default.Star
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
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8F),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 月份选择器
 */
@Composable
private fun MonthSelector(
    currentYearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "上个月")
        }

        Text(
            text = "${currentYearMonth.year}年${currentYearMonth.monthValue}月",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "下个月")
        }
    }
}

/**
 * 日历网格
 */
@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    checkInRecords: List<CheckInRecord>,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    val daysInMonth = lastDayOfMonth.dayOfMonth

    // 星期标题
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 日期网格
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(280.dp)
    ) {
        // 填充月初空白日期
        val emptyCells = firstDayOfWeek - 1
        repeat(emptyCells) {
            item {
                Box(modifier = Modifier.size(40.dp))
            }
        }

        // 月份日期
        repeat(daysInMonth) { dayIndex ->
            val day = dayIndex + 1
            val date = yearMonth.atDay(day)
            val checkInRecord = checkInRecords.find { it.date == date }
            val isToday = date == LocalDate.now()

            item {
                CalendarDay(
                    day = day,
                    isToday = isToday,
                    checkInRecord = checkInRecord,
                    onClick = { onDateClick(date) }
                )
            }
        }
    }
}

/**
 * 日历日期项
 */
@Composable
private fun CalendarDay(
    day: Int,
    isToday: Boolean,
    checkInRecord: CheckInRecord?,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isToday -> MaterialTheme.colorScheme.primary
        checkInRecord?.isValidCheckIn() == true -> MaterialTheme.colorScheme.secondary
        checkInRecord != null -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }

    val textColor = when {
        isToday -> Color.White
        checkInRecord?.isValidCheckIn() == true -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * 打卡说明
 */
@Composable
private fun CheckInInstructions() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "打卡规则",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "• 每日答题数量达到20题即可打卡",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• 打卡后日历对应日期会显示为蓝色",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• 连续打卡天数越多，学习效果越好",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "• 今日日期会以特殊颜色标记",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
