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

    private val operators = listOf('+', '-', '*', '/', '^')
    private val functions = listOf("sqrt(", "sin(", "cos(", "tan(", "ln(", "log(", "exp(")

    fun toggleMode() {
        isEngineeringMode = !isEngineeringMode
    }

    fun onInput(value: String) {
        if (expression == "Error") {
            expression = ""
        }

        when (value) {
            "AC" -> onClear()
            "<=" -> backspace()
            "+/-" -> toggleSign()
            "%" -> appendPercent()
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
            if (op == '-') expression += op
            return
        }

        val lastChar = expression.last()

        if (operators.contains(lastChar)) {
            if (expression.length >= 2) {
                val secondLast = expression[expression.length - 2]
                if (operators.contains(secondLast) || secondLast == '(') {
                    expression = expression.dropLast(2) + op + suffix
                    return
                }
            }
            expression = expression.dropLast(1) + op + suffix
        } else if (lastChar == '(') {
            if (op == '-') expression += op
        } else {
            expression += op + suffix
        }
    }

    private fun appendFunction(func: String) {
        if (expression.isEmpty()) {
            expression += "$func("
            return
        }

        var i = expression.length - 1
        while (i >= 0 && (expression[i].isDigit() || expression[i] == '.')) {
            i--
        }

        if (i == expression.length - 1) {
            if (expression.last() == ')') {
                expression += "*$func("
            } else {
                expression += "$func("
            }
        } else {
            val numberStr = expression.substring(i + 1)
            expression = expression.substring(0, i + 1) + "$func($numberStr)"
        }
    }

    private fun appendOpenParen() {
        if (expression.isNotEmpty() && (expression.last().isDigit() || expression.last() == '.' || expression.last() == ')')) {
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
        var i = expression.length - 1
        var hasDot = false
        while (i >= 0 && (expression[i].isDigit() || expression[i] == '.')) {
            if (expression[i] == '.') {
                hasDot = true
                break
            }
            i--
        }

        if (!hasDot) {
            if (expression.isEmpty() || (!expression.last().isDigit() && expression.last() != ')')) {
                expression += "0."
            } else if (expression.last() == ')') {
                expression += "*0."
            } else {
                expression += "."
            }
        }
    }

    private fun backspace() {
        if (expression.isEmpty()) return
        val foundFunction = functions.find { expression.endsWith(it) }
        if (foundFunction != null) {
            expression = expression.dropLast(foundFunction.length)
        } else {
            expression = expression.dropLast(1)
        }
    }

    private fun appendPercent() {
        if (expression.isEmpty()) return
        expression += "*0.01"
    }

    private fun toggleSign() {
        if (expression.isEmpty()) {
            expression = "-"
            return
        }

        var i = expression.length - 1
        while (i >= 0 && (expression[i].isDigit() || expression[i] == '.')) i--
        val start = i + 1

        if (start >= expression.length) {
            if (expression.last() == ')') {
                var depth = 0
                var j = expression.length - 1
                while (j >= 0) {
                    val c = expression[j]
                    if (c == ')') depth++
                    else if (c == '(') {
                        depth--
                        if (depth == 0) break
                    }
                    j--
                }
                if (j >= 0) {
                    if (j >= 2 && expression.substring(j - 2, j) == "(-") {
                        expression = expression.removeRange(j - 2, j)
                    } else if (j >= 1 && expression[j - 1] == '-') {
                        expression = expression.removeRange(j - 1, j)
                    } else {
                        val prefix = expression.substring(0, j)
                        if (prefix.isNotEmpty() && !operators.contains(prefix.last()) && prefix.last() != '(') {
                            expression = prefix + "*(-" + expression.substring(j) + ")"
                        } else {
                            expression = prefix + "(-" + expression.substring(j) + ")"
                        }
                    }
                }
            } else if (operators.contains(expression.last())) {
                expression += "(-"
            }
            return
        }

        val number = expression.substring(start)
        val prefix = expression.substring(0, start)

        if (prefix.endsWith("(-")) {
            expression = prefix.dropLast(2) + number
        } else if (prefix.endsWith("-") && (prefix.length == 1 || operators.contains(prefix[prefix.length - 2]) || prefix[prefix.length - 2] == '(')) {
            expression = prefix.dropLast(1) + number
        } else if (prefix.isNotEmpty() && !operators.contains(prefix.last()) && prefix.last() != '(') {
            expression = "$prefix*(-$number)"
        } else {
            expression = "$prefix(-$number)"
        }
    }

    fun onClear() {
        expression = ""
        result = ""
    }

    fun onCalculate() {
        if (expression.isBlank()) return
        var expToEval = expression
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