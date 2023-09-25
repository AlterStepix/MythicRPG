package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.util.EffectBuilder
import net.alterstepix.mythicrpg.util.ParticleBuilder
import net.alterstepix.mythicrpg.util.centerMLoc
import net.alterstepix.mythicrpg.util.withDisplayName
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType

class IdolsIncarnateItem: MythicItem() {
    init {
        registerItemEvent { event: MItemEvent.AttackLivingEntity ->
            if(event.player.attackCooldown < 0.9f) {
                event.player.world.playSound(event.player.location, Sound.ENTITY_WOLF_GROWL, 0.5f, 0.5f)
                return@registerItemEvent
            }

            val effect = listOf<PotionEffectType>(PotionEffectType.SLOW, PotionEffectType.POISON, PotionEffectType.WITHER)
            event.player.world.playSound(event.player.location, Sound.ENTITY_SPLASH_POTION_BREAK, 0.5f, 0.5f)

            EffectBuilder(effect.random())
                .withDuration(1.0 + Math.random() * 3)
                .withAmplifier((1..3).random())
                .apply(event.target)

            ParticleBuilder(Particle.SPELL_WITCH)
                .setCount(20)
                .setOffset(0.5)
                .display(event.target.centerMLoc)
        }
    }

    override fun createItemStackBase(): ItemStack {
        setColorScheme("#b64747", "#568ebf", "#8955be")
        return ItemStack(Material.NETHERITE_SWORD)
            .withDisplayName("#e74949", "Idols Incarnate")
            .withDamage(8.0)
            .withAttackSpeed(1.6)
            .withAbilityDescription(
                abilityName = "Idolic Curse", activation = "Attack",
                "Fully charged attacks have a chance",
                "to apply strong *debuffs* to your target.",
            )
    }
}