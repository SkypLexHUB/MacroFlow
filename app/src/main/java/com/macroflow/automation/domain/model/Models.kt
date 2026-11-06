package com.macroflow.automation.domain.model

data class Macro(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val lastRunAt: Long? = null,
    val actionCount: Int = 0,
)

enum class ExecutionStatus {
    SUCCESS, FAILED, STOPPED;

    companion object {
        fun fromName(name: String?): ExecutionStatus =
            values().firstOrNull { it.name == name } ?: FAILED
    }
}

data class ExecutionRecord(
    val id: Long,
    val macroId: Long?,
    val macroName: String,
    val startedAt: Long,
    val durationMs: Long,
    val actionCount: Int,
    val status: ExecutionStatus,
    val failureReason: String?,
)

enum class ThemeMode {
    DARK, LIGHT, SYSTEM;

    companion object {
        fun fromName(name: String?): ThemeMode =
            values().firstOrNull { it.name == name } ?: DARK
    }
}
