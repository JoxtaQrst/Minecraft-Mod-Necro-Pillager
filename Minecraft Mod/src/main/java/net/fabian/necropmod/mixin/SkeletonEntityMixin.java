package net.fabian.necropmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonEntity.class)
public abstract class SkeletonEntityMixin extends LivingEntity {
    public SkeletonEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void onDropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops, CallbackInfo ci) {
        Entity attacker = source.getAttacker();

        // Verifică dacă atacatorul este jucătorul și dacă deține coasa
        if (attacker instanceof PlayerEntity player) {
            ItemStack heldItem = player.getMainHandStack();

            if (heldItem.getItem() == Registries.ITEM.get(new Identifier("necropmod:scythe"))) {
                // Anulează evenimentul de drop implicit
                ci.cancel();

                // Adăugăm drop-urile noastre
                int amount = 1 + this.random.nextInt(4); // generează un număr între 1 și 4
                this.dropItem(new ItemStack(Registries.ITEM.get(new Identifier("necropmod:essence_undeath")), amount).getItem());
            }
        }
    }
}
