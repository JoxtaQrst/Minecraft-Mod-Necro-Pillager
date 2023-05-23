package net.fabian.necropmod.entity;

import net.fabian.necropmod.NecropMod;
import net.fabian.necropmod.entity.custom.BossEntity;
import net.fabian.necropmod.entity.custom.BossProjectile;
import net.fabricmc.fabric.api.command.v2.FabricEntitySelectorReader;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BossEntity> BOSS = Registry.register(

            Registries.ENTITY_TYPE, new Identifier(NecropMod.MOD_ID,"necropillager"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE,BossEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f,1.75f)).build());

    public static final EntityType<BossProjectile> BOSS_PROJECTILE = Registry.register(
           Registries.ENTITY_TYPE, new Identifier(NecropMod.MOD_ID,"boss_projectile"),
            EntityType.Builder.create(BossProjectile::new,SpawnGroup.MISC)
                    .setDimensions(0.5f, 0.5f).build(NecropMod.MOD_ID));
}
