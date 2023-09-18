package net.alterstepix.mythicrpg.util

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

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

fun ItemStack.withUnbreakable(unbreakable: Boolean = true) = this.appliedToMeta { meta: ItemMeta -> meta.isUnbreakable = unbreakable }
fun ItemStack.withFlags(vararg flags: ItemFlag) = this.appliedToMeta { meta: ItemMeta -> meta.addItemFlags(*flags) }