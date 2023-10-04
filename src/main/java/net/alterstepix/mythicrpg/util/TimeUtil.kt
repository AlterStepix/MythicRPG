package net.alterstepix.mythicrpg.util

import net.alterstepix.mythicrpg.MythicRPG
import org.bukkit.scheduler.BukkitRunnable

fun runLater(ticks: Long, lambda: () -> Unit) {
    object: BukkitRunnable() {
        override fun run() {
            lambda()
        }
    }.runTaskLater(MythicRPG.getInstance(), ticks)
}

class ParallelLoopBreakException(): Exception()

fun runParallelFor(delay: Long, iterations: Int, startDelay: Long = 0L, lambda: (breakLoop: () -> Nothing, i: Int) -> Unit) {
    object: BukkitRunnable() {
        var i = 0

        private fun breakLoop(): Nothing {
            throw ParallelLoopBreakException()
        }

        override fun run() {
            if(i >= iterations) {
                cancel()
                return
            }
            try {
                lambda(::breakLoop, i)
            } catch (_: ParallelLoopBreakException) {
                cancel()
                return
            }
            i++
        }
    }.runTaskTimer(MythicRPG.getInstance(), startDelay, delay)
}

fun runParallelWhile(delay: Long, condition: () -> Boolean, startDelay: Long = 0L, lambda: (breakLoop: () -> Nothing) -> Unit) {
    object: BukkitRunnable() {
        private fun breakLoop(): Nothing {
            throw ParallelLoopBreakException()
        }

        override fun run() {
            if(!condition()) {
                cancel()
                return
            }
            try {
                lambda(::breakLoop)
            } catch (_: ParallelLoopBreakException) {
                cancel()
                return
            }
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