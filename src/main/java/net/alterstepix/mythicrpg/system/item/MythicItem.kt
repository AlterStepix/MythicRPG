package net.alterstepix.mythicrpg.system.item

import net.alterstepix.mythicrpg.system.event.EventManager
import net.alterstepix.mythicrpg.system.event.MCancellable
import net.alterstepix.mythicrpg.system.event.item.MItemEvent
import net.alterstepix.mythicrpg.system.manager.Identifiable
import net.alterstepix.mythicrpg.util.*
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

abstract class MythicItem: Identifiable {
    private var colorScheme = Triple<String, String, String>("#ff0000", "#ffe600", "#24ff00")
    class AbilityContext() {
        class AbilityCancelledException constructor(val reason: String) : Throwable()
        fun cancel(reason: String) {
            throw AbilityCancelledException(reason)
        }
    }

    protected inline fun <reified T: MItemEvent> registerItemAbility(cooldownMs: Long = 0L, autoCancel: Boolean = true, silent: Boolean = false, crossinline handler: (T, AbilityContext) -> Unit) {
        val map = CooldownMap(cooldownMs)
        val notificationCooldown = CooldownMap(500)
        EventManager.register(lambda = { event: T ->
            if(event.itemStack.getData("mythic-item") == getIdentifier()) {
                if(map[event.player]) {
                    try {
                        val ctx = AbilityContext()
                        handler(event, ctx)
                        notificationCooldown.update(event.player)
                        if(autoCancel && event is MCancellable) { event.isCancelled = true }
                    } catch (e: AbilityContext.AbilityCancelledException) {
                        map.reset(event.player)
                        if (!silent && notificationCooldown[event.player]) {
                            event.player.sendMessage("${hex("#c9775b")}${e.reason}")
                        }
                    }
                } else if (!silent && notificationCooldown[event.player]){
                    val time = (map.timeOf(event.player).toDouble() / 1000.0).format(1)
                    val progress = (map.progressOf(event.player) * 100.0).toInt()
                    event.player.sendMessage("${hex("#c9775b")}This ability in on cooldown for ${time}s ${hex("#d59642")} [$progress%]")
                }
            }
        })
    }

    protected inline fun <reified T: MItemEvent> registerItemEvent(crossinline handler: (T) -> Unit) {
        EventManager.register(lambda = { event: T ->
            if (event.itemStack.getData("mythic-item") == getIdentifier()) {
                handler(event)
            }
        })
    }

    protected abstract fun createItemStackBase(): ItemStack

    protected fun setColorScheme(a: String, b: String, c: String) {
        this.colorScheme = Triple(a, b, c)
    }

    fun createItemStack() =
        createItemStackBase()
            .withUnbreakable()
            .withFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
            .withData("mythic-item", getIdentifier())

    protected fun ItemStack.withDamage(damage: Double) = this
        .withAttribute(Attribute.GENERIC_ATTACK_DAMAGE, damage - 1.0, EquipmentSlot.HAND)
        .withLore("${hex(colorScheme.first)}Damage: ${hex(colorScheme.second)}♥${hex(colorScheme.third)}§l$damage")

    protected fun ItemStack.withAttackSpeed(attackSpeed: Double) = this
        .withAttribute(Attribute.GENERIC_ATTACK_SPEED, attackSpeed - 4.0, EquipmentSlot.HAND)
        .withLore("${hex(colorScheme.first)}Attack Speed: ${hex(colorScheme.second)}⚔${hex(colorScheme.third)}§l$attackSpeed")

    protected fun ItemStack.withAbilityDescription(abilityName: String, activation: String, vararg description: String) = this
        .withLore("")
        .withLore("${hex(colorScheme.second)}Ability: §l${abilityName.uppercase()} §r${hex(colorScheme.third)}[$activation]")
        .withLore(*description.map { line -> "§7$line".replace(Regex("\\*(.+)\\*"), "${hex(colorScheme.first)}$0§7").replace("*", "") }.toTypedArray())

    protected fun ItemStack.withCooldownLore(cooldownMs: Long) = this
        .withLore("§8[Cooldown: ${(cooldownMs.toDouble() / 1000.0).format(1)}s]")
}