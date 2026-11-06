package com.macroflow.automation.ui.macros

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.macroflow.automation.R
import com.macroflow.automation.domain.model.Macro
import com.macroflow.automation.domain.validation.MacroValidator
import com.macroflow.automation.ui.common.AppCard
import com.macroflow.automation.ui.common.EmptyState
import com.macroflow.automation.ui.theme.WarningAmber

@Composable
fun MacrosScreen(viewModel: MacrosViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = stringResource(R.string.error_generic)

    LaunchedEffect(state.hasError) {
        if (state.hasError) {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::openCreate,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.macro_new)) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.macros_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
            )
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.macros_search)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            )
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    state.totalCount == 0 -> EmptyState(
                        icon = Icons.Filled.Add,
                        title = stringResource(R.string.macros_empty_title),
                        body = stringResource(R.string.macros_empty_body),
                        modifier = Modifier.align(Alignment.Center),
                    )
                    state.macros.isEmpty() -> EmptyState(
                        icon = Icons.Filled.Search,
                        title = stringResource(R.string.macros_no_results),
                        body = "",
                        modifier = Modifier.align(Alignment.Center),
                    )
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.macros, key = { it.id }) { macro ->
                            MacroCard(
                                macro = macro,
                                onFavorite = { viewModel.toggleFavorite(macro) },
                                onEdit = { viewModel.openEdit(macro) },
                                onDelete = { viewModel.requestDelete(macro) },
                            )
                        }
                    }
                }
            }
        }
    }

    state.dialog?.let { dialog ->
        EditMacroDialog(
            dialog = dialog,
            onChange = viewModel::onDialogChange,
            onSave = viewModel::save,
            onDismiss = viewModel::dismissDialog,
        )
    }

    state.pendingDelete?.let { macro ->
        AlertDialog(
            onDismissRequest = viewModel::cancelDelete,
            title = { Text(stringResource(R.string.delete_macro_title)) },
            text = { Text(stringResource(R.string.delete_macro_message, macro.name)) },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDelete) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelDelete) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun MacroCard(
    macro: Macro,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = macro.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (macro.description.isNotBlank()) {
                    Text(
                        text = macro.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = stringResource(R.string.macro_actions_count, macro.actionCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (macro.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (macro.isFavorite) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.cd_edit))
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun EditMacroDialog(
    dialog: EditDialogState,
    onChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val nameError = when (dialog.error) {
        MacroValidator.Error.EMPTY_NAME -> stringResource(R.string.error_name_empty)
        MacroValidator.Error.NAME_TOO_LONG -> stringResource(R.string.error_name_long, MacroValidator.MAX_NAME_LENGTH)
        else -> null
    }
    val descriptionError = when (dialog.error) {
        MacroValidator.Error.DESCRIPTION_TOO_LONG ->
            stringResource(R.string.error_desc_long, MacroValidator.MAX_DESCRIPTION_LENGTH)
        else -> null
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(if (dialog.id == null) R.string.macro_new else R.string.macro_edit))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dialog.name,
                    onValueChange = { onChange(it, dialog.description) },
                    label = { Text(stringResource(R.string.macro_name)) },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = dialog.description,
                    onValueChange = { onChange(dialog.name, it) },
                    label = { Text(stringResource(R.string.macro_description)) },
                    minLines = 2,
                    maxLines = 4,
                    isError = descriptionError != null,
                    supportingText = descriptionError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
    )
}
