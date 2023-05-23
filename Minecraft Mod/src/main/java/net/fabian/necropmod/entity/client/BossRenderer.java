package net.fabian.necropmod.entity.client;

import net.fabian.necropmod.NecropMod;
import net.fabian.necropmod.entity.custom.BossEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

public class BossRenderer extends GeoEntityRenderer<BossEntity> {
        public BossRenderer(EntityRendererFactory.Context context) {
            super(context, new BossModel());
            this.shadowRadius = 0.4F; // Shadow size
        }
}
