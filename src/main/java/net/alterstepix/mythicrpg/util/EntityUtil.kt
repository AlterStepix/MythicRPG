package net.alterstepix.mythicrpg.util

import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

open class EntityBuilder<T: Entity>(val entityClass: Class<out T>) {
    private val modifiers: MutableList<(T) -> Unit> = mutableListOf()

    fun addModifier(modifier: (T) -> Unit) {
        modifiers.add(modifier)
    }

    fun spawn(location: MLoc): T {
        return location.world.spawn(location.location, entityClass, false) { entity ->
            for(modifier in modifiers) {
                modifier(entity)
            }
        }
    }

    fun setCustomNameVisible(visible: Boolean = true): EntityBuilder<T> {
        addModifier { entity ->
            entity.isCustomNameVisible = visible
        }
        return this
    }
}

fun <T: LivingEntity> EntityBuilder<T>.setHealth(health: Double): EntityBuilder<T> {
    addModifier { entity ->
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = health
        entity.health = health
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setEquipment(helmet: Material = Material.AIR, chestplate: Material = Material.AIR, leggings: Material = Material.AIR, boots: Material = Material.AIR): EntityBuilder<T> {
    addModifier { entity ->
        if(helmet != Material.AIR) entity.equipment?.helmet = ItemStack(helmet).withUnbreakable()
        if(chestplate != Material.AIR) entity.equipment?.chestplate = ItemStack(helmet).withUnbreakable()
        if(leggings != Material.AIR) entity.equipment?.leggings = ItemStack(leggings).withUnbreakable()
        if(boots != Material.AIR) entity.equipment?.boots = ItemStack(boots).withUnbreakable()
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setMainhand(itemMaterial: Material): EntityBuilder<T> {
    addModifier { entity ->
        entity.equipment?.setItemInMainHand(ItemStack(itemMaterial).withUnbreakable())
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setAI(ai: Boolean): EntityBuilder<T> {
    addModifier { entity ->
        entity.setAI(ai)
    }
    return this
}

fun Entity.pushFrom(location: MLoc, strength: Double = 1.0, extraY: Double = 0.0) {
    this.velocity = this.velocity.add(this.centerMLoc.mVec.sub(location.mVec).normalize().mul(strength).add(0.0, extraY, 0.0).vector)
}

fun Entity.pushFromWithY(location: MLoc, strength: Double, y: Double) {
    this.velocity = this.velocity.add(this.centerMLoc.mVec.sub(location.mVec).normalize().mul(strength).withY(y).vector)
}

fun Entity.pullTowards(location: MLoc, strength: Double = 1.0, extraY: Double = 0.0) {
    this.velocity = this.velocity.add(location.mVec.sub(this.centerMLoc.mVec).normalize().mul(strength).add(0.0, extraY, 0.0).vector)
}

fun Entity.pullTowardsWithY(location: MLoc, strength: Double, y: Double) {
    this.velocity = this.velocity.add(location.mVec.sub(this.centerMLoc.mVec).normalize().mul(strength).withY(y).vector)
}

fun LivingEntity.damageFrom(damage: Double, location: MLoc) {
    this.damage(damage)
    this.pushFromWithY(location, 0.4, 0.3)
}

fun LivingEntity.damageFrom(damage: Double, player: Player) {
    this.damage(damage)
    this.pushFromWithY(player.centerMLoc, 0.4, 0.3)
}

inline fun <reified T: Entity> LivingEntity.lookingAt(range: Double): T? {
    val result = this.world.rayTrace(this.eyeLocation, this.eyeLocation.direction, range, FluidCollisionMode.NEVER, true, 0.1, Predicate { entity -> entity != this && entity is T})
        ?: return null

    Bukkit.getLogger().info("ray traced: he = ${result.hitEntity}; hb = ${result.hitBlock}")

    if(result.hitEntity != null && result.hitEntity is T) {
        return result.hitEntity as T
    }

    return null
}