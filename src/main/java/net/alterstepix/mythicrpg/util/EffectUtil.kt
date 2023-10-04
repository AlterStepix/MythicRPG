package net.alterstepix.mythicrpg.util

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EffectBuilder(val type: PotionEffectType, var duration: Int = 20, var amplifier: Int = 0) {
    private var visible = false

    fun withDuration(duration: Double): EffectBuilder {
        this.duration = (duration * 20.0).toInt()
        return this
    }

    fun withAmplifier(amplifier: Int): EffectBuilder {
        this.amplifier = amplifier - 1
        return this
    }

    fun withVisibility(visible: Boolean = true): EffectBuilder {
        this.visible = visible
        return this
    }

    fun apply(entity: LivingEntity) {
        entity.addPotionEffect(PotionEffect(type, duration, amplifier, visible, visible, visible))
    }

    fun apply(entity: Collection<LivingEntity>) {
        entity.forEach(::apply)
    }

    fun apply(vararg entity: LivingEntity) {
        entity.forEach(::apply)
    }
}

fun potionEffect(type: PotionEffectType, duration: Double, amplifier: Int = 1, visible: Boolean = false) =
    EffectBuilder(type)
        .withDuration(duration)
        .withAmplifier(amplifier)
        .withVisibility(visible)