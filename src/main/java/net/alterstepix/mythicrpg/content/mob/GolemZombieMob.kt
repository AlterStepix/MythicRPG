package net.alterstepix.mythicrpg.content.mob

import net.alterstepix.mythicrpg.system.ingredient.IngredientManager
import net.alterstepix.mythicrpg.system.event.mob.MMobEvent
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.system.mob.MythicMob
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

@MythicContent class GolemZombieMob: MythicMob<Zombie>(MobType.MOB) {
    init {
        registerDrops(IngredientManager.compressedDeepslateShard)

        registerMobAbility(80..160) { entity ->
            if(entity.target == null) return@registerMobAbility

            entity.makeSound(Sound.ENTITY_ITEM_BREAK, 0.5f, 0.5f)
            particles(Particle.BLOCK_CRACK, 40, 0.0, 0.1, Material.COBBLESTONE.createBlockData()).displayCircle(entity.centerMLoc, 1.5)

            runLater(20) {
                particles(Particle.BLOCK_DUST, 300, 0.0, 0.3, Material.DEEPSLATE_IRON_ORE.createBlockData()).displaySphere(entity.centerMLoc, 4.0)
                particles(Particle.EXPLOSION_LARGE, 3, 4.0, 0.5).display(entity.centerMLoc)
                entity.makeSound(Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.5f)

                for(target in searchEntities<Player>(entity.centerMLoc, 5.0).filter { e -> e != entity }) {
                    target.damage(9.0)
                    target.velocity.add(Vector(0.0, 1.7, 0.0))
                }
            }
        }

        registerMobEvent<MMobEvent.AttackEntity> { event ->
            EffectBuilder(PotionEffectType.SLOW)
                .withDuration(2.0)
                .withAmplifier(3)
                .apply(event.target)

            event.entity.makeSound(Sound.ENTITY_ITEM_BREAK, 0.5f, -1.0f)
            particles(Particle.BLOCK_CRACK, 40, 2.0, 0.5, Material.DEEPSLATE.createBlockData()).display(event.entity.eyeMLoc)
        }
    }

    override fun createMobBase(): EntityBuilder<Zombie> {
        return EntityBuilder(Zombie::class.java)
            .setDisplayName("#4e4355", "Golem Zombie")
            .setHealth(30.0)
            .setSpeed(0.2)
            .setKnockbackResistance(0.5)
            .setEquipment(
                helmet = ItemStack(Material.COBBLESTONE),
                chestplate = ItemStack(Material.LEATHER_CHESTPLATE).withLeatherColor("#4e4355")
            )
            .setMainhand(Material.STONE_SWORD)
    }
}