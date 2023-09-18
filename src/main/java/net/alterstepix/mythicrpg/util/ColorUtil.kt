package net.alterstepix.mythicrpg.util

import net.md_5.bungee.api.ChatColor

/**
 *  Format given string with hex color codes.
 *  @param source String to format (example: "#fd6990$$Hello, $$#69fdc5$$world.")
 *  @return Formatted string (printable in chat)
 */
fun colorCode(source: String): String {
    val string = StringBuilder()

    var color = true
    for (part in source.split("$$").toTypedArray()) {
        if (color) string.append(ChatColor.of(part))
        else string.append(part)

        color = !color
    }
    return string.toString()
}

/**
 * Converts a hex color code to printable minecraft color code
 * @param hex A hex color code (that starts with #).
 * @return Printable minecraft color code.
 */
fun hex(hex: String) = ChatColor.of(hex).toString()