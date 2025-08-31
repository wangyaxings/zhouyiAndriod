package com.example.zhouyi.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.zhouyi.ui.theme.ZhouyiTheme

/**
 * A reusable composable for rendering a hexagram with precise UI/UX control.
 *
 * - lines 顺序：自下而上（index 0 -> 5）
 * - 阳爻（true）：使用“总宽度 x 总高度”的矩形色块表示（直角，非圆角）
 * - 阴爻（false）：与阳爻同样总宽度和高度，但中间留空，左右为两个等长矩形段（直角）
 */
@Composable
fun HexagramView(
    lines: List<Boolean>,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    lineWidth: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    lineSpacing: Dp = 18.dp,
    yinLineGap: Dp = 22.dp
) {
    require(lines.size == 6) { "A hexagram must consist of exactly 6 lines." }

    val totalHeight = (strokeWidth * 6) + (lineSpacing * 5)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(width = lineWidth, height = totalHeight)) {
            val strokePx = strokeWidth.toPx()
            val widthPx = lineWidth.toPx()
            val spacingPx = lineSpacing.toPx()
            val gapPx = yinLineGap.toPx()

            lines.forEachIndexed { index, isYang ->
                // 按自下而上绘制；Canvas 原点在顶部
                val reversedIndex = 5 - index
                val yTop = reversedIndex * (strokePx + spacingPx)

                if (isYang) {
                    // 阳爻：整块矩形（直角）
                    drawRect(
                        color = color,
                        topLeft = Offset(0f, yTop),
                        size = androidx.compose.ui.geometry.Size(widthPx, strokePx)
                    )
                } else {
                    // 阴爻：两段矩形（直角），中间留白
                    val segment = (widthPx - gapPx) / 2f
                    // 左段
                    drawRect(
                        color = color,
                        topLeft = Offset(0f, yTop),
                        size = androidx.compose.ui.geometry.Size(segment, strokePx)
                    )
                    // 右段
                    drawRect(
                        color = color,
                        topLeft = Offset(segment + gapPx, yTop),
                        size = androidx.compose.ui.geometry.Size(widthPx - (segment + gapPx), strokePx)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HexagramViewPreview() {
    ZhouyiTheme {
        // Example: from bottom to top
        val example = listOf(true, false, true, true, false, true)
        HexagramView(
            lines = example,
            color = Color(0xFF2E2B27),
            lineWidth = 140.dp,
            strokeWidth = 12.dp,
            lineSpacing = 18.dp,
            yinLineGap = 24.dp
        )
    }
}
