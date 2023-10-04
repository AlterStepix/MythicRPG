package net.alterstepix.mythicrpg.util

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

const val searchIgnoreTag = "mythicrpg-search-ignore"

inline fun <reified T: Entity> searchEntities(location: Location, radius: Double) =
    (location.world?.entities ?: listOf())
        .filter { entity -> entity.boundingBox.center.distanceSquared(location.toVector()) <= radius * radius }
        .filterNot { entity -> entity.scoreboardTags.contains(searchIgnoreTag) }
        .filterIsInstance<T>()

inline fun <reified T: Entity> checkEntities(location: Location) =
    (location.world?.entities ?: listOf())
        .filter { entity -> entity.boundingBox.contains(location.toVector()) }
        .filterNot { entity -> entity.scoreboardTags.contains(searchIgnoreTag) }
        .filterIsInstance<T>()

inline fun <reified T: Entity> checkEntities(location: MLoc) = checkEntities<T>(location.location)

inline fun <reified T: Entity> searchEntitiesCylinder(location: Location, radius: Double) =
    (location.world?.entities ?: listOf())
        .filter { entity ->
            val eLoc = entity.boundingBox.center
            eLoc.y = 0.0

            val loc = location.clone()
            loc.y = 0.0

            eLoc.distanceSquared(loc.toVector()) <= radius * radius
        }
        .filterNot { entity -> entity.scoreboardTags.contains(searchIgnoreTag) }
        .filterIsInstance<T>()

inline fun <reified T: Entity> searchEntities(player: Player, radius: Double) =
    searchEntities<T>(player.location, radius)
        .filter { entity -> entity != player }

inline fun <reified T: Entity> searchEntitiesCylinder(player: Player, radius: Double) =
    searchEntitiesCylinder<T>(player.location, radius)
        .filter { entity -> entity != player }

inline fun <reified T: Entity> searchEntities(location: MLoc, radius: Double) =
    searchEntities<T>(location.location, radius)

inline fun <reified T: Entity> searchEntitiesCylinder(location: MLoc, radius: Double) =
    searchEntitiesCylinder<T>(location.location, radius)