package net.fabian.necropmod.item;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RunicWhip extends Item {

    private static final int COOLDOWN = 200; // ticks/time
    private boolean isHorseSpawned = false;
    private Entity necroHorse;

    public RunicWhip(Settings settings) {
        super(settings);
        registerHorseDeathEventHandler();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.world.isClient) {
            // Check if cooldown is done
            if (!user.getItemCooldownManager().isCoolingDown(this)) {
                if (entity instanceof MobEntity) {
                    if (!isHorseSpawned) { // Check if horse has already been spawned
                        necroHorse = spawnUndeadHorse(user.world, user, entity);
                        entity.kill();
                        user.world.playSound(null, user.getX(), user.getY(), user.getZ(),
                                SoundEvents.ENTITY_HORSE_DEATH, SoundCategory.PLAYERS, 1.0f, 0.1f);
                        user.getItemCooldownManager().set(this, COOLDOWN);
                        user.world.playSound(null, user.getX(), user.getY(), user.getZ(),
                                SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        isHorseSpawned = true; // Set the flag to true after spawning the horse
                        user.sendMessage(Text.literal("Something for you to ride that"), true);
                        user.giveItemStack(new ItemStack(Items.SADDLE));
                    } else {
                        user.sendMessage(Text.literal("Only one horse can be spawned at a time."), true);
                        if (necroHorse instanceof LivingEntity) {
                            ((LivingEntity) necroHorse).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 600, 0, false, false));
                        }
                    }
                } else {
                    user.sendMessage(Text.literal("The Runic Whip can only be used on mobs."), true);
                }
            } else {
                user.sendMessage(Text.literal("The whip is still on cooldown."), true);
            }
        }

        return ActionResult.SUCCESS;
    }

    private Entity spawnUndeadHorse(World world, PlayerEntity player, Entity entity) {
        HorseEntity horse = EntityType.HORSE.create(world);
        horse.setVariant(HorseColor.BLACK);
        horse.setTame(true);
        horse.setCustomName(Text.literal("Dreadsteed"));
        horse.setOwnerUuid(player.getUuid());
        horse.setHealth(20.0f);
        horse.setHealth(horse.getMaxHealth()); // Set horse's health to maximum

        // Set horse's position
        horse.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());

        world.spawnEntity(horse);

        return horse;
    }

    private void registerHorseDeathEventHandler() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof HorseEntity && isHorseSpawned && !entity.isLiving()) {
                isHorseSpawned = false;
            }
        });
    }
}

