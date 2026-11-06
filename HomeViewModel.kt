package com.macroflow.automation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macroflow.automation.data.repository.HistoryRepository
import com.macroflow.automation.data.repository.MacroRepository
import com.macroflow.automation.domain.model.ExecutionRecord
import com.macroflow.automation.domain.model.ExecutionStatus
import com.macroflow.automation.domain.model.Macro
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val totalMacros: Int = 0,
    val totalRuns: Int = 0,
    val successRuns: Int = 0,
    val failedRuns: Int = 0,
    val recent: List<ExecutionRecord> = emptyList(),
    val favorites: List<Macro> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    macroRepository: MacroRepository,
    historyRepository: HistoryRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        macroRepository.observeMacros(),
        historyRepository.observeTotal(),
        historyRepository.observeCount(ExecutionStatus.SUCCESS),
        historyRepository.observeCount(ExecutionStatus.FAILED),
        historyRepository.observeRecent(5),
    ) { macros, total, success, failed, recent ->
        HomeUiState(
            isLoading = false,
            totalMacros = macros.size,
            totalRuns = total,
            successRuns = success,
            failedRuns = failed,
            recent = recent,
            favorites = macros.filter { it.isFavorite },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
