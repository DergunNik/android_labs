package com.example.timer.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timer.domain.model.PhaseType
import com.example.timer.domain.model.TimerPhase
import com.example.timer.presentation.viewmodel.EditViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val sequence by viewModel.sequenceState.collectAsState()

    if (sequence == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentSeq = sequence!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentSeq.id.isEmpty()) "Новый таймер" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveSequence(onSaved = onNavigateBack) }) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = currentSeq.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Название таймера") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Фазы таймера", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentSeq.phases, key = { it.id }) { phase ->
                    PhaseEditItem(
                        phase = phase,
                        onIncreaseTime = { viewModel.updatePhaseDuration(phase.id, 5) },
                        onDecreaseTime = { viewModel.updatePhaseDuration(phase.id, -5) },
                        onTimeChange = { newTimeStr -> viewModel.setPhaseDuration(phase.id, newTimeStr) },
                        onRemove = { viewModel.removePhase(phase.id) }
                    )
                }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PhaseType.entries.forEach { type ->
                    Button(onClick = { viewModel.addPhase(type) }) {
                        Text(type.name)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhaseEditItem(
    phase: TimerPhase,
    onIncreaseTime: () -> Unit,
    onDecreaseTime: () -> Unit,
    onTimeChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outline
    val controlHeight = 32.dp

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = phase.type.name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.weight(2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    onClick = onDecreaseTime,
                    modifier = Modifier.size(controlHeight),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("-5", style = MaterialTheme.typography.bodySmall, color = contentColor)
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                BasicTextField(
                    value = if (phase.durationSeconds == 0) "" else phase.durationSeconds.toString(),
                    onValueChange = { if (it.all { char -> char.isDigit() }) onTimeChange(it) },
                    modifier = Modifier
                        .width(65.dp)
                        .height(controlHeight),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        color = contentColor,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = phase.durationSeconds.toString(),
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = remember { MutableInteractionSource() },
                            contentPadding = PaddingValues(0.dp),
                            container = {
                                OutlinedTextFieldDefaults.ContainerBox(
                                    enabled = true,
                                    isError = false,
                                    interactionSource = remember { MutableInteractionSource() },
                                    colors = OutlinedTextFieldDefaults.colors(),
                                    shape = MaterialTheme.shapes.extraSmall,
                                )
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.width(4.dp))

                Surface(
                    onClick = onIncreaseTime,
                    modifier = Modifier.size(controlHeight),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("+5", style = MaterialTheme.typography.bodySmall, color = contentColor)
                    }
                }
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(onClick = onRemove, modifier = Modifier.size(controlHeight)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(20.dp),
                        tint = contentColor
                    )
                }
            }
        }
    }
}