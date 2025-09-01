package com.example.zhouyi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.zhouyi.data.model.Hexagram
import com.example.zhouyi.ui.common.HexagramView

/**
 * 卦象绘制组件（兼容旧用法）
 * 使用优化后的HexagramView组件
 */
@Composable
fun HexagramCanvas(
    hexagram: Hexagram,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onSurface,
    showBackground: Boolean = false,
    showTrigramSeparator: Boolean = false
) {
    // 获取正确的六爻顺序（自上而下）
    val lines = hexagram.getLines()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HexagramView(
            lines = lines,
            color = lineColor,
            lineWidth = 140.dp,
            strokeWidth = 10.dp,
            lineSpacing = 14.dp,
            yinLineGap = 24.dp,
            showBackground = showBackground,
            showTrigramSeparator = showTrigramSeparator,
            cornerRadius = 12.dp,
            elevation = 4.dp
        )
    }
}

/**
 * 小型卦象（用于列表项等）
 */
@Composable
fun SmallHexagramCanvas(
    hexagram: Hexagram,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val lines = hexagram.getLines()

    HexagramView(
        lines = lines,
        modifier = modifier,
        color = lineColor,
        lineWidth = 72.dp,
        strokeWidth = 5.dp,
        lineSpacing = 6.dp,
        yinLineGap = 12.dp,
        showBackground = false,
        showTrigramSeparator = false,
        cornerRadius = 6.dp,
        elevation = 0.dp
    )
}

/**
 * 大型卦象（用于详情页面）
 */
@Composable
fun LargeHexagramCanvas(
    hexagram: Hexagram,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val lines = hexagram.getLines()

    HexagramView(
        lines = lines,
        modifier = modifier,
        color = lineColor,
        lineWidth = 200.dp,
        strokeWidth = 12.dp,
        lineSpacing = 18.dp,
        yinLineGap = 32.dp,
        showBackground = false,
        showTrigramSeparator = false,
        cornerRadius = 16.dp,
        elevation = 8.dp
    )
}
