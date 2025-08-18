package br.pucrio.inf.lac.mobilehub.presentation.ui.main.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.di.daggerViewModel
import br.pucrio.inf.lac.mobilehub.di.injector
import com.google.accompanist.insets.LocalWindowInsets

@Composable
internal fun HomeScreen() {
    val viewModel: HomeViewModel = daggerViewModel(injector::homeViewModel)
    val state = viewModel.state
    val context = LocalContext.current

    if (!state.isInitialized) LaunchedEffect(Unit) {
        viewModel.loadSettings(context)
    }

    HomeScreenContent(
        state = state,
        onStartMobileHub = viewModel::startMobileHub,
        onStopMobileHub = viewModel::stopMobileHub
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeState,
    onStartMobileHub: () -> Unit = {},
    onStopMobileHub: () -> Unit = {}
) = with(state) {
    val insets = LocalWindowInsets.current
    val systemBarTop = with(LocalDensity.current) { insets.systemBars.top.toDp() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = systemBarTop),
        floatingActionButton = {
            FloatingActionButton(
                onClick = when (state.isMobileHubRunning) {
                    true -> onStopMobileHub
                    false -> onStartMobileHub
                }
            ) {
                val icon = when (state.isMobileHubRunning) {
                    true -> Icons.Filled.Stop
                    false -> Icons.Filled.PlayArrow
                }
                Icon(icon, contentDescription = null)
            }
        }
    ) { padding ->
        DiscoveredDevices(
            modifier = Modifier.padding(padding),
            devices = state.discoveredDevices.values.toList()
        ) 
    }
}

@Composable
private fun DiscoveredDevices(
    devices: List<MobileObject>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(devices) { device ->
            Text(text = device.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val state = HomeState()

    MaterialTheme {
        HomeScreenContent(state = state)
    }
}

