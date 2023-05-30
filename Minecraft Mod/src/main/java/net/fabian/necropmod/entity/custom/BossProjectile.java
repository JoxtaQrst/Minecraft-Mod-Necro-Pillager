package net.fabian.necropmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

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
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        Entity target = entityHitResult.getEntity();
        if (target instanceof LivingEntity livingTarget) {
            // damage
            livingTarget.damage(world.getDamageSources().magic(), 2.0F);

        }
        this.world.createExplosion(this, this.getX(), this.getBodyY(0.5D), this.getZ(), 2.0F, World.ExplosionSourceType.NONE);


        // Elimin proiectilul
        this.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        // Eliminați proiectilul după ce a avut o coliziune
        this.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void tick() {
        super.tick();
        // Actualizați poziția proiectilului în funcție de viteză și direcție
        this.move(MovementType.SELF, this.getVelocity());
        spawnProjectileTrailParticles(this);
        // Verificați dacă proiectilul a atins jucătorul
        if (this.targetEntity instanceof PlayerEntity player && this.squaredDistanceTo(this.targetEntity) < 1.0D) {

            // Check if the player is blocking with a shield
            if (player.isBlocking() && player.getActiveItem().getItem() instanceof ShieldItem) {
                // Play a sound to indicate that the projectile was blocked
                //Durabiliy of the shield
                player.getActiveItem().damage(1,player,(p) -> p.sendToolBreakStatus(player.getActiveHand()));

                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Eliminați proiectilul după ce a fost blocat de către jucător
                this.remove(Entity.RemovalReason.DISCARDED);
                return;
            }

            // Apply damage and effects to the player if not blocked
            this.targetEntity.damage(world.getDamageSources().magic(), 2.0F);
            this.targetEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40, 1));


            // Eliminați proiectilul după ce a atins jucătorul
            this.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private void spawnProjectileTrailParticles(BossProjectile projectile) {
        if (projectile.world instanceof ServerWorld serverWorld) {

            // Spawn particles around the projectile
            int particleCount = 5;
            double trailRadius = 0.2; // Adjust the radius of the particle trail

            for (int i = 0; i < particleCount; i++) {
                double angle = ((double) i / particleCount) * 2.0 * Math.PI;
                double offsetX = Math.cos(angle) * 0;
                double offsetY = Math.sin(angle) * trailRadius;

                serverWorld.spawnParticles(ParticleTypes.SOUL,
                        projectile.getX() + offsetX, projectile.getY() + offsetY, projectile.getZ() + offsetX,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
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