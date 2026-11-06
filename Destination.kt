package com.macroflow.automation.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.macroflow.automation.R

enum class Destination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home("home", R.string.nav_home, Icons.Filled.Home),
    Macros("macros", R.string.nav_macros, Icons.AutoMirrored.Filled.List),
    Editor("editor", R.string.nav_editor, Icons.Filled.Edit),
    Schedule("schedule", R.string.nav_schedule, Icons.Filled.Schedule),
    Settings("settings", R.string.nav_settings, Icons.Filled.Settings),
}
