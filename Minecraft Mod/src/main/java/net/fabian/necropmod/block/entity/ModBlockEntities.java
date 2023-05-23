package net.fabian.necropmod.block.entity;

import net.fabian.necropmod.NecropMod;
import net.fabian.necropmod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<AnimatedBlockEntity> ANIMATED_BLOCK_ENTITY;

    public static void registerAllBlockEntities() {
        ANIMATED_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(NecropMod.MOD_ID,"texture_totem"),
                FabricBlockEntityTypeBuilder.create(AnimatedBlockEntity::new,ModBlocks.TOTEM_UNDEATH).build());
    }
}
