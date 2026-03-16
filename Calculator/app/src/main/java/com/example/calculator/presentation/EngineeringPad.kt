package com.example.calculator.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calculator.ui.theme.CalculatorTheme
import com.example.calculator.viewmodels.CalculatorViewModel

@Composable
fun EngineeringPad(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    buttonCornerRadius: Dp = 28.dp,
    buttonHeightPortrait: Dp = 52.dp,
    buttonHeightLandscape: Dp = 56.dp
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val landscapeButtons = listOf(
            listOf("(", ")", "x^y"),
            listOf("!", "√x", "x^2"),
            listOf("sin", "cos", "tan"),
            listOf("ln", "log", "e^x")
        )

        val portraitButtons = listOf(
            listOf("(", ")", "x^y", "!"),
            listOf("√x", "x^2", "sin", "cos"),
            listOf("tan", "ln", "log", "e^x")
        )

        @Composable
        fun engButtonColors(label: String) = ButtonDefaults.buttonColors(
            containerColor = CalculatorTheme.colors.additional,
            contentColor = MaterialTheme.colorScheme.onTertiary
        )

        val shape = RoundedCornerShape(buttonCornerRadius)
        val height = if (isLandscape) buttonHeightLandscape else buttonHeightPortrait
        val buttons = if (isLandscape) landscapeButtons else portraitButtons

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { btn ->
                    if (btn.isNotEmpty()) {
                        Button(
                            onClick = { viewModel.onInput(btn) },
                            modifier = Modifier
                                .weight(1f)
                                .height(height),
                            shape = shape,
                            colors = engButtonColors(btn)
                        ) {
                            Text(btn)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}