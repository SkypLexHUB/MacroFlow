package com.macroflow.automation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.macroflow.automation.data.local.entity.MacroActionEntity

@Dao
interface MacroActionDao {
    @Query("SELECT * FROM macro_actions WHERE macroId = :macroId ORDER BY position ASC")
    suspend fun getForMacro(macroId: Long): List<MacroActionEntity>

    @Insert
    suspend fun insertAll(actions: List<MacroActionEntity>)

    @Query("DELETE FROM macro_actions WHERE macroId = :macroId")
    suspend fun deleteForMacro(macroId: Long)

    /** Atomically replaces all actions of a macro. */
    @Transaction
    suspend fun replaceForMacro(macroId: Long, actions: List<MacroActionEntity>) {
        deleteForMacro(macroId)
        insertAll(actions)
    }
}
