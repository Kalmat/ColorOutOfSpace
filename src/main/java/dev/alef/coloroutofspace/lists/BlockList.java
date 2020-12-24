package dev.alef.coloroutofspace.lists;

import java.util.function.ToIntFunction;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.blocks.CuredBlock;
import dev.alef.coloroutofspace.blocks.CuredMetBlock;
import dev.alef.coloroutofspace.blocks.InfectedBlock;
import dev.alef.coloroutofspace.blocks.InfectedDirtBlock;
import dev.alef.coloroutofspace.blocks.InfectedGrassBlock;
import dev.alef.coloroutofspace.blocks.InfectedLeavesBlock;
import dev.alef.coloroutofspace.blocks.InfectedWoodBlock;
import dev.alef.coloroutofspace.blocks.MeteoriteBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockList {
	
    private static ToIntFunction<BlockState> lightValueA = (p_235830_0_) -> {return 12;};
    private static ToIntFunction<BlockState> lightValueB = (p_235830_0_) -> {return 7;};
    public static Block color_meteorite_block = new MeteoriteBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(1000000f, 1000000f).harvestLevel(1000).sound(SoundType.METAL).func_235838_a_(lightValueA));
    public static Block color_infected_block = new InfectedBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(1000000f, 1000000f).harvestLevel(1000).func_235838_a_(lightValueB));
    public static Block color_dirt_block = new InfectedDirtBlock(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.field_241539_ab_).func_235861_h_().hardnessAndResistance(1000000f, 1000000f).harvestLevel(1000).func_235838_a_(lightValueB));
    public static Block color_wood_block = new InfectedWoodBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(1000000f, 1000000f).harvestLevel(1000).func_235838_a_(lightValueB));
    public static Block color_leaves_block = new InfectedLeavesBlock(Block.Properties.create(Material.LEAVES));
    public static Block color_grass_block = new InfectedGrassBlock(AbstractBlock.Properties.create(Material.field_242934_h, MaterialColor.NETHERRACK).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.field_235581_C_));
    public static Block color_cured_met_block = new CuredMetBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.1f, 0.1f).harvestLevel(0).sound(SoundType.METAL));
    public static Block color_cured_block = new CuredBlock(Block.Properties.create(Material.EARTH));
    
    public static final DeferredRegister<Block> BLOCK_LIST = DeferredRegister.create(ForgeRegistries.BLOCKS, Refs.MODID);
    public static final RegistryObject<Block> METEORITE_BLOCK = BLOCK_LIST.register("color_meteorite_block", () -> color_meteorite_block);
    public static final RegistryObject<Block> INFECTED_BLOCK = BLOCK_LIST.register("color_infected_block", () -> color_infected_block);
    public static final RegistryObject<Block> WOOD_BLOCK = BLOCK_LIST.register("color_wood_block", () -> color_wood_block);
    public static final RegistryObject<Block> CURED_MET_BLOCK = BLOCK_LIST.register("color_cured_met_block", () -> color_cured_met_block);
    public static final RegistryObject<Block> CURED_BLOCK = BLOCK_LIST.register("color_cured_block", () -> color_cured_block);
    public static final RegistryObject<Block> LEAVES_BLOCK = BLOCK_LIST.register("color_leaves_block", () -> color_leaves_block);
    public static final RegistryObject<Block> GRASS_BLOCK = BLOCK_LIST.register("color_grass_block", () -> color_grass_block);
    public static final RegistryObject<Block> DIRT_BLOCK = BLOCK_LIST.register("color_dirt_block", () -> color_dirt_block);
}
