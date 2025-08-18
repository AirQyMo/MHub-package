package br.pucrio.inf.lac.mobilehub.presentation.ui.main.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.pucrio.inf.lac.mobilehub.R

sealed class NavigationItem(@StringRes val titleId: Int, @DrawableRes val icon: Int, val route: String) {
    object Home : NavigationItem(R.string.action_home, R.drawable.ic_home, "home")
    object ContextData : NavigationItem(R.string.action_context_data, R.drawable.ic_context, "context_data")
    object Settings : NavigationItem(R.string.action_settings, R.drawable.ic_settings, "settings")
}
