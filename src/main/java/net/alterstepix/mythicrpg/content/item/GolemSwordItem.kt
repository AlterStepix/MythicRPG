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
import org.bukkit.util.Vector

@MythicContent class GolemSwordItem: MythicItem() {
    init {
        registerItemAbility(cooldownMs = 16000L, autoCancel = true) { event: MItemEvent.RightClick, ctx: AbilityContext ->
            val entities = searchEntities<LivingEntity>(event.player, 5.0)
            if(entities.isEmpty()) {
                ctx.cancel("No target found within 5.0 blocks!")
            }

            particles(Particle.BLOCK_CRACK, 40, 0.0, 0.1, Material.COBBLESTONE.createBlockData()).displayCircle(event.player.centerMLoc, 1.5)

            particles(Particle.BLOCK_DUST, 300, 0.0, 0.3, Material.DEEPSLATE_IRON_ORE.createBlockData()).displaySphere(event.player.centerMLoc, 5.0)
            particles(Particle.EXPLOSION_LARGE, 3, 4.0, 0.5).display(event.player.centerMLoc)
            event.player.makeSound(Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.5f)

            for(target in entities) {
                target.damage(9.0)
                target.velocity.add(Vector(0.0, 1.7, 0.0))
            }
        }
    }
    override fun createItemStackBase(): ItemStack {
        this.setColorScheme("#6371ad", "#d48d5f", "#d45f5f")
        return ItemStack(Material.STONE_SWORD)
            .withDisplayName("#d45f5f", "Golem's Sword")
            .withDamage(9.0)
            .withAttackSpeed(0.7)
            .withAbilityDescription(abilityName = "Slam", activation = "Right Click",
                "Creates a strong explosion and deals *9.0 damage* to every target",
                "within a *5.0 block* radius."
            )
    }
}