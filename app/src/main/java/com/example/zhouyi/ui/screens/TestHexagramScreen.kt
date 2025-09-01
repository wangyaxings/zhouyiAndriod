package com.example.zhouyi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zhouyi.ui.common.HexagramView
import com.example.zhouyi.ui.components.HexagramCanvas
import com.example.zhouyi.ui.components.SmallHexagramCanvas
import com.example.zhouyi.ui.components.LargeHexagramCanvas
import com.example.zhouyi.ui.theme.ZhouyiTheme

/**
 * 卦画组件测试页面
 * 展示各种卦象的绘制效果和样式
 */
@Composable
fun TestHexagramScreen() {
    ZhouyiTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "卦画组件测试",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // 1. 基础卦象测试
                Text(
                    text = "基础卦象",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // 乾卦：六阳爻 ☰☰
                val qianLines = listOf(true, true, true, true, true, true)
                HexagramView(
                    lines = qianLines,
                    showTrigramSeparator = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 坤卦：六阴爻 ☷☷
                val kunLines = listOf(false, false, false, false, false, false)
                HexagramView(
                    lines = kunLines,
                    showTrigramSeparator = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 既济卦：水火既济 ☵☲
                val jijiLines = listOf(false, true, false, true, false, true)
                HexagramView(
                    lines = jijiLines,
                    showTrigramSeparator = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. 不同样式测试
                Text(
                    text = "不同样式",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 无背景样式
                    HexagramView(
                        lines = qianLines,
                        showBackground = false,
                        showTrigramSeparator = false,
                        lineWidth = 100.dp,
                        strokeWidth = 6.dp
                    )

                    // 有背景样式
                    HexagramView(
                        lines = kunLines,
                        showBackground = true,
                        showTrigramSeparator = true,
                        lineWidth = 100.dp,
                        strokeWidth = 6.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. 不同尺寸测试
                Text(
                    text = "不同尺寸",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 小尺寸
                    HexagramView(
                        lines = jijiLines,
                        lineWidth = 60.dp,
                        strokeWidth = 4.dp,
                        lineSpacing = 6.dp,
                        yinLineGap = 10.dp,
                        showBackground = false
                    )

                    // 中尺寸
                    HexagramView(
                        lines = jijiLines,
                        lineWidth = 120.dp,
                        strokeWidth = 8.dp,
                        lineSpacing = 10.dp,
                        yinLineGap = 16.dp
                    )

                    // 大尺寸
                    HexagramView(
                        lines = jijiLines,
                        lineWidth = 180.dp,
                        strokeWidth = 12.dp,
                        lineSpacing = 16.dp,
                        yinLineGap = 28.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. 颜色主题测试
                Text(
                    text = "颜色主题",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 默认颜色
                    HexagramView(
                        lines = qianLines,
                        lineWidth = 100.dp,
                        strokeWidth = 6.dp
                    )

                    // 自定义颜色
                    HexagramView(
                        lines = qianLines,
                        color = Color(0xFF8B4513), // 棕色
                        backgroundColor = Color(0xFFF5F5DC), // 米色
                        lineWidth = 100.dp,
                        strokeWidth = 6.dp
                    )

                    // 深色主题
                    HexagramView(
                        lines = qianLines,
                        color = Color(0xFFD4AF37), // 金色
                        backgroundColor = Color(0xFF2F2F2F), // 深灰
                        lineWidth = 100.dp,
                        strokeWidth = 6.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. 特殊卦象测试
                Text(
                    text = "特殊卦象",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // 泰卦：地天泰 ☷☰
                val taiLines = listOf(false, false, false, true, true, true)
                HexagramView(
                    lines = taiLines,
                    showTrigramSeparator = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 否卦：天地否 ☰☷
                val piLines = listOf(true, true, true, false, false, false)
                HexagramView(
                    lines = piLines,
                    showTrigramSeparator = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 未济卦：火水未济 ☲☵
                val weijiLines = listOf(true, false, true, false, true, false)
                HexagramView(
                    lines = weijiLines,
                    showTrigramSeparator = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6. 说明文字
                Text(
                    text = "卦画绘制说明",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Text(
                    text = "• 六爻自下而上排列（第一爻在最下方）\n" +
                           "• 阳爻：完整的横线\n" +
                           "• 阴爻：中断的横线，左右两段\n" +
                           "• 上卦与下卦之间有细微分隔线\n" +
                           "• 支持自定义颜色、尺寸、背景等样式",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
