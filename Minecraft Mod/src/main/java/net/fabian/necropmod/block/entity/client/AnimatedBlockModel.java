package net.fabian.necropmod.block.entity.client;

import net.fabian.necropmod.NecropMod;
import net.fabian.necropmod.block.entity.AnimatedBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AnimatedBlockModel extends GeoModel<AnimatedBlockEntity> {
    @Override
    public Identifier getModelResource(AnimatedBlockEntity animatable) {
        return new Identifier(NecropMod.MOD_ID,"geo/totem.geo.json");
    }

    @Override
    public Identifier getTextureResource(AnimatedBlockEntity animatable) {
        return new Identifier(NecropMod.MOD_ID,"textures/block/texture_totem.png");
    }

    @Override
    public Identifier getAnimationResource(AnimatedBlockEntity animatable) {
        return new Identifier(NecropMod.MOD_ID,"animations/block_model.animation.json");
    }
}
