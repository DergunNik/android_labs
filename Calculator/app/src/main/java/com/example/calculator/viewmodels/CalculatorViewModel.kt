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

    private fun appendFactorial() {
        if (expression.isEmpty()) {
            expression = "0!"
            return
        }

        val lastChar = expression.last()
        if (lastChar == '(') return

        if (lastChar.isDigit() || lastChar == '.' || lastChar == ')') {
            expression += "!"
        } else if (operators.contains(lastChar)) {
            expression += "0!"
        }
    }

    private fun appendFunction(func: String) {
        if (expression.isEmpty()) {
            expression = "$func(0)"
            return
        }

        val lastChar = expression.last()

        if (operators.contains(lastChar) || lastChar == '(') {
            expression += "$func(0)"
            return
        }

        if (lastChar == ')') {
            expression += "*$func("
            return
        }

        var i = expression.length - 1
        while (i >= 0 && (expression[i].isDigit() || expression[i] == '.')) {
            i--
        }

        if (i == expression.length - 1) {
            expression += "$func("
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
        if (expression.isEmpty()) {
            expression = "0."
            return
        }

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
            if (!expression.last().isDigit() && expression.last() != ')') {
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
        if (expression.isEmpty()) {
            expression = "0*0.01"
            return
        }
        expression += "*0.01"
    }

    private fun toggleSign() {
        if (expression.isEmpty()) {
            expression = "-0"
            return
        }
        if (expression == "-") {
            expression = ""
            return
        }

        if (expression.endsWith("(-")) {
            expression = expression.dropLast(2)
            return
        }

        val wrappedNegNumRegex = Regex("""\(\-([\d.]+)\)$""")
        val matchWrapped = wrappedNegNumRegex.find(expression)
        if (matchWrapped != null && matchWrapped.range.last == expression.length - 1) {
            val num = matchWrapped.groupValues[1]
            val prefix = expression.substring(0, matchWrapped.range.first)
            expression = prefix + num
            return
        }

        val startNegNumRegex = Regex("""^-([\d.]+)$""")
        if (expression.matches(startNegNumRegex)) {
            expression = expression.substring(1)
            return
        }

        val incompleteWrappedRegex = Regex("""\(\-([\d.]+)$""")
        val matchIncomplete = incompleteWrappedRegex.find(expression)
        if (matchIncomplete != null && matchIncomplete.range.last == expression.length - 1) {
            val num = matchIncomplete.groupValues[1]
            val prefix = expression.substring(0, matchIncomplete.range.first)
            expression = prefix + num
            return
        }

        var i = expression.length - 1
        while (i >= 0 && (expression[i].isDigit() || expression[i] == '.')) i--

        if (i < expression.length - 1) {
            val num = expression.substring(i + 1)
            val prefix = expression.substring(0, i + 1)

            if (prefix.isEmpty()) {
                expression = "-$num"
            } else if (operators.contains(prefix.last()) || prefix.last() == '(') {
                expression = "$prefix(-$num"
            } else {
                expression = "$prefix*(-$num"
            }
            return
        }

        val lastChar = expression.last()
        if (operators.contains(lastChar) || lastChar == '(') {
            expression += "(-"
        } else if (lastChar == ')') {
            expression += "*(-"
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