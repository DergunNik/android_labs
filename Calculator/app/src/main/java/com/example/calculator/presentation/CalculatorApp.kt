package com.example.calculator.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculator.BuildConfig
import com.example.calculator.viewmodels.CalculatorViewModel

@Composable
fun CalculatorApp(viewModel: CalculatorViewModel = viewModel()) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isDemo = BuildConfig.FLAVOR == "demo"

    val showEngineering = !isDemo && (isLandscape || viewModel.isEngineeringMode)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { viewModel.toggleMode() }) {
                Text(if (viewModel.isEngineeringMode) "Basic" else "Eng")
            }
        }

        Display(
            expression = viewModel.expression,
            result = viewModel.result,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showEngineering) {
                EngineeringPad(viewModel = viewModel, modifier = Modifier.weight(1f))
            }
            BasicPad(viewModel = viewModel, modifier = Modifier.weight(1f))
        }
    }
}