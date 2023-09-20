package net.alterstepix.mythicrpg.system.event.item

import net.alterstepix.mythicrpg.system.event.MCancellable
import net.alterstepix.mythicrpg.system.event.MEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface MItemEvent: MEvent {
    val player: Player
    val itemStack: ItemStack

    data class RightClick(override var isCancelled: Boolean, override val player: Player, override val itemStack: ItemStack): MItemEvent, MCancellable
    data class LeftClick(override var isCancelled: Boolean, override val player: Player, override val itemStack: ItemStack): MItemEvent, MCancellable
    data class AttackLivingEntity(override var isCancelled: Boolean, override val player: Player, override val itemStack: ItemStack, val target: LivingEntity): MItemEvent, MCancellable
}