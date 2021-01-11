package dev.alef.coloroutofspace.lists;

import java.util.function.ToIntFunction;

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
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockList {
	
    private static ToIntFunction<BlockState> lightValueA = (p_235830_0_) -> {return 12;};
    private static ToIntFunction<BlockState> lightValueB = (p_235830_0_) -> {return 7;};
    public static final Block color_meteorite_block = new MeteoriteBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(1000000f, 1000000f).harvestLevel(1000).sound(SoundType.METAL).setLightLevel(lightValueA));
    public static final Block color_infected_block = new InfectedBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(1000000f, 1000000f).setRequiresTool().harvestLevel(1000).setLightLevel(lightValueB));
    public static final Block color_wood_block = new InfectedWoodBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(1000000f, 1000000f).setRequiresTool().harvestLevel(1000).setLightLevel(lightValueB));
    public static final Block color_dirt_block = new InfectedDirtBlock(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.NETHERRACK).setRequiresTool().hardnessAndResistance(1000000f, 1000000f).harvestLevel(1000).tickRandomly().setLightLevel(lightValueB));
    public static final Block color_grass_block = new InfectedGrassBlock(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.CRIMSON_NYLIUM).setRequiresTool().hardnessAndResistance(1000000f, 1000000f).harvestLevel(1000).tickRandomly().setLightLevel(lightValueB));
    public static final Block color_grass = new InfectedGrass(AbstractBlock.Properties.create(Material.NETHER_PLANTS, MaterialColor.NETHERRACK).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.NETHER_SPROUT));
    public static final Block color_leaves_block = new InfectedLeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).sound(SoundType.PLANT).notSolid());
    public static final Block color_cured_met_block = new CuredMetBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.4f, 0.4f).harvestLevel(0).sound(SoundType.METAL));
    public static final Block color_cured_block = new CuredBlock(AbstractBlock.Properties.create(Material.ORGANIC, MaterialColor.PURPLE).hardnessAndResistance(0.6F).sound(SoundType.PLANT));
    
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
