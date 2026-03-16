package com.example.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.calculator.services.Parser

class CalculatorViewModel : ViewModel() {
    var expression by mutableStateOf("")
        private set
    var result by mutableStateOf("")
        private set
    var isEngineeringMode by mutableStateOf(false)
        private set

    fun toggleMode() {
        isEngineeringMode = !isEngineeringMode
    }

    fun onInput(value: String) {
        expression += value
    }

    fun onClear() {
        expression = ""
        result = ""
    }

    fun onCalculate() {
        try {
            if (expression.isNotBlank()) {
                val evalResult = Parser.evaluate(expression)
                result = if (evalResult % 1.0 == 0.0) {
                    evalResult.toLong().toString()
                } else {
                    evalResult.toString()
                }
            }
        } catch (e: Exception) {
            result = "Error"
        }
    }
}