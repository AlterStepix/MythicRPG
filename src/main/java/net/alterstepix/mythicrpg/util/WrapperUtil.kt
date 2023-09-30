package net.alterstepix.mythicrpg.util

/**
 * A wrapper class that provides interior mutability
 */
class Mut<T>(private var value: T) {
    fun set(value: T) {
        this.value = value
    }

    fun get(): T {
        return this.value
    }
}