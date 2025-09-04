package com.example.zhouyi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zhouyi.data.model.HexagramSequenceSong

/**
 * 卦序歌显示组件
 * 显示完整的卦序歌，每个字放在方格内，支持高亮当前卦名
 */
@Composable
fun HexagramSequenceSongView(
    currentHexagramId: Int? = null,
    showAnswer: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 上经卦序歌
        HexagramSequenceLines(
            lines = HexagramSequenceSong.UPPER_SEQUENCE_LINES,
            currentHexagramId = currentHexagramId,
            showAnswer = showAnswer
        )

        // 下经卦序歌
        HexagramSequenceLines(
            lines = HexagramSequenceSong.LOWER_SEQUENCE_LINES,
            currentHexagramId = currentHexagramId,
            showAnswer = showAnswer
        )
    }
}

/**
 * 卦序歌行显示组件
 * 每行七字，每个字放在方格内
 */
@Composable
private fun HexagramSequenceLines(
    lines: List<String>,
    currentHexagramId: Int?,
    showAnswer: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        lines.forEach { line ->
            HexagramSequenceLine(
                line = line,
                currentHexagramId = currentHexagramId,
                showAnswer = showAnswer
            )
        }
    }
}

/**
 * 单行卦序歌显示组件
 * 将每行的七个字分别放在方格内
 */
@Composable
private fun HexagramSequenceLine(
    line: String,
    currentHexagramId: Int?,
    showAnswer: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        line.forEach { char ->
            val isHighlighted = showAnswer && currentHexagramId != null &&
                HexagramSequenceSong.getHexagramName(currentHexagramId)?.contains(char) == true

            HexagramCharBox(
                char = char,
                isHighlighted = isHighlighted
            )
        }
    }
}

/**
 * 单个字符方格组件
 */
@Composable
private fun HexagramCharBox(
    char: Char,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .border(
                width = 0.5.dp,
                color = if (isHighlighted) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }
            )
            .background(
                color = if (isHighlighted) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Transparent
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isHighlighted) {
                MaterialTheme.colorScheme.onError
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
            textAlign = TextAlign.Center
        )
    }
}

