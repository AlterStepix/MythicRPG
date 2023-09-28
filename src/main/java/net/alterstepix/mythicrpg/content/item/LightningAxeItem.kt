package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.config.ConfigValue
import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Particle.DustTransition
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType

@MythicContent class LightningAxeItem: MythicItem() {
    init {
        this.registerItemAbility(cooldownMs = 12500L, autoCancel = true) { event: MItemEvent.RightClick, ctx: AbilityContext ->
            val entities = searchEntitiesCylinder<LivingEntity>(event.player, 4.5)
            if(entities.isEmpty()) {
                ctx.cancel("No targets found within 4.5 blocks!")
            }

            ParticleBuilder(Particle.FIREWORKS_SPARK)
                .setCount(60)
                .displayCircle(event.player.mLoc.add(0.0, 0.1, 0.0), 4.5)

            event.player.world.playSound(event.player.location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 0.5f)

            for(entity in entities) {
                entity.world.strikeLightningEffect(entity.location)
                entity.damageFrom(6.0, event.player)

                ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                    .setCount(30)
                    .setData(DustTransition(Color.SILVER, Color.TEAL, 1.2f))
                    .displayCircle(entity.centerMLoc, 1.6)
            }
        }

        this.registerItemAbility(cooldownMs = 6000L, autoCancel = false, silent = true) { event: MItemEvent.AttackLivingEntity, _: AbilityContext ->
            event.target.world.strikeLightningEffect(event.target.location)
            event.player.world.playSound(event.player.location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 0.5f)

            ParticleBuilder(Particle.WAX_OFF)
                .setCount(30)
                .setOffset(0.5)
                .display(event.target.centerMLoc)

            EffectBuilder(PotionEffectType.SLOW)
                .withAmplifier(4)
                .withDuration(2.5)
                .apply(event.target)
        }
    }

    override fun createItemStackBase(): ItemStack {
        this.setColorScheme("#6371ad", "#d48d5f", "#d45f5f")
        return ItemStack(Material.IRON_AXE)
            .withDisplayName("#7c8ee6", "Lightning Axe")
            .withDamage(7.5)
            .withAttackSpeed(1.1)
            .withAbilityDescription(
                abilityName = "Thunderous Aura", activation = "Right Click",
                "Strikes all nearby entities with lightnings",
                            "dealing *6.0 damage*.",
            )
            .withCooldownLore(12500L)
            .withAbilityDescription(
                abilityName = "Thunderous Strike", activation = "Attack",
                "Strikes your target with lightning",
                            "applying *slowness effect* to it.",
            )
            .withCooldownLore(6000L)
    }
}