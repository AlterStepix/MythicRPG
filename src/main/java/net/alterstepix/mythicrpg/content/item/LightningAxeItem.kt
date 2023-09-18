package net.alterstepix.mythicrpg.content.item

import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.util.withDisplayName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class LightningAxeItem: MythicItem() {
    override fun createItemStackBase(): ItemStack {
        return ItemStack(Material.IRON_AXE)
            .withDisplayName("#7c8ee6", "Lightning Axe")
    }
}