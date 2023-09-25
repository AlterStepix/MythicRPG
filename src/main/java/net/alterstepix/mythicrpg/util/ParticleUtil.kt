package net.alterstepix.mythicrpg.util

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ParticleBuilder(private val particle: Particle) {
    var offsetX = 0.0
    var offsetY = 0.0
    var offsetZ = 0.0

    var count: Int = 1
    var extra: Double = 0.0
    var data: Any? = null
    var force: Boolean = true

    fun setOffset(offsetX: Double, offsetY: Double, offsetZ: Double): ParticleBuilder {
        this.offsetX = offsetX
        this.offsetY = offsetY
        this.offsetZ = offsetZ
        return this
    }

    fun setOffset(offset: Double): ParticleBuilder {
        this.offsetX = offset
        this.offsetY = offset
        this.offsetZ = offset
        return this
    }

    fun setCount(count: Int): ParticleBuilder {
        this.count = count
        return this
    }

    fun setExtra(double: Double): ParticleBuilder {
        this.extra = double
        return this
    }

    fun setData(data: Any): ParticleBuilder {
        this.data = data
        return this
    }

    fun setForce(boolean: Boolean): ParticleBuilder {
        this.force = boolean
        return this
    }

    fun display(location: MLoc) {
        location.world.spawnParticle(particle, location.location, count, offsetX, offsetY, offsetZ, extra, data, force)
    }

    private fun display(location: MLoc, count: Int) {
        location.world.spawnParticle(particle, location.location, count, offsetX, offsetY, offsetZ, extra, data, force)
    }

    private fun displayCircle(location: MLoc, quality: Int, count: Int, radius: Double, transform: (MVec) -> MVec = { it }) {
        for(i in 0 until quality) {
            val t = i.toDouble() / this.count.toDouble()
            val vec = MVec(sin(t * PI * 2), 0.0, cos(t * PI * 2)).multiply(radius)
            val loc = location.add(transform(vec))
            display(loc, count)
        }
    }

    fun displayCircle(location: MLoc, radius: Double, transform: (MVec) -> MVec = { it }) {
        this.displayCircle(location, this.count, 1, radius, transform)
    }

    fun displayCircle(location: MLoc, quality: Int, radius: Double, transform: (MVec) -> MVec = { it }) {
        this.displayCircle(location, quality, this.count, radius, transform)
    }
}