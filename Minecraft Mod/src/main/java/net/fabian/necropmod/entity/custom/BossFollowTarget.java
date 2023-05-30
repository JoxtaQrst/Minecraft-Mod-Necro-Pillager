package net.fabian.necropmod.entity.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class BossFollowTarget extends Goal {

    private final PathAwareEntity mob;
    private final double speed;
    private int startsToMove;


    public BossFollowTarget(PathAwareEntity mob, double speed){
        this.mob =mob;
        this.speed = speed;
    }

    @Override
    public void start() {
        this.startsToMove=0;
    }

    @Override
    public boolean canStart() {
        //starts only when the boss has a target
        PlayerEntity player = (PlayerEntity) this.mob.getTarget();
        return player != null && !player.isCreative();
    }

    @Override
    public boolean shouldContinue() {
        //continues only if the mob has a target
        LivingEntity target = this.mob != null ? this.mob.getTarget() : null;
        double distance = this.mob != null && target != null ? this.mob.squaredDistanceTo(target) : Double.MAX_VALUE;
        double attackRange = 10.0D;
        if(distance > attackRange*attackRange && target!=null){
            return target.isAlive() && (this.mob != null && this.mob.canSee(target));
        }
        return false;
    }

    @Override
    public void stop() {
        //stops pathfinding towards the target
        LivingEntity target = this.mob != null ? this.mob.getTarget() : null; // daca this.mob nu e null, se execut this.mob.getTarget(), altfel e null.
        double distance = this.mob != null && target != null ? this.mob.squaredDistanceTo(target) : Double.MAX_VALUE;
        double attackRange = 10.0D;
        if(distance <= attackRange * attackRange && this.mob != null){
            this.mob.getNavigation().stop();
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob != null ? this.mob.getTarget() : null;
        if(target!=null){
            this.startsToMove++;
            if (this.startsToMove >= 5) {
                Vec3d targetPos = target.getPos();
                if (this.mob != null) {
                    this.mob.getNavigation().startMovingTo(targetPos.x, targetPos.y, targetPos.z, this.speed);
                    this.startsToMove = 0;
                }
            }
        }
    }

}
