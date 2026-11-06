package com.macroflow.automation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.macroflow.automation.R
import com.macroflow.automation.ui.common.EmptyState
import com.macroflow.automation.ui.home.HomeScreen
import com.macroflow.automation.ui.macros.MacrosScreen
import com.macroflow.automation.ui.navigation.Destination
import com.macroflow.automation.ui.settings.SettingsScreen

@Composable
fun MacroFlowRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                Destination.values().forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = { navController.navigateToTab(destination.route) },
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(stringResource(destination.labelRes)) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Home.route) {
                HomeScreen(onManageMacros = { navController.navigateToTab(Destination.Macros.route) })
            }
            composable(Destination.Macros.route) { MacrosScreen() }
            composable(Destination.Editor.route) {
                EmptyState(
                    icon = Icons.Filled.Edit,
                    title = stringResource(R.string.coming_soon),
                    body = stringResource(R.string.editor_body),
                )
            }
            composable(Destination.Schedule.route) {
                EmptyState(
                    icon = Icons.Filled.Schedule,
                    title = stringResource(R.string.coming_soon),
                    body = stringResource(R.string.schedule_body),
                )
            }
            composable(Destination.Settings.route) { SettingsScreen() }
        }
    }
}

private fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
