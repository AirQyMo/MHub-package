package br.pucrio.inf.lac.mobilehub.presentation.ui.main.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.ContextData,
        NavigationItem.Settings
    )

    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                alwaysShowLabel = true,
                icon = { NavigationIcon(item = item) },
                label = { Text(text = stringResource(id = item.titleId)) },
                selected = currentRoute == item.route,
                onClick = { navController.route(item.route) }
            )
        }
    }
}

@Composable
private fun NavigationIcon(modifier: Modifier = Modifier, item: NavigationItem) {
    Icon(
        modifier = modifier.size(24.dp),
        painter = painterResource(id = item.icon),
        contentDescription = stringResource(id = item.titleId)
    )
}

private fun NavController.route(route: String) {
    navigate(route) {
        graph.startDestinationRoute?.let { route ->
            popUpTo(route) {
                saveState = true
            }
        }

        launchSingleTop = true
        restoreState = true
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        BottomNavigationBar(navController)
    }
}
