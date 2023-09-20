package net.alterstepix.mythicrpg.util

import org.bukkit.Location
import org.bukkit.World

data class MLoc(val world: World, val x: Double, val y: Double, val z: Double) {
    val location: Location get() = Location(world, x, y, z)
}