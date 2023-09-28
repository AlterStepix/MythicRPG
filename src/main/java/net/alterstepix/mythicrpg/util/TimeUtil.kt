package net.alterstepix.mythicrpg.util

import net.alterstepix.mythicrpg.MythicRPG
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

fun runLater(ticks: Long, lambda: () -> Unit) {
    object: BukkitRunnable() {
        override fun run() {
            lambda()
        }
    }.runTaskLater(MythicRPG.getInstance(), ticks)
}

class ParallelForBreakException(): Exception()

fun runParallelFor(delay: Long, iterations: Int, startDelay: Long = 0L, lambda: (breakLoop: () -> Nothing, i: Int) -> Unit) {
    object: BukkitRunnable() {
        var i = 0

        private fun breakThrower(): Nothing {
            throw ParallelForBreakException()
        }

        override fun run() {
            if(i >= iterations) {
                cancel()
                return
            }
            try {
                lambda(::breakThrower, i)
            } catch (_: ParallelForBreakException) {
                cancel()
                return
            }
            i++
        }
    }.runTaskTimer(MythicRPG.getInstance(), startDelay, delay)
}

fun runParallelAsync(lambda: () -> Unit) {
    object: BukkitRunnable() {
        override fun run() {
            lambda()
        }
    }.runTaskAsynchronously(MythicRPG.getInstance())
}

fun runParallelSync(lambda: () -> Unit) {
    object: BukkitRunnable() {
        override fun run() {
            lambda()
        }
    }.runTask(MythicRPG.getInstance())
}