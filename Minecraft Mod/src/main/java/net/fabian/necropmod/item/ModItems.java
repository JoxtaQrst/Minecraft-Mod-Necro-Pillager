package net.fabian.necropmod.item;

import net.fabian.necropmod.NecropMod;
import net.fabian.necropmod.block.ModBlocks;
import net.fabian.necropmod.entity.ModEntities;
import net.fabian.necropmod.item.custom.AnimatedBlockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item BOSS_SPAWN_EGG = registerItem("boss_egg",
            new SpawnEggItem(ModEntities.BOSS, 0x4C2C66, 0x319E46,new Boss_Egg(new FabricItemSettings())),ModItemGroup.NECRO_GROUP
    );

    public static final Item RUNIC_WHIP = registerItem("runic_whip",
            new RunicWhip(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            ModItemGroup.NECRO_GROUP
    );

    public static final Item ESSENCE_UNDEATH = registerItem("essence_undeath",
            new EssenceOfUndeath(new Item.Settings().rarity(Rarity.RARE)),
            ModItemGroup.NECRO_GROUP
    );

    public static final Item BOOS_HEAD = registerItem("boss_head",
            new BossHead(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            ModItemGroup.NECRO_GROUP
    );

    public static final Item SCYTHE = registerItem("scythe",
            new Scythe(new Item.Settings().rarity(Rarity.EPIC)),
            ModItemGroup.NECRO_GROUP
    );

    public static final Item TOTEM_ITEM = registerItem("totem",
            new AnimatedBlockItem(ModBlocks.TOTEM_UNDEATH, new FabricItemSettings()),ModItemGroup.NECRO_GROUP);


    private static Item registerItem(String name, Item item, ItemGroup group) {
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, new Identifier(NecropMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        NecropMod.LOGGER.debug("Registering Mod items for " + NecropMod.MOD_ID);
    }
}
