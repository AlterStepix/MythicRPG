package net.alterstepix.mythicrpg.content.mob

import net.alterstepix.mythicrpg.system.event.mob.MMobEvent
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.system.mob.MythicMob
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Skeleton
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType

@MythicContent class IncineratedPyromancerMob : MythicMob<Skeleton>(MobType.MOB) {
    init {
        registerMobEvent { event: MMobEvent.AttackEntity ->
            event.target.isVisualFire = true

            particles(Particle.LAVA, 15).display(event.target.centerMLoc)
            particles(Particle.SMOKE_LARGE, 8, extra = 0.4).display(event.target.eyeMLoc)
            event.target.makeSound(Sound.BLOCK_FIRE_AMBIENT, 2.5f, 0.0f)

            potionEffect(PotionEffectType.CONFUSION, 8.0, 5, true).apply(event.target)
            potionEffect(PotionEffectType.BLINDNESS, 3.0, visible = true).apply(event.target)
            potionEffect(PotionEffectType.SLOW, 1.0,1 , visible = true).apply(event.target)

            runLater(100L) {
                event.target.noDamageTicks = 0
                event.target.isVisualFire = false
            }
        }

        registerMobAbility(300..320) { entity ->
            if (entity.target == null) return@registerMobAbility

            particles(Particle.FLAME, 600).setForce(true).displaySphere(entity.mLoc, 8.0)
            particles(Particle.LAVA, 200).setForce(true).displaySphere(entity.mLoc, 8.0)

            potionEffect(PotionEffectType.SLOW, 3.0,10 , visible = false).apply(entity)

            runLater(40) {
                particles(Particle.SMOKE_LARGE, 200, extra = 0.1).setForce(true).displaySphere(entity.centerMLoc, 5.0)
                for (target in searchEntities<Player>(entity.mLoc, 8.0).filter { e -> e != entity }){
                    target.isVisualFire = true
                    particles(Particle.LAVA, 15).display(target.centerMLoc)
                    runLater(160L) {
                        target.isVisualFire = false
                    }
                    runParallelFor(20L, 8) { breakLoop, _ ->
                        particles(Particle.SMOKE_LARGE, 8, extra = 0.4).display(target.eyeMLoc)
                        particles(Particle.ITEM_CRACK, 15, extra = 0.3, data = ItemStack(Material.COOKED_PORKCHOP)).display(target.eyeMLoc)
                        target.makeSound(Sound.BLOCK_FIRE_AMBIENT, 2.5f, 0.0f)
                        target.noDamageTicks = 0
                        target.damage(1.0)

                        if (target.isDead) {
                            breakLoop()
                        }
                    }
                }
            }
        }
    }

    override fun createMobBase(): EntityBuilder<Skeleton> {
        return EntityBuilder(Skeleton::class.java)
            .setDisplayName("#998350","Incinerated Pyromancer")
            .setHealth(30.0)
            .setSpeed(0.36)
            .setEquipment(
                helmet = ItemStack(Material.LEATHER_HELMET).withLeatherColor("#CCA852"),
                chestplate = ItemStack(Material.LEATHER_CHESTPLATE).withLeatherColor("#A38641"),
                leggings = ItemStack(Material.LEATHER_LEGGINGS).withLeatherColor("#635228")
            )
            .setMainhand(Material.FLINT_AND_STEEL)
            .setOffhand(Material.COOKED_PORKCHOP)
            .addPersistentPotionEffect(PotionEffectType.FIRE_RESISTANCE)
    }
}