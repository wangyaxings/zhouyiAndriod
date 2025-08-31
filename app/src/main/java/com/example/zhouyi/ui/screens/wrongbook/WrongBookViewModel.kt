package com.example.zhouyi.ui.screens.wrongbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zhouyi.data.model.Hexagram
import com.example.zhouyi.data.repository.HexagramRepository
import com.example.zhouyi.data.repository.WrongBookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * 错题本页面ViewModel
 * 管理错题数据和操作
 */
class WrongBookViewModel(
    private val wrongBookRepository: WrongBookRepository,
    private val hexagramRepository: HexagramRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WrongBookUiState())
    val uiState: StateFlow<WrongBookUiState> = _uiState.asStateFlow()
    
    /**
     * 加载错题本数据
     */
    fun loadWrongBook() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 获取错题列表
                val wrongBooks = wrongBookRepository.getAllWrongBooks()
                val hexagrams = hexagramRepository.getAllHexagrams().first()
                
                // 组合错题数据
                val wrongItems = wrongBooks.map { wrongBook ->
                    val hexagram = hexagrams.find { it.id == wrongBook.hexagramId }
                    if (hexagram != null) {
                        WrongBookItem(
                            hexagram = hexagram,
                            wrongCount = wrongBook.wrongCount,
                            lastWrongTimestamp = wrongBook.lastWrongTimestamp,
                            firstWrongTimestamp = wrongBook.firstWrongTimestamp
                        )
                    } else null
                }.filterNotNull()
                
                // 计算统计信息
                val stats = calculateStats(wrongItems)
                
                _uiState.value = _uiState.value.copy(
                    wrongItems = wrongItems,
                    stats = stats,
                    isLoading = false
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "加载错题本失败",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * 计算统计信息
     */
    private fun calculateStats(wrongItems: List<WrongBookItem>): WrongBookStats {
        val totalCount = wrongItems.size
        val highFrequencyCount = wrongItems.count { it.wrongCount >= 3 }
        val recentCount = wrongItems.count { 
            val daysSinceLastWrong = it.getDaysSinceLastWrong()
            daysSinceLastWrong <= 7 // 最近7天内的错题
        }
        
        return WrongBookStats(
            totalCount = totalCount,
            highFrequencyCount = highFrequencyCount,
            recentCount = recentCount
        )
    }
    
    /**
     * 选择卦象
     */
    fun selectHexagram(hexagram: Hexagram) {
        // 这里可以添加选择卦象的逻辑，比如跳转到练习页面
        _uiState.value = _uiState.value.copy(
            selectedHexagram = hexagram
        )
    }
    
    /**
     * 从错题本中移除
     */
    fun removeFromWrongBook(hexagramId: Int) {
        viewModelScope.launch {
            try {
                wrongBookRepository.deleteWrongBook(hexagramId)
                
                // 重新加载数据
                loadWrongBook()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "移除失败"
                )
            }
        }
    }
    
    /**
     * 清空错题本
     */
    fun clearWrongBook() {
        viewModelScope.launch {
            try {
                wrongBookRepository.clearAllWrongBooks()
                
                _uiState.value = _uiState.value.copy(
                    wrongItems = emptyList(),
                    stats = WrongBookStats(0, 0, 0)
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "清空失败"
                )
            }
        }
    }
    
    /**
     * 刷新数据
     */
    fun refreshData() {
        loadWrongBook()
    }
    
    /**
     * 清除错误状态
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 错题本UI状态
 */
data class WrongBookUiState(
    val wrongItems: List<WrongBookItem> = emptyList(),
    val stats: WrongBookStats = WrongBookStats(),
    val selectedHexagram: Hexagram? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 错题项
 */
data class WrongBookItem(
    val hexagram: Hexagram,
    val wrongCount: Int,
    val lastWrongTimestamp: Long,
    val firstWrongTimestamp: Long
) {
    /**
     * 获取距离上次错题的天数
     */
    fun getDaysSinceLastWrong(): Int {
        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - lastWrongTimestamp
        return (timeDiff / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * 获取距离首次错题的天数
     */
    fun getDaysSinceFirstWrong(): Int {
        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - firstWrongTimestamp
        return (timeDiff / (1000 * 60 * 60 * 24)).toInt()
    }
    
    /**
     * 获取最后错题时间的文本描述
     */
    fun getLastWrongDaysText(): String {
        val days = getDaysSinceLastWrong()
        return when {
            days == 0 -> "今天"
            days == 1 -> "昨天"
            days < 7 -> "${days}天前"
            days < 30 -> "${days / 7}周前"
            else -> "${days / 30}个月前"
        }
    }
    
    /**
     * 获取首次错题时间的文本描述
     */
    fun getFirstWrongDaysText(): String {
        val days = getDaysSinceFirstWrong()
        return when {
            days == 0 -> "今天"
            days == 1 -> "昨天"
            days < 7 -> "${days}天前"
            days < 30 -> "${days / 7}周前"
            else -> "${days / 30}个月前"
        }
    }
}

/**
 * 错题本统计信息
 */
data class WrongBookStats(
    val totalCount: Int = 0,
    val highFrequencyCount: Int = 0,
    val recentCount: Int = 0
)
