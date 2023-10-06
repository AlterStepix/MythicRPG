package net.alterstepix.mythicrpg.system.mob

import net.alterstepix.mythicrpg.MythicRPG
import net.alterstepix.mythicrpg.system.ingredient.IngredientManager
import net.alterstepix.mythicrpg.system.event.EventManager
import net.alterstepix.mythicrpg.system.event.mob.MMobEvent
import net.alterstepix.mythicrpg.system.manager.Identifiable
import net.alterstepix.mythicrpg.util.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

abstract class MythicMob<E: LivingEntity>(private val type: MobType): Identifiable {
    private val abilities = mutableListOf<() -> Ability<E>>()
    private val drops = mutableListOf<IngredientManager.Ingredient>()
    protected val mobs = mutableListOf<E>()

    init {
        registerMobEvent<MMobEvent.Death> { event ->
            for(drop in drops) {
                event.drops.addAll(drop.drop())
            }
        }
    }

    enum class MobType(val title: String) {
        MOB("${hex("#59a343")}§lMOB"),
        ELITE("${hex("#bdb277")}§lELITE"),
        BOSS("${hex("#ea6363")}§lBOSS")
    }

    class Ability<E: LivingEntity>(val cooldown: RandomCooldown, val handler: (E) -> Unit)

    fun clearMobs() {
        for(mob in mobs) {
            mob.remove()
        }

        mobs.clear()
    }

    abstract fun createMobBase(): EntityBuilder<E>

    protected inline fun <reified T: MMobEvent> registerMobEvent(crossinline handler: (T) -> Unit) {
        EventManager.register(lambda = { event: T ->
            if(mobs.contains(event.entity)) {
                handler(event)
            }
        })
    }

    protected fun registerMobAbility(cooldownTicks: IntRange, handler: (E) -> Unit) {
        this.abilities.add { Ability(RandomCooldown((cooldownTicks.first * 50L)..(cooldownTicks.last * 50L)), handler) }
    }

    protected fun registerDrops(vararg drops: IngredientManager.Ingredient) {
        this.drops.addAll(drops)
    }

    class MythicMobInstance<E: LivingEntity>(private val mob: E, private val mythicMob: MythicMob<E>): BukkitRunnable() {
        private val displayName = mob.customName
        private val abilities = mythicMob.abilities.map { supplier -> supplier() }

        override fun run() {
            if(!mob.isValid || mob.isDead) {
                mythicMob.mobs.remove(mob)
                cancel(); return
            }
            mob.customName = displayName + " ${hex("#a23434")}${mob.health.format(1)}♥ ${hex("#a6e0af")}${mob.getAttribute(Attribute.GENERIC_ARMOR)?.value?.format(1)}⚓"
            for(ability in abilities) {
                if(ability.cooldown.isReady()) {
                    ability.handler(mob)
                }
            }
        }
    }

    fun create(location: MLoc): E {
        val entity = createMobBase()
            .setData("mythic-mob", getIdentifier())
            .setCustomNameVisible()
            .spawn(location)

        mobs.add(entity)
        MythicMobInstance(entity, this).runTaskTimer(MythicRPG.getInstance(), 0L, 1L)

        return entity
    }

    protected fun <T: Entity> EntityBuilder<T>.setDisplayName(color: String, displayName: String): EntityBuilder<T> {
        addModifier { entity ->
            entity.customName = "${type.title} ${hex(color)}$displayName"
        }
        return this
    }

    protected fun <T: Entity> EntityBuilder<T>.addMobEffect(vararg type: PotionEffectType, amplifier: Int = 1): EntityBuilder<T> {
        addModifier { entity ->
            if (entity is LivingEntity)
                type.forEach {
                    EffectBuilder(it)
                        .withDuration(1.0E308)
                        .withVisibility(false)
                        .withAmplifier(amplifier)
                        .apply(entity)
                }
        }
        return this
    }
}