package net.alterstepix.mythicrpg.system.ingredient

import net.alterstepix.mythicrpg.util.hex
import net.alterstepix.mythicrpg.util.random
import net.alterstepix.mythicrpg.util.withDisplayName
import net.alterstepix.mythicrpg.util.withLore
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object IngredientManager {
    class Ingredient(val itemStack: ItemStack, val drop: () -> List<ItemStack>)

    enum class DropRarity(val displayName: String) {
        COMMON("${hex("#e9e9e9")}Common Drop"),
        RARE("${hex("#75a2bb")}Rare Drop"),
        EPIC("${hex("#b175bd")}Epic Drop"),
        LEGENDARY("${hex("#b175bd")}Legendary Drop"),
        MYTHIC("${hex("#ef4d4d")}Mythic Drop")
    }

    val infectedFleshDrop = makeIngredient(40.0, Material.ROTTEN_FLESH, "#96c355", "Infected Flesh", DropRarity.COMMON)
    val heartOfInfection = makeIngredient(10.0, Material.BEETROOT, "#940611", "Heart of Infection", DropRarity.RARE)

    val compressedDeepslateShard = makeIngredient(10.0, Material.GRAY_DYE, "#4e4355", "Compressed Deepslate Shard", DropRarity.RARE)

    private fun makeIngredient(dropChance: Double, material: Material, displayColor: String, displayName: String, rarity: DropRarity, vararg lore: String): Ingredient {
        val itemStack = ItemStack(material, 1)
            .withDisplayName(displayColor, displayName)
            .withLore(*lore.map { line -> "§7$line" }.toTypedArray())
            .withLore("")
            .withLore(rarity.displayName)

        return Ingredient(itemStack, drop = {
            val drops = mutableListOf<ItemStack>()
            while (random() < dropChance / 100.0) {
                drops.add(itemStack)
            }

            return@Ingredient drops
        })
    }
}