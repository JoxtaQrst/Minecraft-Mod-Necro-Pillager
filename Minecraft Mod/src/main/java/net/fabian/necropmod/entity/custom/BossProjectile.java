package net.fabian.necropmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BossProjectile extends ProjectileEntity {
    private LivingEntity targetEntity;
    private static BossEntity owner;

    public BossProjectile(EntityType<? extends BossProjectile> entityType, World world) {
        super(entityType, world);
    }

    public void setTarget(LivingEntity target) {
        this.targetEntity = target;
    }

    public static void setShooter(BossEntity boss){
        owner = boss;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        // Check if the projectile hit an entity
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            System.out.println("Hit the Player!");
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity target = entityHitResult.getEntity();

            if (target instanceof LivingEntity) {
                LivingEntity livingTarget = (LivingEntity) target;

                // Same actions as in your previous onEntityHit method
                if (livingTarget.isBlocking() && livingTarget.getActiveItem().equals(ItemStack.EMPTY)) {
                    this.world.playSound(null,this.getX(),this.getY(),this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,this.getSoundCategory(), 1.0F, 0.5F);
                }
                livingTarget.damage(BossDamageSources.getProjectileSource(this, owner), 5.0F);
                livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 30));
                this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 2.0F, World.ExplosionSourceType.NONE);
                this.world.emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));

            }
        }

        // Remove the projectile after it hits something
        this.remove(Entity.RemovalReason.DISCARDED);
    }


    @Override
    public void tick() {

        // Update the projectile's motion to move towards the target
        if (this.targetEntity != null ) {
            this.emitGameEvent(GameEvent.PROJECTILE_SHOOT, owner);
            this.hasNoGravity();
            double deltaX = this.targetEntity.getX() - this.getX();
            double deltaY = this.targetEntity.getBodyY(0.5D) - this.getY();
            double deltaZ = this.targetEntity.getZ() - this.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            double speed = 0.05D; // Adjust the speed of the projectile
            if(doesNotCollide(deltaX,deltaY,deltaZ)){

                // Set the velocity of the projectile
                double velocityX = deltaX / distance * speed;
                double velocityY = deltaY / distance * speed;
                double velocityZ = deltaZ / distance * speed;
                this.setVelocity(velocityX, velocityY, velocityZ);

                // Manually update the projectile's position based on its velocity
                this.updatePosition(this.getX() + velocityX, this.getY() + velocityY, this.getZ() + velocityZ);

                System.out.println();
                System.out.println("deltaX: " + deltaX + ", deltaY: " + deltaY + ", deltaZ: " + deltaZ);
                System.out.println("distance: " + distance);
                System.out.println("velocity: " + (deltaX / distance * speed) + ", " + (deltaY / distance * speed) + ", " + (deltaZ / distance * speed));
                System.out.println();

            }

        }

        if (this.age >= 200) {
            this.remove(Entity.RemovalReason.DISCARDED);
        }

    }


    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }


    @Override
    protected void initDataTracker() {
        // Initialize data tracker if needed
    }
}
