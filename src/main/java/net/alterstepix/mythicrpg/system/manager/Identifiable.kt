package net.alterstepix.mythicrpg.system.manager

interface Identifiable {
    fun getIdentifier(): String {
        return this::class.simpleName ?: "Unknown"
    }
}