package net.alterstepix.mythicrpg.content.mob

import net.alterstepix.mythicrpg.system.event.mob.MMobEvent
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.system.mob.MythicMob
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie

@MythicContent class InfectedZombieMob: MythicMob<Zombie>(MobType.MOB) {
    init {
        registerMobEvent { event: MMobEvent.AttackEntity ->
            particles(Particle.HEART, 4, 0.3, 0.4).display(event.entity.eyeMLoc)
            event.entity.world.playSound(event.entity.location, Sound.ENTITY_WITCH_DRINK, 0.5f, 0.5f)

            event.entity.increaseHealth(4.0)
        }
    }

    override fun createMobBase(): EntityBuilder<Zombie> {
        return EntityBuilder(Zombie::class.java)
            .setDisplayName("#4bb030", "Infected Zombie")
            .setHealth(15.0)
            .setEquipment(
                helmet = Material.LEATHER_HELMET,
                leggings = Material.LEATHER_LEGGINGS,
                boots = Material.LEATHER_BOOTS
            )
            .setMainhand(Material.STONE_SHOVEL)
    }
}