package com.example.zhouyi.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.zhouyi.data.model.Hexagram

/**
 * 卦象绘制组件
 * 使用Canvas绘制六爻
 */
@Composable
fun HexagramCanvas(
    hexagram: Hexagram,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onSurface,
    lineWidth: Float = 8f,
    lineSpacing: Float = 20f,
    showNumber: Boolean = false
) {
    val lines = hexagram.getLines()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showNumber) {
            Text(
                text = hexagram.id.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Canvas(
            modifier = Modifier
                .width(120.dp)
                .height((lines.size * (lineWidth + lineSpacing) + lineSpacing).dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val lineHeight = (canvasHeight - lineSpacing * (lines.size + 1)) / lines.size

            lines.forEachIndexed { index, isYang ->
                val y = lineSpacing + index * (lineHeight + lineSpacing) + lineHeight / 2

                if (isYang) {
                    // 阳爻：实线
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = lineWidth,
                        cap = StrokeCap.Round
                    )
                } else {
                    // 阴爻：断线（两段）
                    val segmentWidth = canvasWidth / 3
                    val gapWidth = segmentWidth / 4

                    // 第一段
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(segmentWidth - gapWidth / 2, y),
                        strokeWidth = lineWidth,
                        cap = StrokeCap.Round
                    )

                    // 第二段
                    drawLine(
                        color = lineColor,
                        start = Offset(segmentWidth + gapWidth / 2, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = lineWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

/**
 * 小型卦象绘制组件
 * 用于列表项等小尺寸显示
 */
@Composable
fun SmallHexagramCanvas(
    hexagram: Hexagram,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onSurface,
    lineWidth: Float = 4f,
    lineSpacing: Float = 8f
) {
    val lines = hexagram.getLines()

    Canvas(
        modifier = modifier
            .width(60.dp)
            .height((lines.size * (lineWidth + lineSpacing) + lineSpacing).dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val lineHeight = (canvasHeight - lineSpacing * (lines.size + 1)) / lines.size

        lines.forEachIndexed { index, isYang ->
            val y = lineSpacing + index * (lineHeight + lineSpacing) + lineHeight / 2

            if (isYang) {
                // 阳爻：实线
                drawLine(
                    color = lineColor,
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            } else {
                // 阴爻：断线（两段）
                val segmentWidth = canvasWidth / 3
                val gapWidth = segmentWidth / 4

                // 第一段
                drawLine(
                    color = lineColor,
                    start = Offset(0f, y),
                    end = Offset(segmentWidth - gapWidth / 2, y),
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )

                // 第二段
                drawLine(
                    color = lineColor,
                    start = Offset(segmentWidth + gapWidth / 2, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
