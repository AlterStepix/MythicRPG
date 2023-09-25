package net.alterstepix.mythicrpg.util

import java.text.DecimalFormat

fun Double.format(places: Int): String {
    return String.format("%1.${places}f", this)
}