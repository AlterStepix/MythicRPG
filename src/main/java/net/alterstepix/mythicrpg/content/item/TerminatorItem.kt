package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Particle.*
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import kotlin.math.PI

@MythicContent class TerminatorItem: MythicItem() {
    init {
        registerItemEvent { event: MItemEvent.RightClick, ->
            event.isCancelled = true
        }

        registerItemAbility(1500) { event: MItemEvent.RightClick, _: AbilityContext ->
            event.player.setCooldown(Material.FISHING_ROD, 30)

            event.player.makeSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.0f)
            for(i in 0 until 3) {
                PathTracer(30.0)
                    .withDensity(4.0)
                    .addFinalHandler { data ->
                        particles(CRIT_MAGIC, 20, 0.5, 0.3).display(data.location)
                    }
                    .addIterationHandler { data ->
                        val mobs = checkEntities<LivingEntity>(data.location).filter { e -> e != event.player }

                        for(mob in mobs) {
                            mob.damageFrom(5.0, event.player)
                            particles("#7d7cea", "#61a190", 10, 1.2f, 1.0).display(mob.centerMLoc)
                        }

                        particles(DUST_COLOR_TRANSITION, 1, data = DustTransition(Color.fromRGB(0x6cbedc), Color.fromRGB(0x6bdbd5), 1.0f)).display(data.location)

                        return@addIterationHandler true
                    }
                    .traceParallel(event.player.centerMLoc, event.player.eyeMDir.rotY((i - 1) * PI / 6), 1L, 6)
            }
        }
    }

    override fun createItemStackBase(): ItemStack {
        setColorScheme("#468095", "#5dcfcb", "#cfad5d")
        return ItemStack(Material.FISHING_ROD)
            .withDamage(5.5)
            .withAttackSpeed(0.8)
            .withDisplayName("#cfad5d", "Terminator")
            .withAbilityDescription(
                abilityName = "Termination", activation = "Right Click",
                "Shoots 3 beams of energy",
                "dealing *5.0 damage* each."
            )
            .withCooldownLore(1500)
    }
}