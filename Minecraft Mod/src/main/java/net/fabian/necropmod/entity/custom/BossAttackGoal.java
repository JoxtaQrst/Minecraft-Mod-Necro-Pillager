package net.fabian.necropmod.entity.custom;

import net.fabian.necropmod.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import static net.fabian.necropmod.entity.ModEntities.BOSS_PROJECTILE;

public class BossAttackGoal extends Goal {
    private final BossEntity boss;
    private int ticksUntilNextAttack;
    private final double attackRange = 8.0D;

    public BossAttackGoal(BossEntity boss) {
        this.boss = boss;

    }

    // The goal should start if there is a target
    public boolean canStart() {
        return boss.getTarget() != null;
    }

    // When the goal starts, reset the tick counter
    public void start() {
        ticksUntilNextAttack = 30;
    }

    @Override
    public void stop() {
        this.boss.setAttacking(false);
    }

    // This runs every tick while the goal is active
    @Override
    public void tick() {
        if (this.boss.getTarget() instanceof PlayerEntity) {
            PlayerEntity target = (PlayerEntity) this.boss.getTarget();
            double distance = this.boss.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

            //Check if target is within range
            if (distance <= attackRange * attackRange) {
                // Look at the target player
                this.boss.lookAtEntity(target, 360.0F, 360.0F);
                if (--this.ticksUntilNextAttack <= 0) {

                    this.boss.setAttacking(true);
                    launchProjectile(target);
                    this.ticksUntilNextAttack = 30;
                }
            } else {
                this.boss.setAttacking(false);

            }

        } else {
            this.boss.setAttacking(false);
        }
    }
    private void launchProjectile(PlayerEntity target) {
        Vec3d bossLookVec = this.boss.getRotationVector();
        double spawnDistanceFromBoss = 1.0; // Adjust as needed
        double spawnX = this.boss.getX() + bossLookVec.x * spawnDistanceFromBoss;
        double spawnY = this.boss.getBodyY(0.5D);
        double spawnZ = this.boss.getZ() + bossLookVec.z * spawnDistanceFromBoss;

        BossProjectile projectile = new BossProjectile(BOSS_PROJECTILE, this.boss.world);
        BossProjectile.setShooter(boss);
        projectile.updatePosition(spawnX, spawnY, spawnZ);
        projectile.setTarget(target);

        // Spawn the projectile in the world
        this.boss.world.spawnEntity(projectile);


    }


}

