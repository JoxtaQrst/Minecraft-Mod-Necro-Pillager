package net.fabian.necropmod.data.advancements;

import net.fabian.necropmod.entity.ModEntities;
import net.fabian.necropmod.entity.custom.BossEntity;
import net.fabian.necropmod.item.ModItems;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<Advancement>> {

    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.Builder.create()
                .display(
                        new ItemStack(ModItems.BOOS_HEAD),
                        Text.of("Necro-Boom!"),
                        Text.of("You've vanquished the mighty Pillager Necromancer and put an end to their mischievous necromancy! Congratulations!"),
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .criterion("killed_boss", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(ModEntities.BOSS)))
                .build(consumer, "necropmod:advancements/boss_advancement");
    }
}
