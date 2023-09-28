package net.alterstepix.mythicrpg.util

fun random() = Math.random()

fun <T> random(range: ClosedRange<T>): Double
where T: Number,
      T: Comparable<T>
{
    return range.start.toDouble() + (Math.random() * (range.endInclusive.toDouble() - range.start.toDouble()))
}