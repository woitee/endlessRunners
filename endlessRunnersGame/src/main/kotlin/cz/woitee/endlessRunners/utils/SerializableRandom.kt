package cz.woitee.endlessRunners.utils

import java.io.Serializable
import kotlin.random.Random

/**
 * A copy of Kotlin's XorWowRandom, but Serializable.
 */
class SerializableRandom(
    private var x: Int,
    private var y: Int,
    private var z: Int,
    private var w: Int,
    private var v: Int,
    private var addend: Int
) : Random(), Serializable {

    constructor(seed1: Int, seed2: Int) :
            this(seed1, seed2, 0, 0, seed1.inv(), (seed1 shl 10) xor (seed2 ushr 4))

    constructor(seed: Long) : this(seed.toInt(), seed.shr(32).toInt())

    init {
        require((x or y or z or w or v) != 0) { "Initial state must have at least one non-zero element." }

        // some trivial seeds can produce several values with zeroes in upper bits, so we discard first 64
        repeat(64) { nextInt() }
    }

    override fun nextInt(): Int {
        var t = x
        t = t xor (t ushr 2)
        x = y
        y = z
        z = w
        val v0 = v
        w = v0
        t = (t xor (t shl 1)) xor v0 xor (v0 shl 4)
        v = t
        addend += 362437
        return t + addend
    }

    private fun Int.takeUpperBits(bitCount: Int): Int =
            this.ushr(32 - bitCount) and (-bitCount).shr(31)

    override fun nextBits(bitCount: Int): Int =
            nextInt().takeUpperBits(bitCount)
}
