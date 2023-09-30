package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.event.MEvent
import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

@MythicContent class AirBurnerItem : MythicItem() {

    init {
        registerItemAbility(cooldownMs = 13000L, autoCancel = true) { event : MItemEvent.RightClick, abilityContext ->
            val entities = searchEntities<LivingEntity>(event.player, 10.0).toMutableList()
            if (entities.isEmpty()) {
                entities+=event.player
                burnEntity(event, entities)
                return@registerItemAbility
            }

            particles(type = Particle.SMOKE_LARGE, count = 100, extra = 0.1).setForce(true).displaySphere(event.player.centerMLoc, 5.0)

            burnEntity(event, entities)

        }

    }
    override fun createItemStackBase(): ItemStack {
        setColorScheme("#F5AA47", "#0F72A8", "#47B7F5")
        return ItemStack(Material.FLINT_AND_STEEL)
            .withDisplayName("#F56E3B", "Air Burner")
            .withDamage(0.5)
            .withAttackSpeed(3.0)
            .withAbilityDescription(
                abilityName = "Hot Air",  activation = "Right Click",
                "Attacks entities within a radius of *10* blocks,",
                "dealing fire damage to them over *6.5* seconds,",
                "but if they are *absent*, it attacks you!"
            )
    }

    private fun burnEntity(event: MItemEvent.RightClick, entities: List<LivingEntity>) {
        val entities = entities.toMutableList()

        // Visual fire block
        for (entity in entities) {
            entity.setVisualFire(true)
            particles(type =  Particle.LAVA, count = 15).display(entity.centerMLoc)
        }
        runLater(128) {
            for (entity in entities) {
                entity.setVisualFire(false)
            }
        }

        // Damage block
        for (entity in entities) {
            runParallelFor(16L, 8) { _, _ ->
                particles(type =  Particle.SMOKE_LARGE, count = 8, extra = 0.4).display(entity.eyeMLoc)
                particles(type =  Particle.ITEM_CRACK, count = 15, data =  ItemStack(Material.COOKED_PORKCHOP), extra = 0.3).display(entity.eyeMLoc)
                entity.makeSound(Sound.BLOCK_FIRE_AMBIENT, 2.5f, 0.0f)
                entity.noDamageTicks = 0
                entity.damage(1.0)
            }
        }

    }
}