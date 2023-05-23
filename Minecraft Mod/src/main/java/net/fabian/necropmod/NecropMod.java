package net.fabian.necropmod;

import net.fabian.necropmod.block.ModBlocks;
import net.fabian.necropmod.block.entity.ModBlockEntities;
import net.fabian.necropmod.entity.ModEntities;
import net.fabian.necropmod.entity.custom.BossEntity;
import net.fabian.necropmod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class NecropMod implements ModInitializer {

	public static final String MOD_ID = "necropmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModBlockEntities.registerAllBlockEntities();
		GeckoLib.initialize();
		FabricDefaultAttributeRegistry.register(ModEntities.BOSS, BossEntity.setAttributes());
	}
}
