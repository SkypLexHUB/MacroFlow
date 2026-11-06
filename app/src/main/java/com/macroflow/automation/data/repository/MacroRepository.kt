package com.macroflow.automation.data.repository

import com.macroflow.automation.data.local.dao.MacroDao
import com.macroflow.automation.data.local.dao.MacroWithCount
import com.macroflow.automation.data.local.entity.MacroEntity
import com.macroflow.automation.domain.model.Macro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MacroRepository @Inject constructor(
    private val dao: MacroDao,
) {
    fun observeMacros(): Flow<List<Macro>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    suspend fun create(name: String, description: String): Long {
        val now = System.currentTimeMillis()
        return dao.insert(
            MacroEntity(
                name = name,
                description = description,
                isFavorite = false,
                createdAt = now,
                updatedAt = now,
                lastRunAt = null,
            ),
        )
    }

    suspend fun updateDetails(id: Long, name: String, description: String) {
        val existing = dao.getById(id) ?: return
        dao.update(existing.copy(name = name, description = description, updatedAt = System.currentTimeMillis()))
    }

    suspend fun setFavorite(id: Long, favorite: Boolean) {
        dao.setFavorite(id, favorite, System.currentTimeMillis())
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }
}

private fun MacroWithCount.toDomain() = Macro(
    id = macro.id,
    name = macro.name,
    description = macro.description,
    isFavorite = macro.isFavorite,
    createdAt = macro.createdAt,
    updatedAt = macro.updatedAt,
    lastRunAt = macro.lastRunAt,
    actionCount = actionCount,
)
