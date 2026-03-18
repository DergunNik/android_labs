package com.example.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.calculator.services.Parser
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {
    var expression by mutableStateOf("")
        private set

    var result by mutableStateOf("")
        private set

    var isEngineeringMode by mutableStateOf(false)
        private set

    private val operators = listOf('+', '-', '*', '/', '^')
    private val functions = listOf("sqrt(", "sin(", "cos(", "tan(", "ln(", "log(", "exp(")

    fun toggleMode() {
        isEngineeringMode = !isEngineeringMode
    }

    fun onInput(value: String) {
        if (expression == "Error") expression = ""

        when (value) {
            "AC" -> onClear()
            "<=" -> backspace()
            "+/-" -> toggleSign()
            "%" -> appendPercent()
            "!" -> appendFactorial()
            "×" -> appendOperator('*')
            "÷" -> appendOperator('/')
            "+", "-" -> appendOperator(value[0])
            "(" -> appendOpenParen()
            ")" -> appendCloseParen()
            "." -> appendDot()
            "=" -> onCalculate()
            "x^2" -> appendOperator('^', "2")
            "x^y" -> appendOperator('^')
            "√x" -> appendFunction("sqrt")
            "e^x" -> appendFunction("exp")
            "sin", "cos", "tan", "ln", "log" -> appendFunction(value)
            else -> appendDigit(value)
        }
    }

    private fun appendDigit(digit: String) {
        if (expression.isNotEmpty() && expression.last() == ')') {
            expression += "*$digit"
        } else {
            expression += digit
        }
    }

    private fun appendOperator(op: Char, suffix: String = "") {
        if (expression.isEmpty()) {
            expression = "0$op$suffix"
            return
        }
        val lastChar = expression.last()
        if (operators.contains(lastChar)) {
            expression = expression.dropLast(1) + op + suffix
        } else {
            expression += op + suffix
        }
    }

    private fun appendFactorial() {
        if (expression.isEmpty()) {
            expression = "0!"
            return
        }
        val lastChar = expression.last()
        if (lastChar.isDigit() || lastChar == ')' || lastChar == 'e' || lastChar == 'i') {
            expression += "!"
        }
    }

    private fun appendFunction(func: String) {
        if (expression.isEmpty() || operators.contains(expression.last()) || expression.last() == '(') {
            expression += "$func("
        } else {
            expression += "*$func("
        }
    }

    private fun appendOpenParen() {
        if (expression.isNotEmpty() && (expression.last().isDigit() || expression.last() == ')' || expression.last() == '!')) {
            expression += "*("
        } else {
            expression += "("
        }
    }

    private fun appendCloseParen() {
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        if (closeCount < openCount && expression.isNotEmpty() && expression.last() != '(') {
            expression += ")"
        }
    }

    private fun appendDot() {
        if (expression.isEmpty()) { expression = "0."; return }
        val lastPart = expression.split(Regex("[+\\-*/^()]")).last()
        if (!lastPart.contains('.')) {
            expression += if (expression.last().isDigit()) "." else "0."
        }
    }

    private fun backspace() {
        if (expression.isEmpty()) return
        val foundFunction = functions.find { expression.endsWith(it) }
        expression = if (foundFunction != null) expression.dropLast(foundFunction.length) else expression.dropLast(1)
    }

    private fun appendPercent() {
        if (expression.isNotEmpty()) expression += "*0.01"
    }

    private fun toggleSign() {
        if (expression.isEmpty()) { expression = "-"; return }
        if (expression.startsWith("-")) expression = expression.substring(1)
        else expression = "-$expression"
    }

    fun onClear() {
        expression = ""
        result = ""
    }

    fun onCalculate() {
        if (expression.isBlank()) return
        var expToEval = expression.replace("×", "*").replace("÷", "/")

        val open = expToEval.count { it == '(' }
        val close = expToEval.count { it == ')' }
        if (open > close) expToEval += ")".repeat(open - close)

        try {
            val evalResult = Parser.evaluate(expToEval)
            val formattedResult = evalResult.stripTrailingZeros().toPlainString()

            result = expression
            expression = formattedResult
        } catch (e: Exception) {
            result = expression
            expression = "Error"
        }
    }
}