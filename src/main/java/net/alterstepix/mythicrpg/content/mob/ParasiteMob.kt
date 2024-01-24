package net.alterstepix.mythicrpg.content.mob

import net.alterstepix.mythicrpg.system.event.mob.MMobEvent
import net.alterstepix.mythicrpg.system.manager.MobManager
import net.alterstepix.mythicrpg.system.manager.MythicContent
import net.alterstepix.mythicrpg.system.mob.MythicMob
import net.alterstepix.mythicrpg.util.*
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType

@MythicContent class ParasiteMob: MythicMob<Zombie>(MobType.ELITE) {
    init {
        registerMobEvent { event: MMobEvent.AttackEntity ->
            if (event.target is Player) {
                event.target.saturation = 0.0f
            }

            potionEffect(PotionEffectType.SLOW, 1.5,2).apply(event.target)
            //potionEffect(PotionEffectType.BLINDNESS, 2.0).apply(event.target)
            potionEffect(PotionEffectType.HUNGER, 8.0, 50).apply(event.target)
            potionEffect(PotionEffectType.POISON, 4.0, 4).apply(event.target)

            particles(Particle.ITEM_CRACK, 15, extra = 0.3, data = ItemStack(Material.ROTTEN_FLESH)).display(event.target.eyeMLoc)
            event.entity.makeSound(Sound.BLOCK_ANVIL_PLACE, 1.5f, 1.5f)

            if (event.entity.health >= (event.entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0) || random()<=0.3) {
                PathTracer(15.0)
                    .withDensity(2.0)
                    .addIterationHandler { data ->
                        data.direction = data.direction.add(random(-0.1..0.1), random(-0.1..0.1), random(-0.1..0.1)).normalize()
                        particles(Particle.TOTEM, 5).display(data.location)
                        return@addIterationHandler true
                    }
                    .addFinalHandler { data ->
                        particles(Particle.VILLAGER_HAPPY, 100).setForce(true).displaySphere(data.location.add(0.0, 1.0, 0.0), 3.0)
                        event.entity.makeSound(Sound.BLOCK_END_PORTAL_SPAWN, 2.0f, 0.0f)
                        MobManager["InfectedZombieMob"]?.create(data.location.add(0.0, 1.0, 0.0))
                    }
                    .traceParallelMultiple(random(2.0..4.0).toInt(), event.entity.eyeMLoc.add(0.0, 1.0,0.0), MVec(random(-0.1..0.1), random(-0.1..0.0), random(-0.1..0.1)), 3L, 3L)
            } else {
                event.entity.increaseHealth(10.0)
                particles(Particle.HEART, 4, extra = 0.3, offset = 0.4).display(event.entity.eyeMLoc)
                event.entity.makeSound(Sound.BLOCK_HONEY_BLOCK_BREAK, 4.0f, 0.0f)
            }
        }

        registerMobAbility(500..520) { entity ->
            if (entity.target == null) return@registerMobAbility

            val startVec = MVec(random(-0.1..0.1), 10.0, random(-0.1..0.1))

            val hungerFallingBlock = entity.world.spawnFallingBlock(entity.mLoc.location.add(0.0, 3.5, 0.0), Material.JUNGLE_LEAVES.createBlockData())
            hungerFallingBlock.setGravity(false)
            hungerFallingBlock.dropItem = false
            hungerFallingBlock.velocity = startVec.normalize().copy().vector
            /*
            val hungerFallingBlockHP = object: MythicMob<Slime>(MobType.MOB) {
                override fun createMobBase(): EntityBuilder<Slime> {
                    val hungerFallingBlockHPSlime = EntityBuilder(Slime::class.java)
                        .setDisplayName("#79A814", "Hunger Projectile")
                        .setHealth(1.0)
                        .addPersistentPotionEffect(PotionEffectType.INVISIBILITY)
                        .addPersistentPotionEffect(PotionEffectType.GLOWING)
                        .setAI(false)
                    hungerFallingBlockHPSlime.addModifier { slime ->
                        slime.size = 4
                    }
                    return hungerFallingBlockHPSlime
                }
            }.create(entity.mLoc.add(0.0, 3.5, 0.0))
            hungerFallingBlockHP.velocity = startVec.normalize().copy().vector
            hungerFallingBlockHP.health=1.0
            */
            PathTracer(20.0)
                .withDensity(2.0)
                .addIterationHandler { data ->
                    /*
                    if (hungerFallingBlockHP.isDead) {
                        particles(Particle.BLOCK_CRACK, 70, extra = 0.3, data = Material.JUNGLE_LEAVES.createBlockData()).display(data.location)
                        particles(Particle.ITEM_CRACK, 70, extra = 0.25, data = ItemStack(Material.ROTTEN_FLESH)).display(data.location)
                        hungerFallingBlock.remove()
                        hungerFallingBlockHP.remove()
                        return@addIterationHandler false
                    }
                     */
                    data.direction = (data.direction.add(random(-0.1..0.1), random(-0.1..0.1), random(-0.1..0.1)).mul(6.0).add(
                        (entity.target?:entity).centerMLoc.sub(data.location.mVec).mVec.mul(0.5)).mul(0.07).normalize()).mul(0.85)
                    hungerFallingBlock.velocity = data.direction.mul(0.5).vector
                    //hungerFallingBlockHP.velocity = data.direction.mul(0.5).vector
                    if (hungerFallingBlock.location.x > data.location.location.x+0.2 || hungerFallingBlock.location.x < data.location.location.x-0.2 ||
                        hungerFallingBlock.location.y > data.location.location.y+0.2 || hungerFallingBlock.location.y < data.location.location.y-0.2 ||
                        hungerFallingBlock.location.z > data.location.location.z+0.2 || hungerFallingBlock.location.z < data.location.location.z-0.2) {
                        hungerFallingBlock.teleport(data.location.location)
                    }
                    /*
                    if (hungerFallingBlockHP.location.x > data.location.location.x+0.2 || hungerFallingBlockHP.location.x < data.location.location.x-0.2 ||
                        hungerFallingBlockHP.location.y > data.location.location.y+0.2 || hungerFallingBlockHP.location.y < data.location.location.y-0.2 ||
                        hungerFallingBlockHP.location.z > data.location.location.z+0.2 || hungerFallingBlockHP.location.z < data.location.location.z-0.2) {
                        hungerFallingBlockHP.teleport(data.location.location)
                    }
                    */
                    particles("#6FA61C", "#A9FC2B", 1, size = 3.0f).display(data.location)
                    val entities = searchEntities<LivingEntity>(data.location, 2.0).filter { searchEntity -> searchEntity != entity /*&& searchEntity!= hungerFallingBlockHP*/ }
                    if (entities.isNotEmpty()) {
                        for (searchEntity in searchEntities<LivingEntity>(data.location, 4.0).filter { searchEntity -> searchEntity != entity /*&& searchEntity!= hungerFallingBlockHP*/}) {
                            if (searchEntity is Player) {
                                searchEntity.saturation = 0.0f
                            }
                            searchEntity.damageFrom(6.0, data.location)
                            potionEffect(PotionEffectType.HUNGER, 10.0, 99).apply(searchEntity)
                            //potionEffect(PotionEffectType.CONFUSION, 8.0, 5).apply(searchEntity)
                        }
                        particles(Particle.BLOCK_CRACK, 500, data = Material.JUNGLE_LEAVES.createBlockData()).setForce(true).displaySphere(data.location.add(0.0, 1.0, 0.0), 4.0)
                        particles("#FF2400", "#610D00", 20, size = 3.0f, extra = 0.2).display(data.location)
                        hungerFallingBlock.remove()
                        //hungerFallingBlockHP.remove()
                    }
                    return@addIterationHandler entities.isEmpty()
                }
                .addFinalHandler { data ->
                    for (searchEntity in searchEntities<LivingEntity>(data.location, 3.0).filter { searchEntity -> searchEntity != entity /*&& searchEntity!= hungerFallingBlockHP*/}) {
                        if (searchEntity is Player) {
                            searchEntity.saturation = 0.0f
                        }
                        searchEntity.damageFrom(4.0, data.location)
                        potionEffect(PotionEffectType.HUNGER, 7.0, 99).apply(searchEntity)
                        //potionEffect(PotionEffectType.CONFUSION, 8.0, 5).apply(searchEntity)
                    }
                    particles(Particle.BLOCK_CRACK, 500, data = Material.JUNGLE_LEAVES.createBlockData()).setForce(true).displaySphere(data.location.add(0.0, 1.0, 0.0), 3.0)
                    particles(Particle.ITEM_CRACK, 70, extra = 0.25, data = ItemStack(Material.ROTTEN_FLESH)).display(data.location)
                    hungerFallingBlock.remove()
                    //hungerFallingBlockHP.remove()
                }
                .traceParallel(entity.mLoc.add(0.0, 3.0,0.0), startVec, 2L, 2L)
        }
    }
    override fun createMobBase(): EntityBuilder<Zombie> {
        return EntityBuilder(Zombie::class.java)
            .setDisplayName("#597A0F", "Parasite")
            .setHealth(100.0)
            .setSpeed(0.17)
            .setKnockbackResistance(0.75)
            .setArmor(25.0)
            .setEquipment(
                helmet = ItemStack(Material.IRON_HELMET),
                chestplate = ItemStack(Material.LEATHER_CHESTPLATE).withLeatherColor("#2C7A0F"),
                leggings = ItemStack(Material.LEATHER_LEGGINGS).withLeatherColor("#0F7A40"),
                boots = ItemStack(Material.LEATHER_BOOTS).withLeatherColor("#0F7A1B"),
            )
            .setMainhand(Material.NETHERITE_SWORD)
            .setOffhand(Material.KELP)
    }
}