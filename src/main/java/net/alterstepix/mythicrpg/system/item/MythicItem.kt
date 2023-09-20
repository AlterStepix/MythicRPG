package net.alterstepix.mythicrpg.system.item

import net.alterstepix.mythicrpg.system.event.EventManager
import net.alterstepix.mythicrpg.system.event.MCancellable
import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.util.*
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

abstract class MythicItem {
    protected inline fun <reified T: MItemEvent> registerItemEvent(cooldownMs: Long = 0L, autoCancel: Boolean = true, crossinline handler: (T) -> Unit) {
        val map = CooldownMap(cooldownMs)
        EventManager.register(lambda = { event: T ->
            if(event.itemStack.getData("mythic-item") == getIdentifier() && map[event.player]) {
                if(autoCancel && event is MCancellable) { event.isCancelled = true }
                handler(event)
            }
        })
    }

    protected abstract fun createItemStackBase(): ItemStack

    fun createItemStack() =
        createItemStackBase()
            .withUnbreakable()
            .withFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
            .withData("mythic-item", getIdentifier())

    fun getIdentifier(): String {
        return this::class.simpleName ?: "Unknown"
    }
}