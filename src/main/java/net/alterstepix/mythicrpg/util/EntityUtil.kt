package net.alterstepix.mythicrpg.util

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

fun Entity.pushFrom(location: MLoc, strength: Double = 1.0, extraY: Double = 0.0) {
    this.velocity = this.centerMLoc.mVec.sub(location.mVec).normalize().mul(strength).add(0.0, extraY, 0.0).vector
}

fun Entity.pushFromWithY(location: MLoc, strength: Double, y: Double) {
    this.velocity = this.centerMLoc.mVec.sub(location.mVec).normalize().mul(strength).withY(y).vector
}

fun Entity.pullTowards(location: MLoc, strength: Double = 1.0, extraY: Double = 0.0) {
    this.velocity = location.mVec.sub(this.centerMLoc.mVec).normalize().mul(strength).add(0.0, extraY, 0.0).vector
}

fun Entity.pullTowardsWithY(location: MLoc, strength: Double, y: Double) {
    this.velocity = location.mVec.sub(this.centerMLoc.mVec).normalize().mul(strength).withY(y).vector
}

fun LivingEntity.damageFrom(damage: Double, location: MLoc) {
    this.damage(damage)
    this.pushFromWithY(location, 0.4, 0.3)
}

fun LivingEntity.damageFrom(damage: Double, player: Player) {
    this.damage(damage)
    this.pushFromWithY(player.centerMLoc, 0.4, 0.3)
}