package net.fabian.necropmod.item;


import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;


public class RunicWhip extends Item {

    private static final int COOLDOWN = 200; // ticks/time
    private boolean isHorseSpawned = false;
    private SkeletonHorseEntity necroHorse;
    public RunicWhip(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(!user.world.isClient)
        {
            //Check if cooldown is done
            if(!user.getItemCooldownManager().isCoolingDown(this)){
                if(entity instanceof MobEntity){
                    if (!isHorseSpawned) { // Check if horse has already been spawned
                        necroHorse = spawnUndeadHorse(user.world, user, entity);
                        entity.kill();
                        user.world.playSound(null, user.getX(), user.getY(), user.getZ(),
                                SoundEvents.ENTITY_HORSE_DEATH, SoundCategory.PLAYERS, 1.0f, 0.1f);
                        user.getItemCooldownManager().set(this, COOLDOWN);
                        user.world.playSound(null, user.getX(), user.getY(), user.getZ(),
                                SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        isHorseSpawned = true; // Set the flag to true after spawning the horse
                    } else {
                        user.sendMessage(Text.literal("Only one horse can be spawned at a time."), true);
                        necroHorse.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 600, 0, false, false));

                    }
                }
                else {
                    user.sendMessage(Text.literal("The Runic Whip can only be used on mobs."), true);
                }
            }
            else{
                user.sendMessage(Text.literal("The whip is still on cooldown."),true);
            }

        }
        return ActionResult.SUCCESS;
    }

    private SkeletonHorseEntity spawnUndeadHorse(World world, PlayerEntity player, Entity entity)
    {
        SkeletonHorseEntity horse = EntityType.SKELETON_HORSE.create(world);
        horse.setTame(true);
        horse.setCustomName(Text.literal("Roach"));
        horse.setOwnerUuid(player.getUuid());
        horse.setHealth(320);

        horse.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.SADDLE));

        horse.setPosition(entity.getX(), entity.getY(), entity.getZ());
        world.spawnEntity(horse);
        return horse;

    }
}
