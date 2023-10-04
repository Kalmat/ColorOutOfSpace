package dev.alef.coloroutofspace;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import dev.alef.coloroutofspace.lists.BlockList;
import dev.alef.coloroutofspace.lists.EntityList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class Refs {
	
	public static final String MODID = "coloroutofspace";
	public static final String NAME = "alef's Color out of Space";
	public static final String VERSION = "0.0.1-BETA";
	public static Boolean debug = false;

	public static int difficulty = Refs.NORMAL;
	public static final int NORMAL = 0;
	// HARDCORE: Anitplayer AMPLIFIED strength and FIRE resistant - Infect ALL monsters - Time to cure LIMITED to while Meteorite is Active - Aggressive entities will ALWAYS duplicate
	public static final int HARDCORE = 1;
	
	public static final int daysToFall = 3;
	public static final int graceDaysToFall = 3;
	public static final int explosionRadius = 5;
	public static final int timeIncrease = 6000; 		// 24000 = one day (clock not increasing while sleeping)
	public static final int radiusIncrease = 3;
	public static final int infectRadiusLimit = 60;
	public static final int cureMaxLevel = 20;
	
	public static final int infectedGrassChance = 3;	// 1/x Chance (1 = 100%, 2 = 50%, ...)
	public static final int lootChance = 20;  			// 4/x Item - 2/x Entity - 1/x Water/Lava - (x-6)/x Default (Lepisma/Nothing)
	public static final int spawnDefaultLootChance = 4; // 1/x Chance (1 = 100%, 2 = 50%, ...)
	public static final int enchantabilityChance = 2; 	// 1/x Chance (1 = 100%, 2 = 50%, ...)
	public static final int dupEntityChance = 1;  		// 1/x Chance (1 = 100%, 2 = 50%, ...)
	public static final int dupAggressiveChance = 5;  	// 1/x Chance (1 = 100%, 2 = 50%, ...) - For Zoglins, Guardians and Elder Guardians
	
	public static final BlockState meteoriteState = BlockList.color_meteorite_block.getDefaultState();
	public static final BlockState infectedState = BlockList.color_infected_block.getDefaultState();
	public static final BlockState infectedWoodState = BlockList.color_wood_block.getDefaultState();
	public static final BlockState infectedDirtState = BlockList.color_dirt_block.getDefaultState(); 
	public static final BlockState infectedGrassBlockState = BlockList.color_grass_block.getDefaultState();
	public static final BlockState infectedGrassState = BlockList.color_grass.getDefaultState(); 
	public static final BlockState infectedLeavesState = BlockList.color_leaves_block.getDefaultState();
	public static final BlockState curedMetState = BlockList.color_cured_met_block.getDefaultState();
	public static final BlockState curedState = BlockList.color_cured_block.getDefaultState();
	public static final BlockState nonCuredState = Blocks.MYCELIUM.getDefaultState(); 
	public static final BlockState infectedDoorState = Blocks.CRIMSON_DOOR.getDefaultState();
	public static final BlockState infectedBedState = Blocks.PURPLE_BED.getDefaultState();
	public static final BlockState infectedGlassState = Blocks.PURPLE_STAINED_GLASS.getDefaultState();
	public static final BlockState infectedGlassPaneState = Blocks.PURPLE_STAINED_GLASS_PANE.getDefaultState();
	public static final BlockState infectedStairsState = Blocks.PURPUR_STAIRS.getDefaultState();
	public static final BlockState infectedSlabState = Blocks.PURPUR_SLAB.getDefaultState();
	public static final BlockState infectedTorchState = Blocks.REDSTONE_TORCH.getDefaultState();
	public static final BlockState infectedWallTorchState = Blocks.REDSTONE_WALL_TORCH.getDefaultState();
	
	public static final EntityType<?> defaultLootEntity = EntityType.SILVERFISH;
	
	public static final Color glintColor = new Color(1.0f, 0.3f, 0.7f, 0.1f); //0xFF33E0FB?
	public static final Color waterColor = new Color(0x62529E); // end-water color
	
	public static final String soulsCollectedMsg = "Souls Collected";
	public static final String allSoulsCollectedMsg = "ALL souls Collected!";
	public static final String mineMetMsg = "Harvest Meteorite";
	public static final String metHarvested = "Meteorite harvested";
	public static final String eatMetAntidote = "Eat Color Antidote";
	
	public static final int curedMetSound = 0;
	
	public static final List<String> tagNames = Arrays.asList(
			"Take me to your leader",
			"I come in peace",
			"Running will make it worst",
			"Why don't you kiss me?",
			"I love you!",
			"No way out",
			"Nowhere to hide",
			"You'll be my lunch",
			"Abandon all hope",
			"I am your father",
			"Relax, won't hurt... much",
			"Sayonara, baby",
			"Come with daddy",
			"The restroom, please?",
			"Pay for your sins",
			"Say I'm handsome",
			"See you in Hell",
			"Curiosity killed Steve",
			"(your) Life is overestimated",
			"Can I borrow your blood, please?"
	);
	
	public static final List<Block> modBlockList = Arrays.asList(
			BlockList.color_meteorite_block,
			BlockList.color_infected_block,
			BlockList.color_wood_block,
			BlockList.color_dirt_block,
			BlockList.color_grass_block,
			BlockList.color_grass,
			BlockList.color_leaves_block,
			BlockList.color_cured_met_block,
			BlockList.color_cured_block,
			Refs.infectedBedState.getBlock(),
			Refs.infectedDoorState.getBlock(),
			Refs.infectedGlassState.getBlock(),
			Refs.infectedGlassPaneState.getBlock()
	);
	
	public static final List<Block> unbreakableBlockList = Arrays.asList(
			BlockList.color_meteorite_block,
			BlockList.color_infected_block,
			BlockList.color_wood_block,
			BlockList.color_dirt_block,
			BlockList.color_grass_block
	);
	
	public static final List<Block> modBlocksToCureList = Arrays.asList(
			BlockList.color_infected_block,
			BlockList.color_wood_block,
			BlockList.color_dirt_block,
			BlockList.color_grass_block
	);
	
	public static final List<EntityType<?>> souls = Arrays.asList(
			EntityType.PILLAGER,
			EntityType.WITCH,
			EntityType.WANDERING_TRADER,
			EntityType.VINDICATOR,
			EntityType.ILLUSIONER,
			EntityType.EVOKER,
			EntityList.color_anti_player
	);
	
	public static final List<Item> lootItems = Arrays.asList(
			Items.OBSIDIAN,
			Items.NETHERITE_INGOT,
			Items.MAP,
			Items.GOLDEN_APPLE,
			Items.BEACON,
			Items.TOTEM_OF_UNDYING,
			Items.DIAMOND_SWORD,
			Items.DIAMOND_PICKAXE,
			Items.DIAMOND_AXE,
			Items.DIAMOND,
			Items.DIAMOND_HELMET,
			Items.DIAMOND_CHESTPLATE,
			Items.DIAMOND_LEGGINGS,
			Items.DIAMOND_BOOTS,
			Items.EMERALD,
			Items.IRON_SWORD,
			Items.IRON_PICKAXE,
			Items.IRON_AXE,
			Items.IRON_BLOCK,
			Items.BOW,
			Items.CROSSBOW,
			Items.POTION,
			Items.GOLDEN_HORSE_ARMOR,
			Items.GOLD_BLOCK,
			Items.SADDLE,
			Items.LEAD,
			Items.COMPASS,
			Items.CLOCK,
			Items.REDSTONE_BLOCK,
			Items.NAME_TAG,
			Items.SPLASH_POTION,
			Items.SLIME_BALL,
			Items.FIRE_CHARGE,
			Items.BOOK,
			Items.LAPIS_BLOCK,
			Items.ENDER_PEARL
			//Items.ENDER_EYE,
			//Items.BLAZE_ROD,
			//Items.END_ROD,
			//Items.ELYTRA,
			//Items.END_CRYSTAL,
			//Items.WITHER_ROSE,
			//Items.GHAST_TEAR,
			//Items.IRON_HORSE_ARMOR,
			//Items.IRON_HELMET,
			//Items.IRON_CHESTPLATE,
			//Items.GOLDEN_HELMET,
			//Items.IRON_LEGGINGS,
			//Items.IRON_BOOTS,
			//Items.GOLDEN_CHESTPLATE,
			//Items.GOLDEN_LEGGINGS,
			//Items.GOLDEN_BOOTS,
	);
	
	public static final List<Fluid> nonLootBlocks = Arrays.asList(
			Fluids.LAVA,
			Fluids.WATER
	);

	public static final List<EntityType<?>> lootEntities = Arrays.asList(
			EntityType.CREEPER,
			EntityType.VEX,
			EntityType.HUSK,
			EntityType.PILLAGER,
			EntityType.RAVAGER,
			EntityType.VINDICATOR,
			EntityType.ILLUSIONER,
			EntityType.EVOKER,
			EntityType.LIGHTNING_BOLT,
			EntityType.SLIME,
			EntityType.GIANT,
			EntityType.WITCH
	);
	
	public static final List<EntityType<?>> entitiesToInfect = Arrays.asList(
			EntityType.VILLAGER,
			EntityType.PIG,
			EntityType.COW,
			EntityType.SHEEP,
			EntityType.CAT,
			EntityType.CHICKEN,
			EntityType.PARROT,
			EntityType.SPIDER,
			EntityType.CAVE_SPIDER,
			EntityType.HORSE,
			EntityType.LLAMA,
			EntityType.OCELOT,
			EntityType.FOX,
			EntityType.POLAR_BEAR,
			EntityType.PANDA,
			EntityType.DONKEY,
			EntityType.MULE,
			EntityType.DOLPHIN,
			EntityType.SQUID,
			EntityType.COD,
			EntityType.TURTLE,
			EntityType.WANDERING_TRADER,
			EntityType.TRADER_LLAMA,
			EntityType.RABBIT,
			EntityType.IRON_GOLEM,
			EntityType.SALMON,
			EntityType.PUFFERFISH,
			EntityType.WOLF,
			EntityType.BEE,
			EntityType.TROPICAL_FISH
	);
	
	public static final List<EntityType<?>> infectedEntities = Arrays.asList(
			EntityType.HUSK,
			EntityList.color_brute,
			EntityType.RAVAGER,
			EntityType.SHULKER,
			EntityType.BLAZE,
			EntityType.GHAST,
			EntityType.GHAST,
			EntityType.MAGMA_CUBE,
			EntityType.MAGMA_CUBE,
			EntityType.ZOMBIE_HORSE,
			EntityType.ZOMBIE_HORSE,
			EntityType.STRIDER,
			EntityType.STRIDER,
			EntityType.STRIDER,
			EntityType.STRIDER,
			EntityType.SKELETON_HORSE,
			EntityType.SKELETON_HORSE,
			EntityType.ELDER_GUARDIAN,
			EntityType.ELDER_GUARDIAN,
			EntityType.GUARDIAN,
			EntityType.ELDER_GUARDIAN,
			EntityType.HUSK,
			EntityType.ZOMBIE_HORSE,
			EntityType.ENDERMITE,
			EntityType.BLAZE, 
			EntityType.GUARDIAN,
			EntityType.GUARDIAN,
			EntityType.SHULKER,
			EntityType.VEX,
			EntityType.GUARDIAN
	);
	
	public static final List<EntityType<?>> canBeRidden = Arrays.asList(
			EntityType.ZOMBIE_HORSE,
			EntityType.SKELETON_HORSE,
			EntityType.STRIDER
	);
	
	public static final List<EntityType<?>> infectedDupEntities = Arrays.asList(
			EntityType.HUSK,
			EntityList.color_brute,
			EntityType.field_242287_aj, // PIGLIN BRUTE
			EntityType.PIGLIN,
			EntityType.ZOGLIN,
			EntityType.SHULKER,
			EntityType.GHAST,
			EntityType.MAGMA_CUBE,
			EntityType.ZOMBIE_HORSE,
			EntityType.SKELETON_HORSE,
			EntityType.STRIDER,
			EntityType.ENDERMITE,
			EntityType.BLAZE, 
			EntityType.VEX
	);
	
	public static final List<EntityType<?>> aggressiveEntities = Arrays.asList(
			EntityList.color_brute,
			EntityType.ZOGLIN,
			EntityType.GUARDIAN,
			EntityType.ELDER_GUARDIAN
	);

	public static final int alignUp = 0;
	public static final int alignVCenter = 10;
	public static final int alignDown = 20;
	
	public static final int alignLeft = 0;
	public static final int alignHCenter = 1;
	public static final int alignRight = 2;
	
	public static final int alignUpLeft = alignUp + alignLeft;
    public static final int alignUpCenter = alignUp + alignHCenter;
    public static final int alignUpRight = alignUp + alignRight;
    public static final int alignCenterLeft = alignVCenter + alignLeft;
    public static final int alignCenterCenter = alignVCenter + alignHCenter;
    public static final int alignCenterRight = alignVCenter + alignRight;
    public static final int alignDownLeft = alignDown + alignLeft;
    public static final int alignDownRight = alignDown + alignRight;
	public static final int[] alignList = {alignUpLeft,  alignUpCenter, alignUpRight, alignCenterLeft, alignCenterRight, alignDownLeft, alignDownRight};
}
