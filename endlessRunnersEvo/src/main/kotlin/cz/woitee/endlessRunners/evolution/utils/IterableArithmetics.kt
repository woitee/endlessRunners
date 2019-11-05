package cz.woitee.endlessRunners.evolution.utils

fun sumOfInts(seq: Iterable<Int>): Int {
    var total = 0
    for (i in seq) total += i
    return total
}
fun sumOfLongs(seq: Iterable<Long>): Long {
    var total = 0L
    for (i in seq) total += i
    return total
}
fun sumOfFloats(seq: Iterable<Float>): Float {
    var total = 0f
    for (i in seq) total += i
    return total
}
fun sumOfDoubles(seq: Iterable<Double>): Double {
    var total = 0.0
    for (i in seq) total += i
    return total
}

fun meanOfInts(seq: Iterable<Int>): Double {
    return sumOfInts(seq).toDouble() / seq.count()
}
fun meanOfLongs(seq: Iterable<Long>): Double {
    return sumOfLongs(seq).toDouble() / seq.count()
}
fun meanOfFloats(seq: Iterable<Float>): Double {
    return sumOfFloats(seq).toDouble() / seq.count()
}
fun meanOfDoubles(seq: Iterable<Double>): Double {
    return sumOfDoubles(seq).toDouble() / seq.count()
}
