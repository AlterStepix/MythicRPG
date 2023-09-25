package net.alterstepix.mythicrpg.util

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.sqrt

data class MLoc(val world: World, val x: Double, val y: Double, val z: Double) {
    val location: Location get() = Location(world, x, y, z)
    val vector: Vector get() = Vector(x, y, z)
    val mVec: MVec get() = MVec(x, y, z)

    fun add(x: Double, y: Double, z: Double) = MLoc(world, this.x + x, this.y + y, this.z + z)
    fun add(vec: MVec) = MLoc(world, this.x + vec.x, this.y + vec.y, this.z + vec.z)

    fun sub(x: Double, y: Double, z: Double) = MLoc(world, this.x - x, this.y - y, this.z - z)
    fun sub(vec: MVec) = MLoc(world, this.x - vec.x, this.y - vec.y, this.z - vec.z)

    fun mul(x: Double, y: Double, z: Double) = MLoc(world, this.x * x, this.y * y, this.z * z)
    fun mul(vec: MVec) = MLoc(world, this.x * vec.x, this.y * vec.y, this.z * vec.z)
    fun mul(s: Double) = MLoc(world, this.x * s, this.y * s, this.z * s)

    fun withX(s: Double) = MLoc(world, s, y, z)
    fun withY(s: Double) = MLoc(world, x, s, z)
    fun withZ(s: Double) = MLoc(world, x, y, s)

    fun multiply(s: Double) = MLoc(world, x * s, y * s, z * s)
}

data class MVec(val x: Double, val y: Double, val z: Double) {
    val vector: Vector get() = Vector(x, y, z)
    fun toMLoc(world: World) = MLoc(world, x, y, z)
    fun toLocation(world: World) = Location(world, x, y, z)

    fun add(x: Double, y: Double, z: Double) = MVec(this.x + x, this.y + y, this.z + z)
    fun add(vec: MVec) = MVec(this.x + vec.x, this.y + vec.y, this.z + vec.z)

    fun sub(x: Double, y: Double, z: Double) = MVec(this.x - x, this.y - y, this.z - z)
    fun sub(vec: MVec) = MVec(this.x - vec.x, this.y - vec.y, this.z - vec.z)

    fun mul(x: Double, y: Double, z: Double) = MVec(this.x * x, this.y * y, this.z * z)
    fun mul(vec: MVec) = MVec(this.x * vec.x, this.y * vec.y, this.z * vec.z)
    fun mul(s: Double) = MVec(this.x * s, this.y * s, this.z * s)

    fun withX(s: Double) = MVec(s, y, z)
    fun withY(s: Double) = MVec(x, s, z)
    fun withZ(s: Double) = MVec(x, y, s)

    fun multiply(s: Double) = MVec(x * s, y * s, z * s)

    fun length() = sqrt(x * x + y * y + z * z)
    fun normalize(): MVec {
        val len = length()
        return MVec(x / length(), y / length(), z / length())
    }
}

val Entity.mLoc get() = MLoc(this.world, this.location.x, this.location.y, this.location.z)
val Entity.centerMLoc get() = MLoc(this.world, this.boundingBox.centerX, this.boundingBox.centerY, this.boundingBox.centerZ)
val LivingEntity.eyeMLoc get() = MLoc(this.world, this.eyeLocation.x, this.eyeLocation.y, this.eyeLocation.z)