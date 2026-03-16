package com.example.calculator.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculator.viewmodels.CalculatorViewModel

@Composable
fun EngineeringPad(viewModel: CalculatorViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val buttons = listOf(
            listOf("(", ")", "^"),
            listOf("!", "sqrt", "")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { btn ->
                    if (btn.isNotEmpty()) {
                        Button(modifier = Modifier.weight(1f), onClick = { viewModel.onInput(btn) }) { Text(btn) }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}