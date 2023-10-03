package net.alterstepix.mythicrpg.system.event.mob

import net.alterstepix.mythicrpg.system.event.EventManager
import net.alterstepix.mythicrpg.util.getData
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

class MobEventLauncher: Listener {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if(event.entity !is LivingEntity || event.damager !is LivingEntity) return

        val mEvent = MMobEvent.AttackEntity(false, event.damager as LivingEntity, event.entity as LivingEntity)
        EventManager.launch(mEvent)
        if(mEvent.isCancelled) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityDeathEvent(event: EntityDeathEvent) {
        if(event.entity.getData("mythic-mob") != null) {
            event.droppedExp = 0
            event.drops.clear()
        }

        val mEvent = MMobEvent.Death(event.entity, event.drops, event.droppedExp)
        EventManager.launch(mEvent)
        event.droppedExp = mEvent.exp
    }
}