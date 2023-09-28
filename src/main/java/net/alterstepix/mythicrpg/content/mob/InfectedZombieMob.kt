package net.alterstepix.mythicrpg.content.mob

import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.system.mob.MythicMob
import net.alterstepix.mythicrpg.util.EntityBuilder
import net.alterstepix.mythicrpg.util.setEquipment
import net.alterstepix.mythicrpg.util.setHealth
import net.alterstepix.mythicrpg.util.setMainhand
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie

@MythicContent class InfectedZombieMob: MythicMob(MobType.Mob) {
    override fun createMobBase(): EntityBuilder<out LivingEntity> {
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