package net.alterstepix.mythicrpg.util

import org.bukkit.entity.Player

class Cooldown(private val cooldownMs: Long) {
    private var lastMs = 0L

    fun isReady(): Boolean {
        if(System.currentTimeMillis() - lastMs >= cooldownMs) {
            lastMs = System.currentTimeMillis()
            return true
        }
        return false
    }

    fun reset() {
        this.lastMs = 0L
    }

    fun update() {
        this.lastMs = System.currentTimeMillis()
    }

    val remainingTime: Long get() = -(System.currentTimeMillis() - lastMs - cooldownMs)
    val progress: Double get() = 1.0 + (System.currentTimeMillis() - lastMs - cooldownMs).toDouble() / cooldownMs.toDouble()
}

class CooldownMap(private val cooldownSupplier: () -> Cooldown) {
    constructor(cooldownMs: Long) : this(cooldownSupplier = { Cooldown(cooldownMs) })
    private val cooldownMap = hashMapOf<Player, Cooldown>()

    fun isReady(player: Player) = this.cooldownMap.getOrPut(player, cooldownSupplier).isReady()
    operator fun get(player: Player) = this.isReady(player)

    fun timeOf(player: Player) = this.cooldownMap.getOrPut(player, cooldownSupplier).remainingTime
    fun progressOf(player: Player) = this.cooldownMap.getOrPut(player, cooldownSupplier).progress
    fun reset(player: Player) = this.cooldownMap.getOrPut(player, cooldownSupplier).reset()
    fun update(player: Player) = this.cooldownMap.getOrPut(player, cooldownSupplier).update()
}