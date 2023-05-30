package net.fabian.necropmod.entity.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import static net.fabian.necropmod.entity.ModEntities.BOSS_PROJECTILE;

public class BossAttackGoal extends Goal {
    private final BossEntity boss;
    private int ticksUntilNextAttack;
    private final double attackRange = 15.0D;

    public BossAttackGoal(BossEntity boss) {
        this.boss = boss;

    }

    // The goal should start if there is a target
    public boolean canStart() {
        LivingEntity target = boss.getTarget();
        return target != null && boss.squaredDistanceTo(target) <= attackRange * attackRange && !((PlayerEntity) target).isCreative();
    }


    // When the goal starts, reset the tick counter
    public void start() {
        ticksUntilNextAttack = 30;
        boss.setShooting(true);
    }

    @Override
    public void stop() {
        boss.setAttacking(false);
        boss.setShooting(false);
    }

    // This runs every tick while the goal is active
    @Override
    public void tick() {
        if (this.boss.getTarget() != null) {
            LivingEntity target = this.boss.getTarget();
            double distance = this.boss.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
            if (distance <= attackRange * attackRange) {
                if (--this.ticksUntilNextAttack <= 0) {
                    this.boss.setAttacking(true);
                    this.launchProjectile(target);
                    this.ticksUntilNextAttack = 30;

                }
            } else {
                this.boss.setAttacking(false);
            }
        } else {
            this.boss.setAttacking(false);
        }
    }


    private void launchProjectile(LivingEntity target) {
        Vec3d bossLookVec = this.boss.getRotationVector();
        double spawnDistanceFromBoss = 1.5; // dist fata de boss
        double spawnX = this.boss.getX() + bossLookVec.x * spawnDistanceFromBoss;
        double spawnY = this.boss.getBodyY(0.5D);
        double spawnZ = this.boss.getZ() + bossLookVec.z * spawnDistanceFromBoss;

        BossProjectile projectile = new BossProjectile(BOSS_PROJECTILE, this.boss.world);
        BossProjectile.setShooter(this.boss);
        projectile.setTarget(target);
        projectile.updatePosition(spawnX, spawnY, spawnZ);

        // Setați viteza proiectilului către jucător
        double deltaX = target.getX() - spawnX;
        double deltaY = target.getBodyY(0.5D) - spawnY;
        double deltaZ = target.getZ() - spawnZ;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        double speed = 0.4D; // Ajustați viteza proiectilului
        double velocityX = deltaX / distance * speed;
        double velocityY = deltaY / distance * speed;
        double velocityZ = deltaZ / distance * speed;
        projectile.setVelocity(velocityX, velocityY, velocityZ);
        // Spawn particles around the entity
        if (this.boss.world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                    this.boss.getX(), this.boss.getBodyY(0.5D), this.boss.getZ(),
                    25, 0.5D, 0.5D, 0.5D, 0.5D);
        }
        this.boss.world.playSound(null, this.boss.getX(), this.boss.getY(), this.boss.getZ(), SoundEvents.ENTITY_VEX_CHARGE, SoundCategory.PLAYERS, 0.5F, 0.3F);
        this.boss.world.spawnEntity(projectile);


    }




}
