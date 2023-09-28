package net.alterstepix.mythicrpg.system.mob

import net.alterstepix.mythicrpg.MythicRPG
import net.alterstepix.mythicrpg.system.manager.Identifiable
import net.alterstepix.mythicrpg.util.EntityBuilder
import net.alterstepix.mythicrpg.util.MLoc
import net.alterstepix.mythicrpg.util.format
import net.alterstepix.mythicrpg.util.hex
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitRunnable

abstract class MythicMob(val type: MobType): Identifiable {
    enum class MobType(val title: String) {
        Mob("${hex("#59a343")}§lMOB"),
        Elite("${hex("#bdb277")}§lELITE"),
        Boss("${hex("#ea6363")}§lBOSS")
    }

    abstract fun createMobBase(): EntityBuilder<out LivingEntity>

    class MythicMobInstance(private val mob: LivingEntity): BukkitRunnable() {
        private val displayName = mob.customName

        override fun run() {
            if(!mob.isValid || mob.isDead) {
                cancel(); return
            }
            mob.customName = displayName + " ${hex("#a23434")}${mob.health.format(1)}♥"
        }
    }

    fun create(location: MLoc): LivingEntity {
        val entity = createMobBase()
            .setCustomNameVisible()
            .spawn(location)

        MythicMobInstance(entity).runTaskTimer(MythicRPG.getInstance(), 0L, 1L)

        return entity
    }

    protected fun <T: Entity> EntityBuilder<T>.setDisplayName(color: String, displayName: String): EntityBuilder<T> {
        addModifier { entity ->
            entity.customName = "${type.title} ${hex(color)}$displayName"
        }
        return this
    }
}