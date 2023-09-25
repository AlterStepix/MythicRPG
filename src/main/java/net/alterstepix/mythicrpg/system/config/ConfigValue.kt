package net.alterstepix.mythicrpg.system.config

import net.alterstepix.mythicrpg.MythicRPG
import kotlin.reflect.KProperty

class ConfigValue<T: Any>(private val path: String, private val default: () -> T) {
    constructor(path: String, defaultV: T) : this(path, { defaultV } )

    private fun saveDefault(): T {
        val config = MythicRPG.getInstance().config
        val value: T = default()
        config.set(path, value)
        config.save("config.yml")
        return value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val config = MythicRPG.getInstance().config
        return try {
            (config.get(path) ?: saveDefault()) as T
        } catch (_: Exception) {
            println("Caught")
            val value: T = default()
            config.set(path, value)
            config.save("config.yml")
            value
        }
    }
}