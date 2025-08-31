package com.example.zhouyi.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 答题记录数据模型
 * 对应式样书中的 attempt 表
 */
@Entity(
    tableName = "attempts",
    foreignKeys = [
        ForeignKey(
            entity = Hexagram::class,
            parentColumns = ["id"],
            childColumns = ["hexagramId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Attempt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hexagramId: Int, // 关联的卦象ID
    val timestamp: Long, // 答题时间戳
    val isCorrect: Boolean, // 是否答对
    val selectedOption: Int, // 用户选择的选项索引 (0-4)
    val correctOption: Int, // 正确答案的选项索引 (0-4)
    val options: List<Int>, // 选项中的卦象ID列表，JSON格式存储
    val mode: String = "practice" // 答题模式：practice, timed, exam
) {
    /**
     * 获取答题用时（毫秒）
     */
    fun getDuration(): Long {
        // 这里可以扩展为记录开始时间和结束时间
        return 0L
    }

    /**
     * 获取选项位置（A-E）
     */
    fun getSelectedOptionLabel(): String {
        return ('A' + selectedOption).toString()
    }

    /**
     * 获取正确答案位置（A-E）
     */
    fun getCorrectOptionLabel(): String {
        return ('A' + correctOption).toString()
    }
}
