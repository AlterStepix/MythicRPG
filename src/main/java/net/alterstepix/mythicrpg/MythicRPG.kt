package net.alterstepix.mythicrpg

import net.alterstepix.mythicrpg.content.command.MythicRpgCommand
import net.alterstepix.mythicrpg.system.event.item.ItemEventLauncher
import net.alterstepix.mythicrpg.system.item.ItemManager
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class MythicRPG : JavaPlugin() {
    companion object {
        private lateinit var INSTANCE: MythicRPG
        private lateinit var CONFIG: FileConfiguration
        fun getInstance() = INSTANCE
    }

    fun getConfiguration() = this.config

    override fun onEnable() {
        INSTANCE = this
        saveDefaultConfig()

        Bukkit.getPluginManager().registerEvents(ItemEventLauncher(), this)

        ItemManager.init()

        val command = MythicRpgCommand()
        Bukkit.getPluginCommand("mythic-rpg")?.setExecutor(command)
        Bukkit.getPluginCommand("mythic-rpg")?.tabCompleter = command
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}