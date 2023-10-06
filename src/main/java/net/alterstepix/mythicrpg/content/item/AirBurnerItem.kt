package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

@MythicContent class AirBurnerItem: MythicItem() {
    init {
        registerItemAbility(cooldownMs = 13000L, autoCancel = true) { event : MItemEvent.RightClick, _ ->
            val entities = searchEntities<LivingEntity>(event.player, 10.0)
            if (entities.isEmpty()) {
                burnEntity(mutableListOf(event.player), 1.0, 8)
                return@registerItemAbility
            }

            particles(Particle.SMOKE_LARGE, 200, extra = 0.1).setForce(true).displaySphere(event.player.centerMLoc, 5.0)
            burnEntity(entities, 1.0, 8)

            if(Random.nextDouble() <= 0.10){
                burnEntity(mutableListOf(event.player), 0.25, 4)
            }

        }

        registerItemEvent {event: MItemEvent.AttackLivingEntity ->
            if(event.player.attackCooldown < 1.0) {
                particles(Particle.SMOKE_LARGE, 8, extra = 0.4).display(event.player.eyeMLoc)
                return@registerItemEvent
            }

            particles(Particle.ITEM_CRACK, 15, data = ItemStack(Material.FLINT_AND_STEEL), extra = 0.3).display(event.target.eyeMLoc)
            event.target.makeSound(Sound.ENTITY_ITEM_BREAK,0.5f, 0.0f)

            if(Random.nextDouble() <= 0.70){
                burnEntity(mutableListOf(event.target), 0.5, 4)
            }else {
                burnEntity(mutableListOf(event.player), 0.5, 4)
            }

        }
    }

    override fun createItemStackBase(): ItemStack {
        setColorScheme("#F5AA47", "#0F72A8", "#47B7F5")
        return ItemStack(Material.FLINT_AND_STEEL)
            .withDisplayName("#F56E3B", "Air Burner")
            .withDamage(0.5)
            .withAttackSpeed(1.0)
            .withAbilityDescription(
                abilityName = "Hot Air",  activation = "Right Click",
                "Attacks entities within a radius of *10* blocks,",
                "dealing fire damage to them over *6.5* seconds,",
                "but if they are *absent* or you'll be out of luck, it attacks you!"
            )
            .withAbilityDescription(
                abilityName = "Unintentional discharge",  activation = "Attack",
                "Has a *high* chance of setting your target on fire, ",
                "but there is a risk of setting *you* on fire as well!",
            )
    }

    private fun burnEntity(entities: List<LivingEntity>, damage: Double, iterations: Int) {
        for (entity in entities) {
            entity.isVisualFire = true
            particles(Particle.LAVA, 15).display(entity.centerMLoc)

            runLater(16L*iterations) {
                entity.isVisualFire = false
            }

            runParallelFor(16L, iterations) { breakLoop, _ ->
                particles(Particle.SMOKE_LARGE, 8, extra = 0.4).display(entity.eyeMLoc)
                particles(Particle.ITEM_CRACK, 15, data = ItemStack(Material.COOKED_PORKCHOP), extra = 0.3).display(entity.eyeMLoc)
                entity.makeSound(Sound.BLOCK_FIRE_AMBIENT, 2.5f, 0.0f)
                entity.noDamageTicks = 0
                entity.damage(damage)

                if(entity.isDead) {
                    breakLoop()
                }
            }
        }
    }
}