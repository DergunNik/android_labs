package com.example.calculator.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Display(expression: String, result: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        Text(
            text = expression,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            maxLines = 2
        )

        Text(
            text = result,
            fontSize = 48.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            maxLines = 1
        )
    }
}