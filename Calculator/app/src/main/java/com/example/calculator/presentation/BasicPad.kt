package com.example.calculator.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculator.viewmodels.CalculatorViewModel

@Composable
fun BasicPad(viewModel: CalculatorViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+"),
            listOf("%")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { btn ->
                    Button(modifier = Modifier.weight(1f), onClick = {
                        when (btn) {
                            "C" -> viewModel.onClear()
                            "=" -> viewModel.onCalculate()
                            else -> viewModel.onInput(btn)
                        }
                    }) { Text(btn) }
                }
            }
        }
    }
}