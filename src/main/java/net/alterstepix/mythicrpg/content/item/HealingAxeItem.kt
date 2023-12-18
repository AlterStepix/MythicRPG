package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import kotlin.math.min

@MythicContent class HealingAxeItem: MythicItem() {

    init {
        registerItemAbility(cooldownMs = 5000L, autoCancel = true) { event : MItemEvent.RightClick, context: AbilityContext ->
            if(event.player.health >= (event.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0)) {
                context.cancel("You are too healthy to use this item!")
            }

            if(event.player.foodLevel < 8.0) {
                context.cancel("You are too hungry to use this item!")
            }

            event.player.health = event.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0
            event.player.foodLevel -= 7

            event.player.makeSound(Sound.BLOCK_HONEY_BLOCK_BREAK, 3.5f, 0.0f)
            particles(Particle.ITEM_CRACK, 25, extra = 0.25, data = ItemStack(Material.ROTTEN_FLESH)).display(event.player.centerMLoc)
            particles(Particle.HEART, 4, extra = 0.3, offset = 0.4).display(event.player.eyeMLoc)

        }

        registerItemEvent { event: MItemEvent.AttackLivingEntity ->
            if (event.player.attackCooldown < 0.9) {
                potionEffect(PotionEffectType.CONFUSION, 8.0, 5).apply(event.player)
                return@registerItemEvent
            }

            particles(Particle.ITEM_CRACK, 25, extra = 0.25, data = ItemStack(Material.ROTTEN_FLESH)).display(event.target.centerMLoc)

            if (event.target is Player && event.target.foodLevel >= 2.0 ) {
                event.target.foodLevel -= 2
            } else {
                event.target.damage(4.0)
            }

            if (event.player.foodLevel >= 20.0) {
                potionEffect(PotionEffectType.SPEED, 5.0, 2).apply(event.player)
                particles(Particle.VILLAGER_HAPPY, 20, extra = 0.3, offset = 0.4).display(event.player.centerMLoc)
            } else {
                event.player.foodLevel = min(event.player.foodLevel+1, 20)
                particles(Particle.TOTEM, 20, extra = 0.2).display(event.player.centerMLoc)
            }

        }


    }
    override fun createItemStackBase(): ItemStack {
        setColorScheme("#A39F1A", "#12705D", "#1CB092")
        return ItemStack(Material.GOLDEN_AXE)
            .withDisplayName("#D4F003", "Healing Axe")
            .withDamage(1.0)
            .withAttackSpeed(0.5)
            .withAbilityDescription(
                abilityName = "Healing by hunger",  activation = "Right Click",
                "Heals *you* completely,",
                "but learn that *after* the treatment",
                "you will be *extremely hungry*!"
            )
            .withAbilityDescription(
                abilityName = "Let's eat",  activation = "Attack",
                "Every hit *saturates* you with your enemies!"
            )
    }
}