package net.fabian.necropmod;

import net.fabian.necropmod.block.entity.ModBlockEntities;
import net.fabian.necropmod.block.entity.client.AnimatedBlockRenderer;
import net.fabian.necropmod.entity.ModEntities;
import net.fabian.necropmod.entity.client.BossProjectileRenderer;
import net.fabian.necropmod.entity.client.BossRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EntityRendererFactory;

public class NecroModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.BOSS, BossRenderer::new);
        EntityRendererRegistry.register(ModEntities.BOSS_PROJECTILE,
                (EntityRendererFactory.Context context) -> new BossProjectileRenderer(context));

        BlockEntityRendererFactories.register(ModBlockEntities.ANIMATED_BLOCK_ENTITY, AnimatedBlockRenderer::new);

    }
}
