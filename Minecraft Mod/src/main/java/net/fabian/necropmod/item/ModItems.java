package net.fabian.necropmod.item;

import net.fabian.necropmod.NecropMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.fabian.necropmod.item.RunicWhip;

public class ModItems {

    public static final Item RUNIC_WHIP = registerItem("runic_whip",
            new RunicWhip(new Item.Settings()),
            ModItemGroup.NECRO_GROUP
    );


    private static Item registerItem(String name, Item item, ItemGroup group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, new Identifier(NecropMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        NecropMod.LOGGER.debug("Registering Mod items for " + NecropMod.MOD_ID);
    }
}
