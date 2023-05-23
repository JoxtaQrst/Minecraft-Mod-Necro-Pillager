package net.fabian.necropmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BossEntity extends PillagerEntity implements GeoEntity {

    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ServerBossBar bossBar;


    public BossEntity(EntityType<? extends PillagerEntity> entityType, World world) {
        super(entityType, world);
        if (world instanceof ServerWorld) {
            this.bossBar = new ServerBossBar(Text.of("Necro Pillager"), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
        }
    }


    public static DefaultAttributeContainer.Builder setAttributes() {
        return PillagerEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D) // Follow range
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D) // Movement speed
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D) // Attack damage
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 120.0D); // Max health
    }

    protected void initGoals() {
        //super.initGoals();

        // Add the wander behavior with normal speed 1.0D
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.5D));
        // Ranged attack behavior
        this.goalSelector.add(3, new FollowMobGoal(this, 1.0D, 3, 7));
        this.goalSelector.add(2, new BossAttackGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.add(0,new SwimGoal(this));



    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        AnimationController<T> controller = tAnimationState.getController();
        if (this.isAttacking()) {
            controller.setAnimation(RawAnimation.begin().then("animation.model.attack", Animation.LoopType.LOOP));
            controller.setAnimationSpeed(1.5D);
        } else if (tAnimationState.isMoving()) {
            controller.setAnimation(RawAnimation.begin().then("animation.model.walk", Animation.LoopType.LOOP));
            controller.setAnimationSpeed(2.5D);
        } else {
            controller.setAnimation(RawAnimation.begin().then("animation.model.idle", Animation.LoopType.LOOP));
            controller.setAnimationSpeed(1.0D);
        }
        // Look at the target player
        if (this.getTarget() instanceof PlayerEntity) {
            PlayerEntity target = (PlayerEntity) this.getTarget();
            this.lookAtEntity(target, 20.0F, 20.0F);
        }

        return PlayState.CONTINUE;
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public void mobTick() {
        super.mobTick();
        updateBossBar();
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (bossBar != null) {
            bossBar.clearPlayers();
            bossBar = null;
        }
    }

    private void updateBossBar() {
        if (bossBar != null) {
            // Update the boss bar progress
            bossBar.setPercent(getHealth() / getMaxHealth());
            // Add players within a certain range to the boss bar
            List<PlayerEntity> nearbyPlayers = (List<PlayerEntity>) world.getPlayers();
            for (PlayerEntity player : nearbyPlayers) {
                if (player instanceof ServerPlayerEntity && this.distanceTo(player) < 50) {
                    bossBar.addPlayer((ServerPlayerEntity) player);
                }
            }

            // Remove players who are no longer within range or too far
            Set<ServerPlayerEntity> currentPlayers = new HashSet<>(bossBar.getPlayers());
            for (ServerPlayerEntity player : currentPlayers) {
                if (!nearbyPlayers.contains(player) || this.distanceTo(player) >= 50) {
                    bossBar.removePlayer(player);
                }
            }
        }
    }

}