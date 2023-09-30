package net.alterstepix.mythicrpg.system.manager

import net.alterstepix.mythicrpg.system.item.MythicItem
import net.alterstepix.mythicrpg.system.mob.MythicMob
import org.reflections.Reflections

abstract class AbstractManager<T: Identifiable>(private val contentClass: Class<T>) {
    private val contentMap = hashMapOf<String, T>()
    fun init() {
        val classes = Reflections("net.alterstepix.mythicrpg")
            .getSubTypesOf(contentClass)

        for(contentClass in classes) {
            if(contentClass.isAnnotationPresent(MythicContent::class.java)) {
                val instance = contentClass.getDeclaredConstructor().newInstance()
                contentMap[instance.getIdentifier()] = instance
            }
        }
    }

    operator fun get(key: String): T? {
        return contentMap[key]
    }

    val keys get() = contentMap.keys
    val values get() = contentMap.values
}

object ItemManager: AbstractManager<MythicItem>(MythicItem::class.java)
object MobManager: AbstractManager<MythicMob<*>>(MythicMob::class.java)

fun initManagers() {
    ItemManager.init()
    MobManager.init()
}