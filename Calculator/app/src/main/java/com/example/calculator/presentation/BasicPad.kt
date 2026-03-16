package com.example.calculator.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculator.viewmodels.CalculatorViewModel

@Composable
fun BasicPad(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    Column(modifier = modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val portraitButtons = listOf(
            listOf("<=", "AC", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("+/-", "0", ".", "="),
        )

        val landscapeButtons = listOf(
            listOf("7", "8", "9", "<=", "÷"),
            listOf("4", "5", "6", "AC", "×"),
            listOf("1", "2", "3", "%", "-"),
            listOf("+/-", "0", ".", "=", "+"),
        )

        @Composable
        fun buttonColorsFor(label: String) = when (label) {
            "+", "-", "×", "÷", "=" -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
            "AC", "%", "<=" -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
            else -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }

        val shape = if (isLandscape) RoundedCornerShape(28.dp) else CircleShape
        val buttons = if (isLandscape) landscapeButtons else portraitButtons

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { btn ->
                    Button(
                        onClick = {
                            when (btn) {
                                "AC" -> viewModel.onClear()
                                "=" -> viewModel.onCalculate()
                                else -> viewModel.onInput(btn)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isLandscape) 56.dp else 72.dp),
                        shape = shape,
                        colors = buttonColorsFor(btn)
                    ) {
                        Text(btn)
                    }
                }
            }
        }
    }
}