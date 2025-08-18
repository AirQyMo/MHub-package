package br.pucrio.inf.lac.mobilehub.presentation.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.contextdata.ContextDataScreen
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.home.HomeScreen
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings.SettingsScreen

@Composable
fun NavigationGraph(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Home.route, modifier = modifier) {
        composable(NavigationItem.Home.route) { HomeScreen() }
        composable(NavigationItem.ContextData.route) { ContextDataScreen() }
        composable(NavigationItem.Settings.route) { SettingsScreen() }
    }
}
