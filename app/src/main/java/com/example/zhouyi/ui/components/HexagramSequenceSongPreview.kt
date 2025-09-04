package com.example.zhouyi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zhouyi.ui.theme.ZhouyiTheme

/**
 * 卦序歌预览组件
 * 用于测试和展示卦序歌显示效果
 */
@Preview(showBackground = true)
@Composable
fun HexagramSequenceSongPreview() {
    ZhouyiTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 测试不同的卦
            val testHexagrams = listOf(1, 3, 9, 15, 30, 35, 50, 64)

            testHexagrams.forEach { hexagramId ->
                HexagramSequenceSongView(
                    currentHexagramId = hexagramId,
                    showAnswer = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 默认状态卦序歌预览（不显示答案）
 */
@Preview(showBackground = true)
@Composable
fun HexagramSequenceSongDefaultPreview() {
    ZhouyiTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 测试不同的卦
            val testHexagrams = listOf(1, 3, 9, 15, 30, 35, 50, 64)

            testHexagrams.forEach { hexagramId ->
                HexagramSequenceSongView(
                    currentHexagramId = hexagramId,
                    showAnswer = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
