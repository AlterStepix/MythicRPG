package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.util.searchEntities
import net.alterstepix.mythicrpg.util.withDisplayName
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class LightningAxeItem: MythicItem() {
    init {
        this.registerItemEvent(cooldownMs = 5500L, autoCancel = true) { event: MItemEvent.RightClick ->
            for(entity in searchEntities<LivingEntity>(event.player, 4.5)) {
                entity.world.strikeLightningEffect(entity.location)
            }
        }
    }

    override fun createItemStackBase(): ItemStack {
        return ItemStack(Material.IRON_AXE)
            .withDisplayName("#7c8ee6", "Lightning Axe")
    }
}