package net.alterstepix.mythicrpg.content.command

import net.alterstepix.mythicrpg.system.manager.ItemManager
import net.alterstepix.mythicrpg.system.manager.MobManager
import net.alterstepix.mythicrpg.util.mLoc
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
                    return true
                }
                val identifier = args[1]
                val mythicItem = ItemManager[identifier] ?: return true.also { sender.sendMessage("Unknown item: $identifier") }
                sender.inventory.addItem(mythicItem.createItemStack())
            }
            "mob" -> {
                if(args.size != 2) {
                    sender.sendMessage("usage: /mythicrpg mob [id]")
                    return true
                }
                val identifier = args[1]
                val element = MobManager[identifier] ?: return true.also { sender.sendMessage("Unknown identifier: $identifier") }

                element.create(sender.mLoc)
            }
            else -> {
                sender.sendMessage("usage: /mythicrpg <mob/item> [options...]")
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
            return mutableListOf("item", "mob")
        }
        if(args.size == 2) {
            when (args.first()) {
                "item" -> return ItemManager.keys.toMutableList()
                "mob" -> return MobManager.keys.toMutableList()
            }
        }
        return mutableListOf()
    }
}