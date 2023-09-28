package net.alterstepix.mythicrpg.content.mob

import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.system.mob.MythicMob
import net.alterstepix.mythicrpg.util.EntityBuilder
import net.alterstepix.mythicrpg.util.setAI
import net.alterstepix.mythicrpg.util.setHealth
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Pig

@MythicContent class TrainingDummyMob: MythicMob(MobType.Boss) {
    override fun createMobBase(): EntityBuilder<out LivingEntity> {
        return EntityBuilder(Pig::class.java)
            .setAI(false)
            .setHealth(1000.0)
            .setDisplayName("#b0306d", "Training Dummy")
    }
}