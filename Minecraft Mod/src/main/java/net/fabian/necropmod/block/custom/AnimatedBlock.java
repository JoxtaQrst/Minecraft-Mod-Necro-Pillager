package net.fabian.necropmod.block.custom;

import net.fabian.necropmod.block.entity.AnimatedBlockEntity;
import net.fabian.necropmod.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class AnimatedBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static int totemCapacity = 100;

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
        return new AnimatedBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        //super.onBreak(world, pos, state, player);

        if (world.isClient) {
            spawnCustomBreakParticles(world, pos);
        }
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);

        if (world.isClient) {
            spawnCustomBreakParticles(world, pos);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            world.playSound(null, pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            spawnCustomBreakParticles(world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    protected void spawnCustomBreakParticles(World world, BlockPos pos) {
        for (int i = 0; i < 30; ++i) {
            double x = pos.getX() + world.random.nextDouble();
            double y = pos.getY() + world.random.nextDouble();
            double z = pos.getZ() + world.random.nextDouble();
            world.addParticle(ParticleTypes.WITCH, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int currentCapacity = getTotemCapacity();
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            //WHEN PLAYER HAS ESSENCE OF UNDEATH
            ItemStack heldItem = player.getStackInHand(hand);
            if(!heldItem.isEmpty() && heldItem.getItem() == ModItems.ESSENCE_UNDEATH){
               if(currentCapacity < 100 ){

                   setTotemCapacity(currentCapacity+10);
                   String remainingCapacity = "Totem Capacity: ";
                   remainingCapacity = remainingCapacity + currentCapacity;
                   //Send message to player
                   player.sendMessage(Text.of(remainingCapacity), true);

                   heldItem.decrement(1);
                   world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                   // Spawn particle effects
                   for (int i = 0; i < 5; ++i) {
                       double x = pos.getX() + world.random.nextDouble();
                       double y = pos.getY() + world.random.nextDouble();
                       double z = pos.getZ() + world.random.nextDouble();
                       world.addParticle(ParticleTypes.ANGRY_VILLAGER, x, y, z, 0.0D, 0.0D, 0.0D);
                   }
               }
            }
            else if(heldItem.isEmpty()){
                //PLAYER EMPTY-HANDED
                if (currentCapacity > 0) {
                    spawnIronGolems(world, pos);

                    // Decrease the totem capacity by 25
                    currentCapacity = currentCapacity - 25;
                    setTotemCapacity(currentCapacity);

                    // Announce the player the remaining capacity
                    String remainingCapacity = "Totem Capacity: ";
                    remainingCapacity = remainingCapacity + currentCapacity;
                    player.sendMessage(Text.of(remainingCapacity), true);

                    // Play sound effects
                    world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    // Spawn particle effects
                    for (int i = 0; i < 10; ++i) {
                        double x = pos.getX() + world.random.nextDouble();
                        double y = pos.getY() + world.random.nextDouble();
                        double z = pos.getZ() + world.random.nextDouble();
                        world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
                    }

                    return ActionResult.SUCCESS;
                } else {
                    // Destroy the block if the capacity is 0s
                    spawnCustomBreakParticles(world, pos);
                    world.breakBlock(pos, true);
                    return ActionResult.SUCCESS;

                }
            }

        }

        return ActionResult.PASS;
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        spawnCustomBreakParticles((World) world, pos);
        super.onBroken(world, pos, state);
    }

    protected void spawnIronGolems(World world, BlockPos pos) {
        for (int i = 0; i < 2; ++i) {
            IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.create(world);
            if (ironGolemEntity != null) {
                double offsetX = 1.5 * Math.cos((2 * Math.PI * i) / 2);
                double offsetZ = 1.5 * Math.sin((2 * Math.PI * i) / 2);
                ironGolemEntity.refreshPositionAndAngles(pos.getX() + 0.5 + offsetX, pos.getY() + 1.0, pos.getZ() + 0.5 + offsetZ, 0.0F, 0.0F);
                ironGolemEntity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
                ironGolemEntity.setCustomName(Text.literal("Iron Crusader"));
                ironGolemEntity.world.spawnEntity(ironGolemEntity);
            }
        }
    }


    protected int getTotemCapacity() {
        return totemCapacity;
    }

    protected void setTotemCapacity(int capacity) {
        totemCapacity = capacity;
    }


}

