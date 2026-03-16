package com.example.calculator.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Display(expression: String, result: String, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    LaunchedEffect(result) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        Text(
            text = result,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Text(
            text = if (expression.isEmpty()) "0" else expression,
            fontSize = 48.sp,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            textAlign = TextAlign.End,
            maxLines = 1,
            softWrap = false,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}