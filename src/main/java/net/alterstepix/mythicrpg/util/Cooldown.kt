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
}

class CooldownMap(private val cooldownMs: Long) {
    private val cooldownMap = hashMapOf<Player, Cooldown>()

    fun isReady(player: Player) = this.cooldownMap.getOrPut(player, defaultValue = { Cooldown(cooldownMs) }).isReady()
    operator fun get(player: Player) = this.isReady(player)
}