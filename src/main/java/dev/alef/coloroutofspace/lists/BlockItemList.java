package dev.alef.coloroutofspace.lists;

import dev.alef.coloroutofspace.Refs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.item.BlockItem;

public class BlockItemList {
	
    public static final DeferredRegister<Item> BLOCKITEM_LIST = DeferredRegister.create(ForgeRegistries.ITEMS, Refs.MODID);
    public static final RegistryObject<Item> METEORITE_BLOCK_ITEM = BLOCKITEM_LIST.register("color_meteorite_block", () -> new BlockItem(BlockList.METEORITE_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<Item> INFECTED_BLOCK_ITEM = BLOCKITEM_LIST.register("color_infected_block", () -> new BlockItem(BlockList.INFECTED_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<Item> WOOD_BLOCK_ITEM = BLOCKITEM_LIST.register("color_wood_block", () -> new BlockItem(BlockList.INFECTED_WOOD_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<Item> DIRT_BLOCK_ITEM = BLOCKITEM_LIST.register("color_dirt_block", () -> new BlockItem(BlockList.INFECTED_DIRT_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<Item> GRASS_BLOCK_ITEM = BLOCKITEM_LIST.register("color_grass_block", () -> new BlockItem(BlockList.INFECTED_GRASS_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<Item> GRASS_ITEM = BLOCKITEM_LIST.register("color_grass", () -> new BlockItem(BlockList.INFECTED_GRASS.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
    public static final RegistryObject<Item> LEAVES_BLOCK_ITEM = BLOCKITEM_LIST.register("color_leaves_block", () -> new BlockItem(BlockList.INFECTED_LEAVES_BLOCK.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
    public static final RegistryObject<Item> CURED_MET_BLOCK_ITEM = BLOCKITEM_LIST.register("color_cured_met_block", () -> new BlockItem(BlockList.CURED_MET_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<Item> CURED_BLOCK_ITEM = BLOCKITEM_LIST.register("color_cured_block", () -> new BlockItem(BlockList.CURED_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
}
