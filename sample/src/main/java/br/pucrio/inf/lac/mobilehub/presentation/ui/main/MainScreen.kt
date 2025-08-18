package br.pucrio.inf.lac.mobilehub.presentation.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import br.pucrio.inf.lac.mobilehub.R
import br.pucrio.inf.lac.mobilehub.presentation.design.Elevation
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.navigation.BottomNavigationBar
import br.pucrio.inf.lac.mobilehub.presentation.ui.main.navigation.NavigationGraph
import com.google.accompanist.insets.navigationBarsWithImePadding

@Composable
fun MainScreen(navController: NavHostController) {
    val bottomBarNavController = rememberNavController()

    Scaffold(
        modifier = Modifier.navigationBarsWithImePadding(),
        bottomBar = { BottomNavigationBar(navController = bottomBarNavController) },
        topBar = {
            MainToolbar(
                title = stringResource(id = R.string.text_home_navigation_title),
                backHint = stringResource(id = R.string.text_main_navigation_back_hint),
                onBackClick = navController::navigateUp
            )
        }
    ) { innerPadding ->
        NavigationGraph(modifier = Modifier.padding(innerPadding), navController = bottomBarNavController)
    }
}

@Composable
private fun MainToolbar(
    title: String,
    backHint: String,
    onBackClick: () -> Unit = {}
) = TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, backHint)
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        elevation = Elevation.Large
    )

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        MainScreen(navController)
    }
}
