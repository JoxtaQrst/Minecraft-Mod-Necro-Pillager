package net.fabian.necropmod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class Scythe extends Item {

    private static final float IRON_SWORD_DAMAGE = 6.0f;
    private static final float SCYTHE_MULTIPLIER = 3.0f;

    public Scythe(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        World world = attacker.getEntityWorld();
        if (!world.isClient && world instanceof ServerWorld) {
            float damage = IRON_SWORD_DAMAGE;
            if (target instanceof ZombieEntity || target instanceof SkeletonEntity) {
                damage *= SCYTHE_MULTIPLIER;
                ServerWorld serverWorld = (ServerWorld) world;
                target.damage(attacker.getDamageSources().playerAttack((PlayerEntity) attacker),damage);
                serverWorld.spawnParticles(ParticleTypes.WITCH, target.getX(), target.getY() + 1.0, target.getZ(), 30, 0.5, 0.5, 0.5, 0.1);

                if (target.isDead()) {
                    serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT, SoundCategory.PLAYERS, 1.0f, 0.2f);
                }
            }
        }
        return super.postHit(stack, target, attacker);
    }


}
