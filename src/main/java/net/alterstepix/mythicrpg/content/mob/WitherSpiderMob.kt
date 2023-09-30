package net.alterstepix.mythicrpg.content.mob

import net.alterstepix.mythicrpg.system.event.mob.MMobEvent
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.system.mob.MythicMob
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Spider
import org.bukkit.entity.Wither
import org.bukkit.potion.PotionEffectType

@MythicContent class WitherSpiderMob: MythicMob<Spider>(MobType.MOB) {
    init {
        registerMobEvent { event: MMobEvent.AttackEntity ->
            particles(Particle.CAMPFIRE_COSY_SMOKE, 4, 0.3, 0.2).display(event.entity.eyeMLoc)
            event.entity.world.playSound(event.entity.location, Sound.ENTITY_WITHER_SHOOT, 0.5f, 0.5f)

            EffectBuilder(PotionEffectType.WITHER)
                .withAmplifier(2)
                .withDuration(4.0)
                .apply(event.target)
        }

        registerMobAbility(40..120) { entity ->
            val target = entity.target ?: return@registerMobAbility

            particles(Particle.EXPLOSION_LARGE, 2, 1.2, 0.5).display(entity.centerMLoc)
            entity.pullTowards(target.centerMLoc, strength = 1.2, extraY = 0.4)
            entity.world.playSound(entity.location, Sound.ENTITY_WITHER_SHOOT, 0.5f, -0.5f)
        }
    }

    override fun createMobBase(): EntityBuilder<Spider> {
        return EntityBuilder(Spider::class.java)
            .setDisplayName("#462352", "Infected Zombie")
            .setHealth(12.0)
    }
}