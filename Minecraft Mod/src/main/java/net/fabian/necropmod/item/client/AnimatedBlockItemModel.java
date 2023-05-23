package net.fabian.necropmod.item.client;

import net.fabian.necropmod.NecropMod;
import net.fabian.necropmod.block.entity.AnimatedBlockEntity;
import net.fabian.necropmod.item.custom.AnimatedBlockItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AnimatedBlockItemModel extends GeoModel<AnimatedBlockItem> {
    @Override
    public Identifier getModelResource(AnimatedBlockItem animatable) {
        return new Identifier(NecropMod.MOD_ID,"geo/totem.geo.json");
    }

    @Override
    public Identifier getTextureResource(AnimatedBlockItem animatable) {
        return new Identifier(NecropMod.MOD_ID,"textures/block/texture_totem.png");
    }

    @Override
    public Identifier getAnimationResource(AnimatedBlockItem animatable) {
        return new Identifier(NecropMod.MOD_ID,"animations/block_model.animation.json");
    }
}
