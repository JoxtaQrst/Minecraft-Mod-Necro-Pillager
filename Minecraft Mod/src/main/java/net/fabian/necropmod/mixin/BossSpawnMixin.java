package net.fabian.necropmod.mixin;

import net.fabian.necropmod.entity.ModEntities;
import net.fabian.necropmod.entity.custom.BossEntity;
import net.minecraft.block.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mixin(World.class)
public class BossSpawnMixin {
    private static final Logger LOGGER = LogManager.getLogger("CandleListenerMixin");
    private void spawnExplosionParticles(Vec3d center, int count) {
        World world = (World) (Object) this;
        for (int i = 0; i < count; i++) {
            double offsetX = world.random.nextGaussian() * 0.2;
            double offsetY = world.random.nextGaussian() * 0.2;
            double offsetZ = world.random.nextGaussian() * 0.2;
            world.addParticle(ParticleTypes.CLOUD, center.x, center.y, center.z, offsetX, offsetY, offsetZ);
        }
    }


    @Inject(method = "setBlockState", at = @At("HEAD"))
    public void onSetBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> ci) {
        // Check if the placed block is a lectern
        if (state.getBlock() instanceof LecternBlock) {
            BlockPos lecternPos = pos;
            BlockState lecternState = state;
            LOGGER.info("Lectern Placed at: {}", lecternPos);
            LOGGER.info("Lectern state: {}", lecternState);

            // Calculate left and right positions relative to the lectern
            Direction lecternFacing = lecternState.get(LecternBlock.FACING);
            BlockPos leftPos = lecternPos.offset(lecternFacing.rotateYCounterclockwise());
            BlockPos rightPos = lecternPos.offset(lecternFacing.rotateYClockwise());

            // Check if a Soul Sand block with a lantern on the left side is placed
            BlockState leftState = ((World) (Object) this).getBlockState(leftPos);
            if (leftState.getBlock() == Blocks.SOUL_SAND && ((World) (Object) this).getBlockState(leftPos.up()).getBlock() instanceof LanternBlock) {
                BlockPos lanternPos = leftPos.up();
                BlockState lanternState = ((World) (Object) this).getBlockState(lanternPos);
                LOGGER.info("Soul Sand with Lantern placed at: {}", leftPos);
                LOGGER.info("Lantern state: {}", lanternState);

                // Summon the boss
                LOGGER.info("Spawining boss .. ");
                // Play thunder sounds
                ((World) (Object) this).playSound(null, lecternPos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1.0F, 1.0F);
                ((World) (Object) this).playSound(null, lecternPos, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 1.0F, 1.0F);

                // Spawn the entity behind the lectern
                BlockPos spawnPos = lecternPos.offset(lecternFacing.getOpposite(), 2);
                BossEntity entity = ModEntities.BOSS.create((World) (Object) this);

                if (entity != null) {
                    entity.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0F, 0.0F);
                    ((World) (Object) this).spawnEntity(entity);
                    Vec3d bossPos = entity.getPos();
                    spawnExplosionParticles(bossPos, 50);
                }
                //lecternpos - lectern position
                //lanternpos - lantern postion
                //leftpos   - soul sand position
                ((World) (Object) this).removeBlock(leftPos, false);
                ((World) (Object) this).playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ((World) (Object) this).removeBlock(lanternPos, false);
                ((World) (Object) this).playSound(null, lanternPos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ((World) (Object) this).removeBlock(lecternPos, false);
                ((World) (Object) this).playSound(null, lecternPos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            // Check if a Soul Sand block with a lantern on the right side is placed
            BlockState rightState = ((World) (Object) this).getBlockState(rightPos);
            if (rightState.getBlock() == Blocks.SOUL_SAND && ((World) (Object) this).getBlockState(rightPos.up()).getBlock() instanceof LanternBlock) {
                BlockPos lanternPos = rightPos.up();
                BlockState lanternState = ((World) (Object) this).getBlockState(lanternPos);
                LOGGER.info("Soul Sand with Lantern placed at: {}", rightPos);
                LOGGER.info("Lantern state: {}", lanternState);

                // Summon the boss entity
                LOGGER.info("Spawining boss .. ");

                // Play thunder sounds
                ((World) (Object) this).playSound(null, lecternPos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1.0F, 1.0F);
                ((World) (Object) this).playSound(null, lecternPos, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 1.0F, 1.0F);

                // Spawn the entity behind the lectern
                BlockPos spawnPos = lecternPos.offset(lecternFacing.getOpposite(), 2);
                BossEntity entity = ModEntities.BOSS.create((World) (Object) this);

                if (entity != null) {
                    entity.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0F, 0.0F);
                    ((World) (Object) this).spawnEntity(entity);
                    Vec3d bossPos = entity.getPos();
                    spawnExplosionParticles(bossPos, 20);
                }

                //lecternpos - lectern position
                //lanternpos - lantern postion
                //rightpos   - soul sand position
                ((World) (Object) this).removeBlock(rightPos, false);
                ((World) (Object) this).playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ((World) (Object) this).removeBlock(lanternPos, false);
                ((World) (Object) this).playSound(null, lanternPos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ((World) (Object) this).removeBlock(lecternPos, false);
                ((World) (Object) this).playSound(null, lecternPos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }




}
