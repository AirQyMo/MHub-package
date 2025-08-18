package br.pucrio.inf.lac.mobilehub.presentation.ui.main.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.pucrio.inf.lac.mobilehub.R
import br.pucrio.inf.lac.mobilehub.di.daggerViewModel
import br.pucrio.inf.lac.mobilehub.di.injector
import br.pucrio.inf.lac.mobilehub.presentation.design.Color
import br.pucrio.inf.lac.mobilehub.presentation.design.Spacing

@Composable
internal fun SettingsScreen() {
    val viewModel: SettingsViewModel = daggerViewModel(injector::settingsViewModel)
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    SettingsScreenContent(
        state = state,
        onWpanIndexChange = viewModel::updateWpanIndex,
        onIpAddressChange = viewModel::updateIpAddress,
        onPortChange = viewModel::updatePort,
        onRestart = { viewModel.restart(context) }
    )
}

@Composable
private fun SettingsScreenContent(
    state: SettingsState,
    onWpanIndexChange: (Int) -> Unit = {},
    onIpAddressChange: (String) -> Unit = {},
    onPortChange: (String) -> Unit = {},
    onRestart: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onRestart) {
                Icon(Icons.Filled.RestartAlt, contentDescription = null)
            }
        }
    ) { padding ->
        SettingsList(
            state = state,
            modifier = Modifier.padding(padding),
            onWpanIndexChange = onWpanIndexChange,
            onIpAddressChange = onIpAddressChange,
            onPortChange = onPortChange
        )
    }
}

@Composable
private fun SettingsList(
    state: SettingsState,
    modifier: Modifier = Modifier,
    onWpanIndexChange: (Int) -> Unit = {},
    onIpAddressChange: (String) -> Unit = {},
    onPortChange: (String) -> Unit = {}
) = with(state) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(all = 16.dp)
            .then(modifier)
    ) {
        SettingsHeader(value = stringResource(id = R.string.text_settings_network))

        SettingsDropdown(
            label = stringResource(id = R.string.text_settings_technology),
            items = state.wlanOptions,
            selectedItem = state.wlan,
            onSelectedIndex = onWpanIndexChange
        )

        Spacer(modifier = Modifier.height(Spacing.ExtraExtraSmall))

        SettingsInput(
            value = ipAddress.value,
            onValueChange = onIpAddressChange,
            isError = port.isError,
            label = stringResource(id = R.string.text_settings_ip_address),
            placeholder = stringResource(id = R.string.text_settings_ip_address_placeholder)
        )

        Spacer(modifier = Modifier.height(Spacing.ExtraExtraSmall))

        SettingsInput(
            value = port.value,
            onValueChange = onPortChange,
            isError = port.isError,
            label = stringResource(id = R.string.text_settings_port),
            placeholder = stringResource(id = R.string.text_settings_port_placeholder),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
private fun SettingsHeader(
    value: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        color = Color.Secondary,
        fontWeight = FontWeight.Bold,
        text = value
    )

    Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SettingsDropdown(
    label: String,
    items: List<String>,
    selectedItem: String,
    onSelectedIndex: (Int) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1,
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(text = label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEachIndexed { index, option ->
                DropdownMenuItem(onClick = {
                    onSelectedIndex(index)
                    expanded = false
                }) { Text(text = option) }
            }
        }
    }
}

@Composable
private fun SettingsInput(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) = OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    maxLines = 1,
    value = value,
    onValueChange = onValueChange,
    isError = isError,
    enabled = enabled,
    label = { Text(text = label) },
    placeholder = { Text(text = placeholder) },
    keyboardOptions = keyboardOptions
)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val state = SettingsState()

    MaterialTheme {
        SettingsScreenContent(state = state)
    }
}
