package net.alterstepix.mythicrpg.system.event.item

import net.alterstepix.mythicrpg.system.event.EventManager
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent

class ItemEventLauncher: Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val mEvent = if(event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
            MItemEvent.LeftClick(false, event.player, event.player.inventory.itemInMainHand)
        } else {
            MItemEvent.RightClick(false, event.player, event.player.inventory.itemInMainHand)
        }

        EventManager.launch(mEvent)
        if(mEvent.isCancelled) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityAttackEntity(event: EntityDamageByEntityEvent) {
        if(event.damager !is Player || event.entity !is LivingEntity || event.cause == EntityDamageEvent.DamageCause.CUSTOM) return

        val mEvent = MItemEvent.AttackLivingEntity(false, event.damager as Player, (event.damager as Player).inventory.itemInMainHand, event.entity as LivingEntity)
        EventManager.launch(mEvent)
        if(mEvent.isCancelled) {
            event.isCancelled = true
        }
    }
}