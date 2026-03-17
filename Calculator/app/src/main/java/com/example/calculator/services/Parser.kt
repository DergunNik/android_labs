package com.example.calculator.services

import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.*

object Parser {
    fun evaluate(str: String): BigDecimal {
        val mc = MathContext.DECIMAL64

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
                return x
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
                    x = BigDecimal(str.substring(startPos, pos))
                } else if (ch >= 'a'.code && ch <= 'z'.code) {
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = when (func) {
                        "pi" -> BigDecimal(Math.PI)
                        "e" -> BigDecimal(Math.E)
                        else -> {
                            val arg = parseFactor().toDouble()
                            val mathResult = when (func) {
                                "sqrt" -> sqrt(arg)
                                "sin" -> sin(arg)
                                "cos" -> cos(arg)
                                "tan" -> tan(arg)
                                "ln" -> ln(arg)
                                "log" -> log10(arg)
                                "exp" -> exp(arg)
                                else -> throw RuntimeException("Unknown function: $func")
                            }
                            BigDecimal(mathResult)
                        }
                    }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) {
                    val exponent = parseFactor()
                    x = try {
                        val intExp = exponent.intValueExact()
                        x.pow(intExp, mc)
                    } catch (e: ArithmeticException) {
                        BigDecimal(x.toDouble().pow(exponent.toDouble()))
                    }
                }

                while (eat('!'.code)) {
                    x = factorial(x)
                }

                return x
            }

            fun factorial(n: BigDecimal): BigDecimal {
                val nDouble = n.toDouble()
                if (nDouble < 0 || nDouble % 1.0 != 0.0) throw IllegalArgumentException("Invalid factorial")
                if (nDouble == 0.0 || nDouble == 1.0) return BigDecimal.ONE
                var res = BigDecimal.ONE
                for (i in 2..nDouble.toInt()) {
                    res = res.multiply(BigDecimal(i))
                }
                return res
            }
        }.parse()
    }
}