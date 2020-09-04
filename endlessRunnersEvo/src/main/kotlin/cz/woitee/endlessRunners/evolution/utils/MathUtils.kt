package cz.woitee.endlessRunners.evolution.utils

import kotlin.math.sqrt

object MathUtils {
    fun divisorsOf(value: Int): ArrayList<Int> {
        var reducingValue = value
        val intSqrt = (sqrt(value.toDouble()) + 0.0001).toInt()
        val sieve = IntArray(intSqrt + 1)

        for (potentialDivisor in 2 .. intSqrt) {
            if (sieve[potentialDivisor] == -1) continue

            while (reducingValue % potentialDivisor == 0) {
                sieve[potentialDivisor] += 1
                reducingValue /= potentialDivisor
            }
            if (sieve[potentialDivisor] > 0) {
                for (i in 2 * potentialDivisor .. intSqrt step potentialDivisor) {
                    sieve[i] = -1
                }
            }
        }

        val res = ArrayList<Int>()
        sieve.forEachIndexed { divisor, pow ->
            repeat(pow) {
                res.add(divisor)
            }
        }
        return res
    }

    fun allDivisorsOf(value: Int): ArrayList<Int> {
        val divisorsMap = HashMap<Int, Int>()

        for (divisor in divisorsOf(value)) {
            divisorsMap[divisor] = divisorsMap.getOrDefault(divisor, 0) + 1
        }

        val divisors = divisorsMap.keys.toIntArray()
        val multiplicities = divisorsMap.values.toIntArray()
        val res = ArrayList<Int>()

        divisorsRecursive(1, 0, divisors, multiplicities, res)

        res.sort()
        return res
    }

    private fun divisorsRecursive(current: Int, i: Int, divisors: IntArray, multiplicities: IntArray, results: ArrayList<Int>) {
        if (i >= divisors.size) {
            results.add(current)
            return
        }

        var cur = current

        divisorsRecursive(cur, i + 1, divisors, multiplicities, results)
        repeat(multiplicities[i]) {
            cur *= divisors[i]
            divisorsRecursive(cur, i + 1, divisors, multiplicities, results)
        }
    }
}