package net.fabian.necropmod.item;

import net.fabian.necropmod.NecropMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup NECRO_GROUP = FabricItemGroup.builder(new Identifier(NecropMod.MOD_ID))
            .displayName(Text.literal("Necro Pillager Mod"))
            .icon(() -> new ItemStack(ModItems.RUNIC_WHIP))
            .build();
}
