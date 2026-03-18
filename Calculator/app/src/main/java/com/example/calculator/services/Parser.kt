package com.example.calculator.services

import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.*

object Parser {
    fun evaluate(str: String): BigDecimal {
        val mc = MathContext.UNLIMITED

        return object {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): BigDecimal {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                // Убираем лишние нули в конце для чистого результата
                return x.stripTrailingZeros()
            }

            fun parseExpression(): BigDecimal {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x = x.add(parseTerm(), mc)
                    else if (eat('-'.code)) x = x.subtract(parseTerm(), mc)
                    else return x
                }
            }

            fun parseTerm(): BigDecimal {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x = x.multiply(parseFactor(), mc)
                    else if (eat('/'.code)) x = x.divide(parseFactor(), mc)
                    else if (eat('%'.code)) x = x.remainder(parseFactor(), mc)
                    else return x
                }
            }

            fun parseFactor(): BigDecimal {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return parseFactor().negate(mc)

                var x: BigDecimal
                val startPos = pos

                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = BigDecimal(str.substring(startPos, pos), mc)
                } else if (ch >= 'a'.code && ch <= 'z'.code) {
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = when (func) {
                        "pi" -> BigDecimal("3.141592653589793238462643383279503", mc)
                        "e" -> BigDecimal("2.718281828459045235360287471352662", mc)
                        else -> {
                            val arg = parseFactor()
                            val mathResult = when (func) {
                                "sqrt" -> arg.sqrt(mc)
                                "sin" -> sin(arg.toDouble()).toBigDecimal(mc)
                                "cos" -> cos(arg.toDouble()).toBigDecimal(mc)
                                "tan" -> tan(arg.toDouble()).toBigDecimal(mc)
                                "ln" -> ln(arg.toDouble()).toBigDecimal(mc)
                                "log" -> log10(arg.toDouble()).toBigDecimal(mc)
                                "exp" -> exp(arg.toDouble()).toBigDecimal(mc)
                                else -> throw RuntimeException("Unknown function: $func")
                            }
                            mathResult
                        }
                    }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                while (eat('!'.code)) {
                    x = factorial(x, mc)
                }

                if (eat('^'.code)) {
                    val exponent = parseFactor()
                    x = power(x, exponent, mc)
                }

                return x
            }

            fun factorial(n: BigDecimal, mc: MathContext): BigDecimal {
                if (n < BigDecimal.ZERO || n.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
                    throw IllegalArgumentException("Factorial is only for non-negative integers")
                }
                val intN = n.intValueExact()
                if (intN == 0 || intN == 1) return BigDecimal.ONE

                var res = BigDecimal.ONE
                for (i in 2..intN) {
                    res = res.multiply(BigDecimal(i), mc)
                }
                return res
            }

            fun power(base: BigDecimal, exponent: BigDecimal, mc: MathContext): BigDecimal {
                return try {
                    val expInt = exponent.intValueExact()
                    if (expInt == 0) return BigDecimal.ONE

                    var res = BigDecimal.ONE
                    val absExp = if (expInt < 0) -expInt else expInt

                    for (i in 1..absExp) {
                        res = res.multiply(base, mc)
                    }

                    if (expInt < 0) BigDecimal.ONE.divide(res, mc) else res
                } catch (e: Exception) {
                    BigDecimal(base.toDouble().pow(exponent.toDouble()), mc)
                }
            }
        }.parse()
    }
}