package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import kotlin.math.PI

@MythicContent class FrozenWandItem: MythicItem() {
    init {
        registerItemAbility(9000L, autoCancel = false) { event: MItemEvent.RightClick, _: AbilityContext ->
            PathTracer(30.0)
                .withDensity(4.0)
                .addFinalHandler { data ->
                    repeat(3) { i ->
                        particles(Particle.CRIT_MAGIC, 50, 0.0, 0.0).displayCircle(data.location, (i + 1) * 0.7, transform = { vec -> vec.transform(data.direction) })
                    }
                }
                .addIterationHandler { data ->
                    val mobs = searchEntities<LivingEntity>(data.location, 2.0).filter { e -> e != event.player }

                    for(mob in mobs) {
                        mob.damageFrom(5.0, event.player)
                        potionEffect(PotionEffectType.SLOW, 4.0, 3).apply(mob)
                        particles("#3c6e75", "#48b6bd", 10, 1.2f, 1.0).display(mob.centerMLoc)
                    }

                    particles("#a8e7ff", "#6ab6f4", 10, 1.2f, 0.0).displayCircle(data.location, 0.4, transform = { vec -> vec.transform(data.direction) })

                    return@addIterationHandler true
                }
                .traceParallel(event.player.eyeMLoc, event.player.eyeMDir, 1L, 6)
        }
    }

    override fun createItemStackBase(): ItemStack {
        this.setColorScheme("#3c6e75", "#48b6bd", "#8ab464")
        return ItemStack(Material.DIAMOND_SHOVEL)
            .withDisplayName("#48b6bd", "Frostbolt Wand")
            .withDamage(4.5)
            .withAttackSpeed(0.9)
            .withAbilityDescription(
                abilityName = "Frostbolt", activation = "Right Click",
                "Launches a fast projectile",
                "dealing *5.0 damage* on impact and freezing its targets.",
            )
            .withCooldownLore(9000L)
    }
}