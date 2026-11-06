package com.macroflow.automation.ui.macros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macroflow.automation.data.repository.MacroRepository
import com.macroflow.automation.domain.model.Macro
import com.macroflow.automation.domain.usecase.SaveMacroUseCase
import com.macroflow.automation.domain.validation.MacroValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditDialogState(
    val id: Long?,
    val name: String,
    val description: String,
    val error: MacroValidator.Error? = null,
)

data class MacrosUiState(
    val isLoading: Boolean = true,
    val macros: List<Macro> = emptyList(),
    val totalCount: Int = 0,
    val query: String = "",
    val dialog: EditDialogState? = null,
    val pendingDelete: Macro? = null,
    val hasError: Boolean = false,
)

private data class LocalState(
    val query: String = "",
    val dialog: EditDialogState? = null,
    val pendingDelete: Macro? = null,
    val hasError: Boolean = false,
)

@HiltViewModel
class MacrosViewModel @Inject constructor(
    private val repository: MacroRepository,
    private val saveMacro: SaveMacroUseCase,
) : ViewModel() {

    private val local = MutableStateFlow(LocalState())

    val uiState: StateFlow<MacrosUiState> = combine(repository.observeMacros(), local) { macros, l ->
        val q = l.query.trim()
        val visible = if (q.isEmpty()) {
            macros
        } else {
            macros.filter { it.name.contains(q, ignoreCase = true) || it.description.contains(q, ignoreCase = true) }
        }
        MacrosUiState(
            isLoading = false,
            macros = visible,
            totalCount = macros.size,
            query = l.query,
            dialog = l.dialog,
            pendingDelete = l.pendingDelete,
            hasError = l.hasError,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MacrosUiState())

    fun onQueryChange(query: String) = local.update { it.copy(query = query) }

    fun openCreate() = local.update { it.copy(dialog = EditDialogState(id = null, name = "", description = "")) }

    fun openEdit(macro: Macro) = local.update {
        it.copy(dialog = EditDialogState(id = macro.id, name = macro.name, description = macro.description))
    }

    fun onDialogChange(name: String, description: String) = local.update {
        it.copy(dialog = it.dialog?.copy(name = name, description = description, error = null))
    }

    fun dismissDialog() = local.update { it.copy(dialog = null) }

    fun save() {
        val dialog = local.value.dialog ?: return
        viewModelScope.launch {
            try {
                val error = saveMacro(dialog.id, dialog.name, dialog.description)
                if (error != null) {
                    local.update { it.copy(dialog = it.dialog?.copy(error = error)) }
                } else {
                    local.update { it.copy(dialog = null) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                local.update { it.copy(hasError = true) }
            }
        }
    }

    fun toggleFavorite(macro: Macro) {
        viewModelScope.launch {
            try {
                repository.setFavorite(macro.id, !macro.isFavorite)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                local.update { it.copy(hasError = true) }
            }
        }
    }

    fun requestDelete(macro: Macro) = local.update { it.copy(pendingDelete = macro) }

    fun cancelDelete() = local.update { it.copy(pendingDelete = null) }

    fun confirmDelete() {
        val macro = local.value.pendingDelete ?: return
        local.update { it.copy(pendingDelete = null) }
        viewModelScope.launch {
            try {
                repository.delete(macro.id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                local.update { it.copy(hasError = true) }
            }
        }
    }

    fun clearError() = local.update { it.copy(hasError = false) }
}
