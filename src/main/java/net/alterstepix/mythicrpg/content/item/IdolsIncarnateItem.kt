package net.alterstepix.mythicrpg.content.item

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

@MythicContent class IdolsIncarnateItem: MythicItem() {
    private val effects =  listOf<PotionEffectType>(
        PotionEffectType.SLOW, PotionEffectType.POISON, PotionEffectType.WITHER,
        PotionEffectType.BLINDNESS, PotionEffectType.SLOW_DIGGING, PotionEffectType.CONFUSION,
        PotionEffectType.WEAKNESS, PotionEffectType.HUNGER
    )
    init {
        registerItemAbility(7500, autoCancel = true) { event: MItemEvent.RightClick, _: AbilityContext ->
            PathTracer(20.0)
                .withDensity(3.0)
                .addIterationHandler { data ->
                    if(!data.location.block.isPassable) {
                        return@addIterationHandler false
                    }

                    data.direction = data.direction.add(random(-0.1..0.1), random(-0.1..0.1), random(-0.1..0.1)).normalize()
                    ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                        .setData(DustTransition(Color.GRAY, Color.BLACK, 1.2f))
                        .display(data.location)

                    val entities = searchEntities<LivingEntity>(data.location, 2.0).filter { entity -> entity != event.player }

                    if(entities.isNotEmpty()) {
                        val entity = entities.random()
                        entity.damageFrom(8.0, event.player)
                        ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                            .setOffset(1.0)
                            .setCount(40)
                            .setData(DustTransition(Color.RED, Color.BLACK, 1.2f))
                            .display(entity.centerMLoc)
                        EffectBuilder(PotionEffectType.BLINDNESS)
                            .withDuration(2.5)
                            .apply(entity)
                    }

                    return@addIterationHandler entities.isEmpty()
                }
                .addFinalHandler { data ->
                    ParticleBuilder(Particle.SPELL_WITCH)
                        .setOffset(0.2)
                        .setCount(20)
                        .display(data.location)
                }
                .traceParallelMultiple(3, event.player.eyeMLoc, event.player.eyeMDir, 1L, 3L)
        }

        registerItemEvent { event: MItemEvent.AttackLivingEntity ->
            if(event.player.attackCooldown < 0.9f) {
                event.player.world.playSound(event.player.location, Sound.ENTITY_WOLF_GROWL, 0.5f, 0.5f)
                return@registerItemEvent
            }

            if(random() < 0.4) {
                event.player.world.playSound(event.player.location, Sound.ENTITY_ITEM_BREAK, 0.5f, 0.5f)
                return@registerItemEvent
            }

            event.player.world.playSound(event.player.location, Sound.ENTITY_SPLASH_POTION_BREAK, 0.5f, 0.5f)

            EffectBuilder(effects.random())
                .withDuration(random(1.0..3.0))
                .withAmplifier((1..3).random())
                .apply(event.target)

            ParticleBuilder(Particle.SPELL_WITCH)
                .setCount(20)
                .setOffset(0.5)
                .display(event.target.centerMLoc)

            for(i in 1L .. 2L) {
                runLater(i * 5) {
                    ParticleBuilder(Particle.SOUL)
                        .setOffset(0.1, 0.2, 0.1)
                        .setCount(12)
                        .displayCircle(event.target.eyeMLoc, 1.1)
                }
            }
        }
    }

    override fun createItemStackBase(): ItemStack {
        setColorScheme("#b64747", "#568ebf", "#8955be")
        return ItemStack(Material.NETHERITE_SWORD)
            .withDisplayName("#e74949", "Idols Incarnate")
            .withDamage(8.0)
            .withAttackSpeed(1.6)
            .withAbilityDescription(
                abilityName = "Darkness Bolts", activation = "Right Click",
                "Shoots 3 fast projectiles",
                "dealing *8.0 damage* each and blinding their target.",
            )
            .withCooldownLore(7500)
            .withAbilityDescription(
                abilityName = "Idolic Curse", activation = "Attack",
                "Fully charged attacks have a chance",
                "to apply strong *debuffs* to your target.",
            )
    }
}