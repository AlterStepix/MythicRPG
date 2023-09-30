package net.alterstepix.mythicrpg.system.event.mob

import net.alterstepix.mythicrpg.system.event.MCancellable
import net.alterstepix.mythicrpg.system.event.MEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

interface MMobEvent: MEvent {
    val entity: LivingEntity

    class AttackEntity(override var isCancelled: Boolean, override val entity: LivingEntity, val target: LivingEntity): MCancellable, MMobEvent
    class Death(override val entity: LivingEntity, val drops: MutableList<ItemStack>, var exp: Int): MMobEvent
}