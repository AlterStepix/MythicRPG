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
import org.bukkit.util.Vector

@MythicContent class BerserkAxeItem: MythicItem() {
    init {
        registerItemEvent { event: MItemEvent.AttackLivingEntity ->
            event.target.pushFromWithY(event.player.centerMLoc, 0.7, 0.4)
            event.player.world.playSound(event.player.location, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.3f)
            ParticleBuilder(Particle.CRIT)
                .setCount(25)
                .setOffset(0.7)
                .setExtra(0.2)
                .display(event.target.centerMLoc)

            val delta = 20.0 - event.player.health
            if(delta <= 0.0) return@registerItemEvent
            event.damage = event.damage * (1.0 + delta / 40.0)
            ParticleBuilder(Particle.DUST_COLOR_TRANSITION)
                .setCount((delta * 2).toInt())
                .setData(DustTransition(Color.RED, Color.BLACK, 0.9f + delta.toFloat() / 40.0f))
                .displayCircle(event.target.centerMLoc, 1.4)
        }

        registerItemAbility(17500, autoCancel = true) { event: MItemEvent.RightClickEntity, _: AbilityContext ->
            if(event.target !is LivingEntity) return@registerItemAbility

            runParallelFor(4L, 8) { breakLoop, _ ->
                if(!event.target.isValid || event.player.lookingAt<LivingEntity>(5.0) != event.target) breakLoop()

                event.player.swingMainHand()
                event.target.world.playSound(event.target.location, Sound.ENTITY_ITEM_BREAK, 0.7f, 0.6f)

                particles(BLOCK_DUST, 10, offset = 0.4, data = Material.REDSTONE_WIRE.createBlockData()).display(event.target.centerMLoc)
                particles(BLOCK_DUST, 10, offset = 0.4, data = Material.REDSTONE_BLOCK.createBlockData()).display(event.target.centerMLoc)
                particles(ITEM_CRACK, 10, offset = 0.2, extra = 0.2, data = ItemStack(Material.BONE)).display(event.target.centerMLoc)
                particles(ITEM_CRACK, 10, offset = 0.2, extra = 0.2, data = ItemStack(Material.PORKCHOP)).display(event.target.centerMLoc)

                event.target.velocity = Vector(0.0, 0.18, 0.0)
                event.target.noDamageTicks = 0
                event.target.damage(2.0)
            }
        }
    }

    override fun createItemStackBase(): ItemStack {
        this.setColorScheme("#6371ad", "#d48d5f", "#d45f5f")
        return ItemStack(Material.STONE_AXE)
            .withDisplayName("#a18c67", "Berserk Axe")
            .withDamage(8.0)
            .withAttackSpeed(1.0)
            .withAbilityDescription(
                abilityName = "Rage",  activation = "Attack",
                "Attacking an enemy will deal additional *knockback* and",
                "additional *damage* for each heart you are",
                "missing below *20♥*. Up to +50% damage."
            )
            .withAbilityDescription(
                abilityName = "Rampage", activation = "Right Click Entity",
                "Perform a *rapid* sequence of 8 attacks",
                "dealing *2 damage* each. Automatically stops attacking",
                "once you are not looking at the target anymore."
            )
            .withCooldownLore(17500)
    }


}