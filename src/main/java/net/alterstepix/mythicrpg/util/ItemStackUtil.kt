package net.alterstepix.mythicrpg.util

import net.alterstepix.mythicrpg.MythicRPG
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

/**
 * Creates a new [ItemStack] with and applies [lambda] to its [ItemMeta].
 * @param lambda Transformation to apply to the new [ItemMeta].
 * @param T Casts new [ItemStack]'s [ItemMeta] to this type (unchecked cast).
 * @return A new [ItemStack] with the [lambda] applied to its [ItemMeta].
 */
fun <T: ItemMeta> ItemStack.appliedToMeta(lambda: (T) -> Unit): ItemStack {
    val item = this.clone()
    val meta = item.itemMeta

    lambda(meta as T)

    item.itemMeta = meta
    return item
}

/**
 * Creates a new [ItemStack] with specified [displayName].
 * @param displayName Display name to create an [ItemStack] with.
 * @return Newly created [ItemStack]
 */
fun ItemStack.withDisplayName(displayName: String) = this.appliedToMeta { meta: ItemMeta -> meta.setDisplayName(displayName) }

/**
 * Creates a new [ItemStack] with specified [displayName] colored with [hex] color code.
 * @param hex The hex color code of the [displayName].
 * @param displayName Display name to create an [ItemStack] with.
 * @return Newly created [ItemStack]
 */
fun ItemStack.withDisplayName(hex: String, displayName: String) = this.withDisplayName("${hex(hex)}$displayName")

fun ItemStack.withLore(vararg lore: String) = this.appliedToMeta { meta: ItemMeta ->
    if(meta.lore == null) meta.lore = lore.toList()
    else {
        val itemLore = meta.lore
        itemLore?.addAll(lore)
        meta.lore = itemLore
    }
}
fun ItemStack.withUnbreakable(unbreakable: Boolean = true) = this.appliedToMeta { meta: ItemMeta -> meta.isUnbreakable = unbreakable }
fun ItemStack.withFlags(vararg flags: ItemFlag) = this.appliedToMeta { meta: ItemMeta -> meta.addItemFlags(*flags) }
fun ItemStack.withData(key: String, value: String) = this.appliedToMeta { meta: ItemMeta -> meta.persistentDataContainer[NamespacedKey(MythicRPG.getInstance(), key), PersistentDataType.STRING] = value }

fun ItemStack.getData(key: String): String? {
    val meta = this.itemMeta ?: return null
    return meta.persistentDataContainer[NamespacedKey(MythicRPG.getInstance(), key), PersistentDataType.STRING]
}

fun ItemStack.withAttribute(attribute: Attribute, value: Double, slot: EquipmentSlot) = this.appliedToMeta { meta: ItemMeta ->
    meta.addAttributeModifier(attribute, AttributeModifier(UUID.randomUUID(), "Attribute", value, AttributeModifier.Operation.ADD_NUMBER, slot))
}

fun ItemStack.withLeatherColor(color: Color) = this.appliedToMeta { meta: LeatherArmorMeta ->
    meta.setColor(color)
}

fun ItemStack.withLeatherColor(color: String) = this.appliedToMeta { meta: LeatherArmorMeta ->
    meta.setColor(Color.fromRGB(color.trimStart('#').toInt(16)))
}