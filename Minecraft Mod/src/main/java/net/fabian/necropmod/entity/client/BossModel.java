package net.fabian.necropmod.entity.client;

import net.fabian.necropmod.NecropMod;
import net.fabian.necropmod.entity.custom.BossEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BossModel extends GeoModel<BossEntity> {
    @Override
    public Identifier getModelResource(BossEntity animatable) {
        return new Identifier(NecropMod.MOD_ID,"geo/boss.geo.json");
    }

    @Override
    public Identifier getTextureResource(BossEntity animatable) {
        return new Identifier(NecropMod.MOD_ID,"textures/entity/necropillager.png");
    }

    @Override
    public Identifier getAnimationResource(BossEntity animatable) {
        return new Identifier(NecropMod.MOD_ID,"animations/model.animation.json");
    }

    @Override
    public void setCustomAnimations(BossEntity animatable, long instanceId, AnimationState<BossEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head3");

        if(head!=null){
            EntityModelData entityModelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityModelData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityModelData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
