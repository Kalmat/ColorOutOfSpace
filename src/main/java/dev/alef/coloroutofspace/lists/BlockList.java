package dev.alef.coloroutofspace.lists;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.block.CuredBlock;
import dev.alef.coloroutofspace.block.CuredMetBlock;
import dev.alef.coloroutofspace.block.InfectedBlock;
import dev.alef.coloroutofspace.block.InfectedDirtBlock;
import dev.alef.coloroutofspace.block.InfectedGrass;
import dev.alef.coloroutofspace.block.InfectedGrassBlock;
import dev.alef.coloroutofspace.block.InfectedLeavesBlock;
import dev.alef.coloroutofspace.block.InfectedWoodBlock;
import dev.alef.coloroutofspace.block.MeteoriteBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockList {
	
    public static final Block color_meteorite_block = new MeteoriteBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F).noDrops().sound(SoundType.METAL).setLightLevel((state) -> { return 12; }));
    public static final Block color_infected_block = new InfectedBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 3600000.0F).noDrops().setLightLevel((state) -> { return 7; }));
    public static final Block color_wood_block = new InfectedWoodBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(-1.0F, 3600000.0F).noDrops().setLightLevel((state) -> { return 7; }));
    public static final Block color_dirt_block = new InfectedDirtBlock(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.NETHERRACK).hardnessAndResistance(-1.0F, 3600000.0F).noDrops().tickRandomly().setLightLevel((state) -> { return 7; }));
    public static final Block color_grass_block = new InfectedGrassBlock(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.CRIMSON_NYLIUM).hardnessAndResistance(-1.0F, 3600000.0F).noDrops().tickRandomly().setLightLevel((state) -> { return 7; }));
    public static final Block color_grass = new InfectedGrass(AbstractBlock.Properties.create(Material.NETHER_PLANTS, MaterialColor.NETHERRACK).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.NETHER_SPROUT));
    public static final Block color_leaves_block = new InfectedLeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).notSolid().sound(SoundType.PLANT));
    public static final Block color_cured_met_block = new CuredMetBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.4f, 0.4f).harvestLevel(0).sound(SoundType.METAL));
    public static final Block color_cured_block = new CuredBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(0.6F).harvestLevel(0).sound(SoundType.GROUND));
    
    public static final DeferredRegister<Block> BLOCK_LIST = DeferredRegister.create(ForgeRegistries.BLOCKS, Refs.MODID);
    public static final RegistryObject<Block> METEORITE_BLOCK = BLOCK_LIST.register("color_meteorite_block", () -> color_meteorite_block);
    public static final RegistryObject<Block> INFECTED_BLOCK = BLOCK_LIST.register("color_infected_block", () -> color_infected_block);
    public static final RegistryObject<Block> INFECTED_WOOD_BLOCK = BLOCK_LIST.register("color_wood_block", () -> color_wood_block);
    public static final RegistryObject<Block> INFECTED_DIRT_BLOCK = BLOCK_LIST.register("color_dirt_block", () -> color_dirt_block);
    public static final RegistryObject<Block> INFECTED_GRASS_BLOCK = BLOCK_LIST.register("color_grass_block", () -> color_grass_block);
    public static final RegistryObject<Block> INFECTED_GRASS = BLOCK_LIST.register("color_grass", () -> color_grass);
    public static final RegistryObject<Block> INFECTED_LEAVES_BLOCK = BLOCK_LIST.register("color_leaves_block", () -> color_leaves_block);
    public static final RegistryObject<Block> CURED_MET_BLOCK = BLOCK_LIST.register("color_cured_met_block", () -> color_cured_met_block);
    public static final RegistryObject<Block> CURED_BLOCK = BLOCK_LIST.register("color_cured_block", () -> color_cured_block);

    public static void registerBlockRenderers() {
		RenderTypeLookup.setRenderLayer(color_grass, RenderType.getCutout());
		//RenderTypeLookup.setRenderLayer(color_leaves_block, RenderType.getSolid());
    }
}
