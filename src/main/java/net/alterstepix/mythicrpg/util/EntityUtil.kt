package net.alterstepix.mythicrpg.util

import net.alterstepix.mythicrpg.MythicRPG
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import java.util.function.Predicate
import kotlin.math.min

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

    fun setData(key: String, value: String): EntityBuilder<T> {
        addModifier { entity ->
            entity.persistentDataContainer[NamespacedKey(MythicRPG.getInstance(), key), PersistentDataType.STRING] = value
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

fun <T: LivingEntity> EntityBuilder<T>.setSpeed(speed: Double): EntityBuilder<T> {
    addModifier { entity ->
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = speed
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setKnockbackResistance(value: Double): EntityBuilder<T> {
    addModifier { entity ->
        entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)?.baseValue = value
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setArmor(value: Double): EntityBuilder<T> {
    addModifier { entity ->
        entity.getAttribute(Attribute.GENERIC_ARMOR)?.baseValue = value
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setEquipment(helmet: Material = Material.AIR, chestplate: Material = Material.AIR, leggings: Material = Material.AIR, boots: Material = Material.AIR): EntityBuilder<T> {
    addModifier { entity ->
        if(helmet != Material.AIR) entity.equipment?.helmet = ItemStack(helmet).withUnbreakable().withAttribute(Attribute.GENERIC_ARMOR, 0.0, EquipmentSlot.HEAD)
        if(chestplate != Material.AIR) entity.equipment?.chestplate = ItemStack(helmet).withUnbreakable().withAttribute(Attribute.GENERIC_ARMOR, 0.0, EquipmentSlot.CHEST)
        if(leggings != Material.AIR) entity.equipment?.leggings = ItemStack(leggings).withUnbreakable().withAttribute(Attribute.GENERIC_ARMOR, 0.0, EquipmentSlot.LEGS)
        if(boots != Material.AIR) entity.equipment?.boots = ItemStack(boots).withUnbreakable().withAttribute(Attribute.GENERIC_ARMOR, 0.0, EquipmentSlot.FEET)
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setEquipment(helmet: ItemStack = ItemStack(Material.AIR), chestplate: ItemStack = ItemStack(Material.AIR), leggings: ItemStack = ItemStack(Material.AIR), boots: ItemStack = ItemStack(Material.AIR)): EntityBuilder<T> {
    addModifier { entity ->
        if(helmet.type != Material.AIR) entity.equipment?.helmet = helmet.withUnbreakable().withAttribute(Attribute.GENERIC_ARMOR, 0.0, EquipmentSlot.HEAD)
        if(chestplate.type != Material.AIR) entity.equipment?.chestplate = chestplate.withUnbreakable().withAttribute(Attribute.GENERIC_ARMOR, 0.0, EquipmentSlot.CHEST)
        if(leggings.type != Material.AIR) entity.equipment?.leggings = leggings.withUnbreakable().withAttribute(Attribute.GENERIC_ARMOR, 0.0, EquipmentSlot.LEGS)
        if(boots.type != Material.AIR) entity.equipment?.boots = boots.withUnbreakable().withAttribute(Attribute.GENERIC_ARMOR, 0.0, EquipmentSlot.FEET)
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setMainhand(itemMaterial: Material): EntityBuilder<T> {
    addModifier { entity ->
        entity.equipment?.setItemInMainHand(ItemStack(itemMaterial).withUnbreakable())
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setOffhand(itemMaterial: Material): EntityBuilder<T> {
    addModifier { entity ->
        entity.equipment?.setItemInOffHand(ItemStack(itemMaterial).withUnbreakable())
    }

    return this
}

fun <T: LivingEntity> EntityBuilder<T>.setAI(ai: Boolean): EntityBuilder<T> {
    addModifier { entity ->
        entity.setAI(ai)
    }
    return this
}

fun <T: LivingEntity> EntityBuilder<T>.addPotionEffect(vararg effects: EffectBuilder): EntityBuilder<T> {
    addModifier { entity ->
        for(effect in effects) {
            effect.apply(entity)
        }
    }
    return this
}

fun <T: LivingEntity> EntityBuilder<T>.addPersistentPotionEffect(potionEffectType: PotionEffectType, amplifier: Int = 1): EntityBuilder<T> {
    addModifier { entity ->
        EffectBuilder(potionEffectType)
            .withDuration(1.0E308)
            .withVisibility(false)
            .withAmplifier(amplifier)
            .apply(entity)
    }
    return this
}

fun Entity.getData(key: String): String? {
    return this.persistentDataContainer[NamespacedKey(MythicRPG.getInstance(), key), PersistentDataType.STRING]
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
    if(this.noDamageTicks > 0) return
    this.damage(damage)
    this.pushFromWithY(location, 0.4, 0.3)
}

fun LivingEntity.damageFrom(damage: Double, player: Player) {
    if(this.noDamageTicks > 0) return
    this.damage(damage)
    this.pushFromWithY(player.centerMLoc, 0.4, 0.3)
}

inline fun <reified T: Entity> LivingEntity.lookingAt(range: Double): T? {
    val result = this.world.rayTrace(this.eyeLocation, this.eyeLocation.direction, range, FluidCollisionMode.NEVER, true, 0.1, Predicate { entity -> entity != this && entity is T})
        ?: return null

    if(result.hitEntity != null && result.hitEntity is T) {
        return result.hitEntity as T
    }

    return null
}

fun Entity.makeSound(sound: Sound, volume: Float, pitch: Float) {
    this.world.playSound(this.location, sound, volume, pitch)
}

fun LivingEntity.increaseHealth(amount: Double) {
    this.health = min(this.health + amount, this.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0)
}