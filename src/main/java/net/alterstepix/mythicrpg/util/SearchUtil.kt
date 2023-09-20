package net.alterstepix.mythicrpg.util

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

inline fun <reified T: Entity> searchEntities(location: Location, radius: Double) =
    (location.world?.entities ?: listOf())
        .filter { entity -> entity.location.distanceSquared(location) <=  radius }
        .filterIsInstance<T>()

inline fun <reified T: Entity> searchEntities(player: Player, radius: Double) =
    searchEntities<T>(player.location, radius)
        .filter { entity -> entity != player }

inline fun <reified T: Entity> searchEntities(location: MLoc, radius: Double) =
    searchEntities<T>(location.location, radius)