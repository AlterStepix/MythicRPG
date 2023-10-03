package net.alterstepix.mythicrpg.system.recipe

import net.alterstepix.mythicrpg.MythicRPG
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.Recipe

object RecipeManager: Listener {
    private val recipes = hashMapOf<NamespacedKey, Recipe>()

    fun addRecipe(key: NamespacedKey, recipe: Recipe) {
        this.recipes[key] = recipe
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        for(recipe in recipes) {
            event.player.discoverRecipe(recipe.key)
        }
    }
}