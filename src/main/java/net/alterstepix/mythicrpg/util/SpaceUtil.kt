package net.alterstepix.mythicrpg.util

import net.alterstepix.mythicrpg.MythicRPG
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class MLoc(val world: World, val x: Double, val y: Double, val z: Double) {
    val location: Location get() = Location(world, x, y, z)
    val vector: Vector get() = Vector(x, y, z)
    val mVec: MVec get() = MVec(x, y, z)
    val block: Block get() = world.getBlockAt(location)
    val blockType: Material get() = world.getBlockAt(location).type

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
        return MVec(x / len, y / len, z /len)
    }

    fun rotX(t: Double) = MVec(x, y * cos(t) - z * sin(t), y * sin(t) + z * cos(t))
    fun rotY(t: Double) = MVec(x * cos(t) + z * sin(t), y, -x * sin(t) + z * cos(t))
    fun rotZ(t: Double) = MVec(x * cos(t) - y * sin(t), x * sin(t) + y * cos(t), z)

    fun cross(vec: MVec) = MVec(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x)
}

val Entity.mLoc get() = MLoc(this.world, this.location.x, this.location.y, this.location.z)
val Entity.centerMLoc get() = MLoc(this.world, this.boundingBox.centerX, this.boundingBox.centerY, this.boundingBox.centerZ)
val LivingEntity.eyeMLoc get() = MLoc(this.world, this.eyeLocation.x, this.eyeLocation.y, this.eyeLocation.z)

val Entity.mDir get() = MVec(this.location.direction.x, this.location.direction.y, this.location.direction.z)
val LivingEntity.eyeMDir get() = MVec(this.eyeLocation.direction.x, this.eyeLocation.direction.y, this.eyeLocation.direction.z)

class PathTracer(private val length: Double) {
    data class Data(
        var location: MLoc,
        var direction: MVec
    )

    private var density: Double = 4.0
    private var collideBlock: Boolean = true
    private val iterHandlers = mutableListOf<(Data) -> Boolean>()
    private val endHandlers = mutableListOf<(Data) -> Unit>()

    fun withDensity(density: Double): PathTracer {
        this.density = density
        return this
    }

    fun addIterationHandler(handler: (Data) -> Boolean): PathTracer {
        this.iterHandlers.add(handler)
        return this
    }

    fun addFinalHandler(handler: (Data) -> Unit): PathTracer {
        this.endHandlers.add(handler)
        return this
    }

    private fun trace(location: MLoc, direction: MVec, caller: (((Int) -> Boolean), Int) -> Unit) {
        val data = Data(location.copy(), direction.normalize().copy())
        val iterations = density * length

        fun advance(i: Int): Boolean {
            if(collideBlock && !data.location.block.isPassable) {
                for(h in endHandlers) { h(data) }
                return false
            }
            for(ih in iterHandlers) { if(!ih(data)) { return false } }
            data.location = data.location.add(data.direction.mul(1.0 / density))

            if(i == iterations.toInt() - 1) {
                for(h in endHandlers) { h(data) }
                return false
            }

            return true
        }

        caller(::advance, iterations.toInt())
    }

    fun traceSync(location: MLoc, direction: MVec) = trace(location, direction) { advance: (Int) -> Boolean, iter: Int ->
        for(i in 0 until iter) {
            if(!advance(i)) {
                return@trace
            }
        }
    }

    fun traceParallel(location: MLoc, direction: MVec, delay: Long, extraIter: Long = 1) = trace(location, direction) { advance: (Int) -> Boolean, iter: Int ->
        object: BukkitRunnable() {
            var i = 0
            override fun run() {
                for(j in 0 until extraIter) {
                    if(i >= iter || !advance(i)) {
                        cancel()
                        return
                    }
                    i++
                }
            }
        }.runTaskTimer(MythicRPG.getInstance(), 0L, delay)
    }

    fun traceSyncMultiple(rays: Int, location: MLoc, direction: MVec) {
        for(i in 0 until rays) {
            traceSync(location, direction)
        }
    }

    fun traceParallelMultiple(rays: Int, location: MLoc, direction: MVec, delay: Long, extraIter: Long = 1) {
        for(i in 0 until rays) {
            traceParallel(location, direction, delay, extraIter)
        }
    }
}