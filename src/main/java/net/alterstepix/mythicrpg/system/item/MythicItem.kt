package net.alterstepix.mythicrpg.system.item

import net.alterstepix.mythicrpg.util.withDisplayName
import net.alterstepix.mythicrpg.util.withFlags
import net.alterstepix.mythicrpg.util.withUnbreakable
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

abstract class MythicItem {
    protected abstract fun createItemStackBase(): ItemStack
    fun createItemStack() =
        createItemStackBase()
            .withUnbreakable()
            .withFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
}