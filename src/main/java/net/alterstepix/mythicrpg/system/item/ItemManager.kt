package net.alterstepix.mythicrpg.system.item

import net.alterstepix.mythicrpg.content.item.LightningAxeItem
import org.bukkit.inventory.ItemStack

object ItemManager {
    private val items = hashMapOf<String, MythicItem>()

    fun init() {
        register(LightningAxeItem())
    }

    private fun register(item: MythicItem) {
        this.items[item.getIdentifier()] = item
    }

    operator fun get(identifier: String): MythicItem? {
        return items[identifier]
    }
}