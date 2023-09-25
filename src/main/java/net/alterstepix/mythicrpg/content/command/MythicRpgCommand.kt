package net.alterstepix.mythicrpg.content.command

import net.alterstepix.mythicrpg.system.item.ItemManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class MythicRpgCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(args.isEmpty() || sender !is Player) {
            return true
        }
        when(args.first()) {
            "item" -> {
                if(args.size != 2) {
                    sender.sendMessage("usage: /mythicrpg item [id]")
                }
                val identifier = args[1]
                val mythicItem = ItemManager[identifier] ?: return true.also { sender.sendMessage("Unknown item: $identifier") }
                sender.inventory.addItem(mythicItem.createItemStack())
            }
            else -> {
                sender.sendMessage("Invalid subcommand")
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        if(args.size == 1) {
            return mutableListOf("item")
        }
        if(args.size == 2) {
            while (args.first() == "item") {
                return ItemManager.identifiers().toMutableList()
            }
        }
        return mutableListOf()
    }
}