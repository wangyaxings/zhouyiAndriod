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
 * 现内部委托给 HexagramView，统一绘制规范。
 */
@Composable
fun HexagramCanvas(
    hexagram: Hexagram,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onSurface,
    lineWidth: Float = 12f, // 视作 dp 粗细
    lineSpacing: Float = 18f // 视作 dp 行距
) {
    val linesTopToBottom = hexagram.getLines()
    val linesBottomToTop = linesTopToBottom.reversed()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HexagramView(
            lines = linesBottomToTop,
            color = lineColor,
            lineWidth = 120.dp,
            strokeWidth = lineWidth.dp,
            lineSpacing = lineSpacing.dp,
            yinLineGap = (120.dp * 0.18f)
        )
    }
}

/**
 * 小型卦象（用于列表项等小尺寸显示）
 */
@Composable
fun SmallHexagramCanvas(
    hexagram: Hexagram,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onSurface,
    lineWidth: Float = 6f,
    lineSpacing: Float = 10f
) {
    val linesTopToBottom = hexagram.getLines()
    val linesBottomToTop = linesTopToBottom.reversed()

    HexagramView(
        lines = linesBottomToTop,
        modifier = modifier,
        color = lineColor,
        lineWidth = 60.dp,
        strokeWidth = lineWidth.dp,
        lineSpacing = lineSpacing.dp,
        yinLineGap = (60.dp * 0.16f)
    )
}
