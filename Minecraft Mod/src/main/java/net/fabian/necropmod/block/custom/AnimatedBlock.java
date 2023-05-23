package net.fabian.necropmod.block.custom;

import net.fabian.necropmod.block.entity.AnimatedBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class AnimatedBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public AnimatedBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AnimatedBlockEntity(pos,state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        super.onBreak(world, pos, state, player);

        if (world.isClient()) {
            for (int i = 0; i < 30; ++i) {
                double d = world.getRandom().nextGaussian() * 0.02D;
                double e = world.getRandom().nextGaussian() * 0.02D;
                double f = world.getRandom().nextGaussian() * 0.02D;
                world.addParticle(ParticleTypes.ENCHANT, (double)pos.getX() + world.getRandom().nextDouble(), (double)pos.getY() + world.getRandom().nextDouble(), (double)pos.getZ() + world.getRandom().nextDouble(), d, e, f);
            }
        }
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        spawnCustomBreakParticles(world, player, pos, state);
        super.afterBreak(world, player, pos, state, blockEntity, tool);
    }

    protected void spawnCustomBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        // Cancel the default particles
         world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
        // Add custom particles here
         world.addParticle(ParticleTypes.WITCH, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
    }


}

