package br.pucrio.inf.lac.mobilehub.presentation.ui.main.contextdata

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.pucrio.inf.lac.mobilehub.di.daggerViewModel
import br.pucrio.inf.lac.mobilehub.di.injector
import br.pucrio.inf.lac.mobilehub.presentation.ui.model.SensorDataUiModel

@Composable
internal fun ContextDataScreen() {
    val viewModel: ContextDataViewModel = daggerViewModel(injector::contextDataViewModel)
    val state = viewModel.state

    ContextDataScreenContent(state = state)
}

@Composable
private fun ContextDataScreenContent(
    state: ContextDataState
) = with(state) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(contextData) { sensorData ->
            SensorData(
                sensorData = sensorData,
                modifier = Modifier.padding(all = 16.dp)
            )
        }
    }
}

@Composable
private fun SensorData(
    sensorData: SensorDataUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.width(144.dp),
            text = sensorData.serviceName.capitalize(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = sensorData.serviceData.joinToString(),
            textAlign = TextAlign.Center,
        )
    }
}

private fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase() else it.toString()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val state = ContextDataState(
        contextData = listOf(
            SensorDataUiModel("temperature", listOf("13.4", "15.2")),
            SensorDataUiModel("humidity", listOf("50.0")),
            SensorDataUiModel("accelerometer", listOf("23.6", "25.3", "20.0"))
        )
    )

    MaterialTheme {
        ContextDataScreenContent(state = state)
    }
}
