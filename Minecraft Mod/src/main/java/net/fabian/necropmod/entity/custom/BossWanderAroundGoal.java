package net.fabian.necropmod.entity.custom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BossWanderAroundGoal extends Goal {

    private final PathAwareEntity mob;
    private final double speed;
    private final double detectionRange;
    private double targetX;
    private double targetY;
    private double targetZ;

    public BossWanderAroundGoal(PathAwareEntity mob, double speed, double detectionRange) {
        this.mob = mob;
        this.speed = speed;
        this.detectionRange = detectionRange;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        if (target != null && mob.squaredDistanceTo(target) <= detectionRange * detectionRange) {
            // Don't start if there's a target within detection range
            return false;
        }

        Vec3d wanderTarget = this.getWanderTarget();
        if (wanderTarget == null) {
            return false;
        }

        this.targetX = wanderTarget.x;
        this.targetY = wanderTarget.y;
        this.targetZ = wanderTarget.z;
        return true;
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return NoPenaltyTargeting.find(this.mob, 10, 7);
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }
}

