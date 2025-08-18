package br.pucrio.inf.lac.mobilehub.presentation.ui.navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
}
