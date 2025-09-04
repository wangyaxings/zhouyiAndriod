package com.example.zhouyi.ui.screens.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zhouyi.data.model.CheckInRecord
import com.example.zhouyi.data.model.CheckInStatistics
import com.example.zhouyi.data.repository.CheckInRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * 打卡界面ViewModel
 */
class CheckInViewModel(
    private val checkInRepository: CheckInRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    init {
        loadCheckInData()
    }

    /**
     * 加载打卡数据
     */
    fun loadCheckInData() {
        viewModelScope.launch {
            try {
                val statistics = checkInRepository.getCheckInStatistics()
                val monthCheckIns = checkInRepository.getCheckInsByMonth(
                    _uiState.value.currentYearMonth.year,
                    _uiState.value.currentYearMonth.monthValue
                ).first()

                _uiState.value = _uiState.value.copy(
                    statistics = statistics,
                    monthCheckIns = monthCheckIns,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "加载打卡数据失败",
                    isLoading = false
                )
            }
        }
    }

    /**
     * 切换到上个月
     */
    fun previousMonth() {
        val currentYearMonth = _uiState.value.currentYearMonth
        val newYearMonth = currentYearMonth.minusMonths(1)

        _uiState.value = _uiState.value.copy(
            currentYearMonth = newYearMonth
        )

        loadMonthCheckIns(newYearMonth)
    }

    /**
     * 切换到下个月
     */
    fun nextMonth() {
        val currentYearMonth = _uiState.value.currentYearMonth
        val newYearMonth = currentYearMonth.plusMonths(1)

        _uiState.value = _uiState.value.copy(
            currentYearMonth = newYearMonth
        )

        loadMonthCheckIns(newYearMonth)
    }

    /**
     * 加载指定月份的打卡记录
     */
    private fun loadMonthCheckIns(yearMonth: YearMonth) {
        viewModelScope.launch {
            try {
                val monthCheckIns = checkInRepository.getCheckInsByMonth(
                    yearMonth.year,
                    yearMonth.monthValue
                ).first()

                _uiState.value = _uiState.value.copy(
                    monthCheckIns = monthCheckIns
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "加载月份数据失败"
                )
            }
        }
    }

    /**
     * 点击日期
     */
    fun onDateClick(date: LocalDate) {
        viewModelScope.launch {
            try {
                val checkInRecord = checkInRepository.getCheckInByDate(date)
                _uiState.value = _uiState.value.copy(
                    selectedDate = date,
                    selectedCheckIn = checkInRecord
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "获取日期信息失败"
                )
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 打卡界面状态
 */
data class CheckInUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val statistics: CheckInStatistics = CheckInStatistics.empty(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val monthCheckIns: List<CheckInRecord> = emptyList(),
    val selectedDate: LocalDate? = null,
    val selectedCheckIn: CheckInRecord? = null
)
