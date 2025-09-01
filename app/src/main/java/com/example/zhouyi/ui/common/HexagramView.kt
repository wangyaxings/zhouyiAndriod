package com.example.zhouyi.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.zhouyi.ui.theme.ZhouyiTheme

/**
 * 专业卦画绘制组件
 *
 * 标准卦画规范：
 * - 六爻自下而上排列（第一爻在最下方）
 * - 阳爻：完整的横线
 * - 阴爻：中断的横线，左右两段
 * - 视觉层次：上卦（外卦）与下卦（内卦）有细微分隔
 */
@Composable
fun HexagramView(
    lines: List<Boolean>,
    modifier: Modifier = Modifier,
    // 视觉设计参数
    color: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    lineWidth: Dp = 140.dp,
    strokeWidth: Dp = 8.dp,
    lineSpacing: Dp = 12.dp,
    yinLineGap: Dp = 20.dp,
    // 新增设计参数
    showBackground: Boolean = false,
    showTrigramSeparator: Boolean = false,
    cornerRadius: Dp = 8.dp,
    elevation: Dp = 2.dp
) {
    require(lines.size == 6) { "卦象必须包含6爻" }

    val totalHeight = (strokeWidth * 6) + (lineSpacing * 5) +
                     if (showTrigramSeparator) lineSpacing / 2 else 0.dp

    if (showBackground) {
        Card(
            modifier = modifier.shadow(elevation, RoundedCornerShape(cornerRadius)),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(cornerRadius)
        ) {
            HexagramCanvas(
                lines = lines,
                color = color,
                lineWidth = lineWidth,
                strokeWidth = strokeWidth,
                lineSpacing = lineSpacing,
                yinLineGap = yinLineGap,
                showTrigramSeparator = showTrigramSeparator,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        HexagramCanvas(
            lines = lines,
            color = color,
            lineWidth = lineWidth,
            strokeWidth = strokeWidth,
            lineSpacing = lineSpacing,
            yinLineGap = yinLineGap,
            showTrigramSeparator = showTrigramSeparator,
            modifier = modifier
        )
    }
}

@Composable
private fun HexagramCanvas(
    lines: List<Boolean>,
    color: Color,
    lineWidth: Dp,
    strokeWidth: Dp,
    lineSpacing: Dp,
    yinLineGap: Dp,
    showTrigramSeparator: Boolean,
    modifier: Modifier = Modifier
) {
    val totalHeight = (strokeWidth * 6) + (lineSpacing * 5) +
                     if (showTrigramSeparator) lineSpacing / 2 else 0.dp

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(width = lineWidth, height = totalHeight)
        ) {
            drawHexagram(
                lines = lines,
                color = color,
                strokeWidth = strokeWidth.toPx(),
                lineSpacing = lineSpacing.toPx(),
                yinLineGap = yinLineGap.toPx(),
                showTrigramSeparator = showTrigramSeparator,
                canvasWidth = lineWidth.toPx()
            )
        }
    }
}

/**
 * 绘制卦象的核心逻辑
 * 严格按照传统卦画规范：自下而上绘制六爻
 */
private fun DrawScope.drawHexagram(
    lines: List<Boolean>,
    color: Color,
    strokeWidth: Float,
    lineSpacing: Float,
    yinLineGap: Float,
    showTrigramSeparator: Boolean,
    canvasWidth: Float
) {
    val separatorGap = if (showTrigramSeparator) lineSpacing / 2 else 0f

    // 正确绘制顺序：lines 按自上而下提供，这里按自上而下绘制
    lines.forEachIndexed { index, isYang ->
        // 在第三、四爻之间增加额外间隔，使上下卦分隔更明显
        val extraGap = if (showTrigramSeparator && index >= 3) separatorGap else 0f
        val yTop = index * (strokeWidth + lineSpacing) + extraGap

        if (isYang) {
            drawRoundRect(
                color = color,
                topLeft = Offset(0f, yTop),
                size = Size(canvasWidth, strokeWidth),
                cornerRadius = CornerRadius(strokeWidth * 0.1f)
            )
        } else {
            val segmentWidth = (canvasWidth - yinLineGap) / 2f

            // 左段
            drawRoundRect(
                color = color,
                topLeft = Offset(0f, yTop),
                size = Size(segmentWidth, strokeWidth),
                cornerRadius = CornerRadius(strokeWidth * 0.1f)
            )

            // 右段
            drawRoundRect(
                color = color,
                topLeft = Offset(segmentWidth + yinLineGap, yTop),
                size = Size(segmentWidth, strokeWidth),
                cornerRadius = CornerRadius(strokeWidth * 0.1f)
            )
        }
    }

    // 可选：绘制上下卦分隔线（极细的装饰线），位于第三、四爻之间
    if (showTrigramSeparator) {
        val separatorY = 3 * (strokeWidth + lineSpacing) + separatorGap / 2
        val separatorAlpha = 0.3f

        drawLine(
            color = color.copy(alpha = separatorAlpha),
            start = Offset(canvasWidth * 0.2f, separatorY),
            end = Offset(canvasWidth * 0.8f, separatorY),
            strokeWidth = 1.dp.toPx()
        )
    }
}

// 测试和预览组件
@Preview(showBackground = true, backgroundColor = 0xFFFFFBFE)
@Composable
private fun HexagramPreview() {
    ZhouyiTheme {
        Column {
            // 测试几个经典卦象

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
        }
    }
}
