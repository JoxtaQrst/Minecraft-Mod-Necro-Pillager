package net.fabian.necropmod.entity.custom;

import net.fabian.necropmod.item.ModItems;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class BossEntity extends HostileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ServerBossBar bossBar;
    private static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(BossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(BossEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private long lastTimeSpawned;


    public BossEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        if (world instanceof ServerWorld) {
            this.bossBar = new ServerBossBar(Text.of("Necro Pillager"), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
        this.dataTracker.startTracking(SHOOTING, false);

    }

    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    public boolean isShooting() {
        return this.dataTracker.get(SHOOTING);
    }

    public void setShooting(boolean shooting) {
        this.dataTracker.set(SHOOTING, shooting);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D) // Follow range
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D) // Movement speed
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D) // Attack damage
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 120.0D); // Max health
    }

    protected void initGoals() {
        //super.initGoals();

        //Goal Selectors

        this.goalSelector.add(0,new SwimGoal(this));
        // Ranged attack behavior
        this.goalSelector.add(1, new LookAtTargetGoal(this));
        this.goalSelector.add(2, new BossFollowTarget(this,0.75D));
        this.goalSelector.add(3, new BossAttackGoal(this));

        // Add the wander behavior with normal speed 1.0D
        //public WanderAroundGoal(PathAwareEntity entity, double speed, int chance, boolean canDespawn) {
        //this.goalSelector.add(5, new WanderAroundGoal(this, 0.5D,10,false)); must create a custom class
        this.goalSelector.add(7, new BossWanderAroundGoal(this,0.5D,30.0D));
        this.goalSelector.add(8, new LookAroundGoal(this));

        //Target Goals
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 30, true, true, entity -> Math.abs(entity.getY() - this.getY()) <= 30.0));

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


        return PlayState.CONTINUE;
    }

    @Override
    protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
        return super.getLootContextBuilder(causedByPlayer, source);
    }

    @Override
    protected boolean shouldDropLoot() {
        return super.shouldDropLoot();
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        // Always drop the boss head
        this.dropItem(ModItems.BOOS_HEAD);

        // Drop a Wither Skull with a 80% chance
        if (this.random.nextInt(100) < 80) {
            this.dropItem(Items.WITHER_SKELETON_SKULL);
        }

        // Drop a Whip with a 90% chance
        if (this.random.nextInt(100) < 90) {
            this.dropItem(ModItems.RUNIC_WHIP);
        }

        // Drop a Scythe with a 95% chance
        if (this.random.nextInt(100) < 95) {
            this.dropItem(ModItems.SCYTHE);
        }
        super.dropEquipment(source, lootingMultiplier, allowDrops);
    }

    @Override
    public int getXpToDrop() {
        return 100;
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
        if (source.getAttacker() instanceof PlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) source.getAttacker();
            player.incrementStat(Stats.KILLED.getOrCreateStat(this.getType()));
            AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(Objects.requireNonNull(player.getServer()).getAdvancementLoader().get(new Identifier("necropmod:advancements/boss_advancement")));
            if (advancementProgress != null && !advancementProgress.isDone()) {
                advancementProgress.obtain(Objects.requireNonNull(player.getServer().getAdvancementLoader().get(new Identifier("necropmod:advancements/boss_advancement"))).getCriteria().values().iterator().next().toString());
            }
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

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (super.damage(source, amount)) { // boss is hurt
            if (source.getAttacker() instanceof PlayerEntity) { 
                long currentTime = System.currentTimeMillis(); 
                if (currentTime - this.lastTimeSpawned >= 30000) { // 30000 millisec = 30 sec
                    this.spawnSkeletons();
                    this.lastTimeSpawned = currentTime; // reset time
                }
            }
            return true;
        } else {
            return false;
        }
    }


    private void spawnSkeletons() {

        List<String> skeletonNames = Arrays.asList("Skeletor", "Sans", "Revenant", "ShovelBone"
                                                    ,"Papyrus","Leoric","Doot Doot","Undertaker","Skelly");

        Collections.shuffle(skeletonNames);
        //let the player know
        if(this.world instanceof ServerWorld serverWorld){
            serverWorld.getPlayers().forEach(player -> player.sendMessage(Text.of("Fallen Warriors, aid me !"),false));
        }

        for(int i = 0; i < 4; i++){
            SkeletonEntity skeleton = EntityType.SKELETON.create(this.world);
            if(skeleton!=null){

                double offsetX = 2 * Math.random() - 1;
                double offsetZ = 2 * Math.random() - 1;

                skeleton.refreshPositionAfterTeleport(this.getPos().add(offsetX, 0, offsetZ));
                // setting the name
                skeleton.setCustomName(Text.literal(skeletonNames.get(i)));
                skeleton.initialize((ServerWorld)this.world,this.world.getLocalDifficulty(skeleton.getBlockPos()), SpawnReason.EVENT,null,null);
                // Equip the skeleton
                skeleton.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                skeleton.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                skeleton.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
                this.world.spawnEntity(skeleton);
                ((ServerWorld) this.world).spawnParticles(ParticleTypes.WITCH, skeleton.getX(), skeleton.getY() + 0.5, skeleton.getZ(), 10, 0, 0, 0, 0.1);



            }
        }
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }




    //LOOK AT TARGET CUSTOM GOAL

    static class LookAtTargetGoal extends Goal{
        private final BossEntity entity;
        private PlayerEntity target;

        public LookAtTargetGoal(BossEntity entity) {
            this.entity = entity;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.target != null && this.target.isCreative()) {
                return false;
            }
            return true;
        }

        @Override
        public void start() {
            this.target = this.entity.world.getClosestPlayer(this.entity, 20.0);
        }

        @Override
        public boolean shouldContinue() {
            return this.target != null && this.target.isAlive() && this.entity.squaredDistanceTo(this.target) <= 20.0 * 20.0;
        }

        @Override
        public void stop() {
            this.target = null;
        }

        @Override
        public void tick() {
            if (this.target != null) {
                double distanceSquared = this.target.squaredDistanceTo(this.entity);
                if (distanceSquared <= 20.0 * 20.0) {
                    double dX = this.target.getX() - this.entity.getX();
                    double dZ = this.target.getZ() - this.entity.getZ();
                    this.entity.setYaw(-((float) MathHelper.atan2(dX, dZ)) * 57.295776f);
                    this.entity.bodyYaw = this.entity.getYaw();
                }
            }
        }
    }



}