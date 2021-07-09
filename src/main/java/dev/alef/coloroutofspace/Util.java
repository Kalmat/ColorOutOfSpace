package dev.alef.coloroutofspace;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.lists.EntityList;
import dev.alef.coloroutofspace.lists.ItemList;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.ForgeRegistries;

public class Util {
	  
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final List<Potion> potions = new ArrayList<Potion>(ForgeRegistries.POTION_TYPES.getValues());
	public static final List<Enchantment> enchantments = new ArrayList<Enchantment>(ForgeRegistries.ENCHANTMENTS.getValues());
	public static final List<Structure<?>> structures = new ArrayList<Structure<?>>(ForgeRegistries.STRUCTURE_FEATURES.getValues());
	
	public static BlockPos getGroundLevel(World worldIn, BlockPos pos, boolean afterExplosion) {
		
		if (worldIn.canBlockSeeSky(pos)) {
			for (int i = 0; i < worldIn.getHeight(); ++i) {
				if (!worldIn.canBlockSeeSky(pos.down()) || (afterExplosion && worldIn.getBlockState(pos.down()).isSolid())) {
					return pos;
				}
				pos = pos.down();
			}
		}
		else {
			for (int i = 0; i < worldIn.getHeight(); ++i) {                          
				pos = pos.up();
				if (worldIn.canBlockSeeSky(pos.down())) {
					return pos;
				}
			}
		}
		return pos;
	}
	
	public static void spawnItem(World worldIn, PlayerEntity player, BlockPos pos, Item spawnItem, boolean enchantAll, boolean enchantBooks, int enchantChance) {
		
		ItemStack stack = new ItemStack(spawnItem);

		if (spawnItem.equals(Items.POTION) || spawnItem.equals(Items.SPLASH_POTION)) {
			stack  = Util.getPotion(worldIn, player, pos, spawnItem);
		}
		else if (spawnItem.equals(Items.MAP)) {
			stack = Util.getMap(worldIn, player, pos, spawnItem);
		}
		else if (spawnItem.equals(Items.BOOK) && (enchantBooks || enchantAll)) {
			stack = Util.enchantItem(worldIn, player, pos, spawnItem, 1);
		}
		else if (stack.isEnchantable() && enchantAll) {
			stack = Util.enchantItem(worldIn, player, pos, spawnItem, enchantChance);
		}
		ItemEntity spawnItemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
		spawnItemEntity.entityDropItem(stack);
	}
	
	public static ItemStack getPotion(World worldIn, PlayerEntity player, BlockPos pos, Item potionItem) {
        Potion potion = Util.getRandomPotion();
        PotionEntity potionentity = new PotionEntity(worldIn, pos.getX(), pos.getY(), pos.getZ());
        potionentity.setItem(PotionUtils.addPotionToItemStack(new ItemStack(potionItem), potion));
        ItemStack stack = potionentity.getItem();
        return stack;
	}
	
	public static ItemStack getMap(World worldIn, PlayerEntity player, BlockPos pos, Item mapItem) {

		List<Object> retValues = Util.getRandomStructure(worldIn, pos, 96);
		BlockPos structPos = (BlockPos) retValues.get(0);
		String structName = "Surroundings Map";
		ItemStack stack;

		if (structPos == null) {
			structPos = pos;
	        stack = FilledMapItem.setupNewMap(worldIn, structPos.getX(), structPos.getZ(), (byte) 0, true, true);
		}
		else {
			structName = (String) retValues.get(1);
	        stack = FilledMapItem.setupNewMap(worldIn, structPos.getX(), structPos.getZ(), (byte) 2, true, true);
	        MapData.addTargetDecoration(stack, structPos, "+", MapDecoration.Type.RED_X);
		}
        FilledMapItem.func_226642_a_((ServerWorld) worldIn, stack);
        stack.setDisplayName(new TranslationTextComponent(structName.toLowerCase(Locale.ROOT)));
        
		return stack;
	}
	
	public static ItemStack enchantItem(World worldIn, PlayerEntity player, BlockPos pos, Item enchantItem, int enchantChance) {

		Random rand = new Random();
		ItemStack stack = new ItemStack(enchantItem);
		
		if (stack != null && !stack.equals(ItemStack.EMPTY) && stack.isEnchantable() && rand.nextInt(enchantChance) == 0) {
			Enchantment ench = Util.getRandomEnchantment(stack);
			if (ench != null) {
				stack.addEnchantment(ench, rand.nextInt(ench.getMaxLevel()) + 1);
			}
		}
		return stack;
	}

	public static Entity spawnEntity(World worldIn, @Nullable PlayerEntity player, BlockPos pos, EntityType<?> spawnEntity, boolean applyPersistence, @Nullable String name) {
		
		if (spawnEntity.equals(EntityType.LIGHTNING_BOLT) && player != null) {
			pos = player.getPosition();
		}
		else if (spawnEntity.equals(EntityType.GHAST)) {
			pos = pos.up(16);
		}
		Entity spawnedEntity = spawnEntity.spawn((ServerWorld) worldIn, null, null, player, pos, SpawnReason.MOB_SUMMONED, false, false);
		if (applyPersistence) {
			Util.applyPersistence(spawnedEntity, name);
		}
		if (Refs.canBeRidden.contains(spawnEntity)) {
			Entity spawnedRider = EntityList.color_brute.spawn((ServerWorld) worldIn, null, null, player, pos, SpawnReason.MOB_SUMMONED, false, false);
			Util.applyPersistence(spawnedRider, name);
			spawnedRider.startRiding(spawnedEntity, true);
		}
		return spawnedEntity;
	}

	public static void applyPersistence(Entity entity, @Nullable String nameIn) {
		
		if (entity instanceof LivingEntity) {
			
			if (nameIn == null) {
				Random rand = new Random();
				nameIn = Refs.tagNames.get(rand.nextInt(Refs.tagNames.size()));
			}
			if (nameIn.length() != 0) {
				entity.setCustomName(new TranslationTextComponent(nameIn));
				entity.setCustomNameVisible(true);
			}
			((MobEntity) entity).enablePersistence();
			if (((LivingEntity) entity).isEntityUndead()) {
				ItemStack stack = new ItemStack(Items.DIAMOND_HELMET);
				stack.addEnchantment(Enchantments.PROTECTION, Enchantments.PROTECTION.getMaxLevel());
				stack.addEnchantment(Enchantments.UNBREAKING, Enchantments.UNBREAKING.getMaxLevel());
				entity.setItemStackToSlot(MobEntity.getSlotForItemStack(stack), stack);
			}
		}
	}
	
	public static void dupEntity(World worldIn, @Nullable PlayerEntity player, BlockPos pos, Entity attacker, LivingEntity entity, boolean applyPersistence, @Nullable String name) {
		
		Random rand = new Random();
		EntityType<?> spawnEntity = attacker.getType();
		int chance = Refs.dupEntityChance;
		if (Refs.aggressiveEntities.contains(spawnEntity)) {
			chance = Refs.dupAggressiveChance;
		}
		if (rand.nextInt(chance) == 0 || Refs.difficulty == Refs.HARDCORE) {
			Entity spawnedEntity = Util.spawnEntity(entity.world, null, entity.getPosition(), spawnEntity, true, null);
			spawnedEntity.setGlowing(true);
		}
	}
	
	public static void spawnFluid(World worldIn, PlayerEntity player, BlockPos pos, Fluid spawnBlock) {
		worldIn.setBlockState(pos.up(), spawnBlock.getDefaultState().getBlockState(), 11);
	}

	public static void spawnMetSword(World worldIn, BlockPos pos, boolean enchant) {
		ItemStack stack = new ItemStack(ItemList.meteorite_sword);
		if (enchant) {
			Enchantment ench = Enchantments.FIRE_ASPECT;
			stack.addEnchantment(ench, ench.getMaxLevel());
		}
		ItemEntity spawnItemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
		spawnItemEntity.entityDropItem(stack);
	}

	public static Potion getRandomPotion() {
		
		Random rand = new Random();
		int randNum = rand.nextInt(Util.potions.size());

		return Util.potions.get(randNum);
	}
	
	public static Enchantment getRandomEnchantment(ItemStack stack) {
		
		Random rand = new Random();
		int randNum = 0;
		Item item = stack.getItem();
		Enchantment ench = null;
		
		if (item.isEnchantable(stack)) {
			for (int i= 0; i < Util.enchantments.size(); ++i) {
				randNum = rand.nextInt(Util.enchantments.size());
				ench =  Util.enchantments.get(randNum);
				if ((item.equals(Items.BOOK) && ench.isAllowedOnBooks()) ||
						(!item.equals(Items.BOOK) && ench.canApply(stack))) {
					break;
				}
			}
		}
		return ench;
	}
	
	public static List<Object> getRandomStructure(World worldIn, BlockPos pos, int radius) {
		
		Random rand = new Random();
		Structure<?> struct;
		BlockPos structPos;
		List<Object> retValues = new ArrayList<Object>();
		retValues.add(null);
		retValues.add("");
		int randNum = 0;
		
		for (int i= 0; i < Util.structures.size(); ++i) {
			randNum = rand.nextInt(Util.structures.size());
			struct = Util.structures.get(randNum);
			structPos = ((ServerWorld) worldIn).func_241117_a_(struct, pos, radius, false);
			if (structPos != null) {
				retValues.set(0, structPos);
				retValues.set(1, struct.getStructureName());
				break;
			}
		}
		return retValues;
	}

	@SuppressWarnings("unchecked")
	public static <T> BlockState cloneState(BlockState stateToClone, BlockState targetState) {
		
		targetState = targetState.getBlock().getDefaultState();
		List<Property<?>> properties = new ArrayList<Property<?>>(stateToClone.getValues().keySet());
		
		for (Property<?> prop : properties) {
			try {
				targetState = targetState.with(Util.castObject(prop, prop.getClass()), Util.castObject(stateToClone.getValues().get(prop), stateToClone.getValues().get(prop).getClass()));
			}
			catch(Exception e) {
				LOGGER.info("Property can not be applied to target state "+prop.getName());
			}
		}
		return targetState;
	}
	
	public static <T> T castObject(Object o, Class<T> clazz) {
	    try {
	        return clazz.cast(o);
	    } 
	    catch(ClassCastException e) {
	    	return null;
	    }
	}
	
	public static ResourceLocation cloneSkin(PlayerEntity player) {
		Minecraft mc = Minecraft.getInstance();
		ResourceLocation skin1 = mc.player.getLocationSkin();
		return skin1;
	}
	
	public static boolean hasNotSolidAround(World worldIn, BlockPos blockPos) {
		BlockPos pos;
		BlockState state;
		for (Direction dir : Direction.values()) {
			pos = blockPos.offset(dir);
			state = worldIn.getBlockState(pos);
			if (!state.isSolid() || Util.allowsMovement(worldIn, state, pos) || !ItemGroup.BUILDING_BLOCKS.equals(state.getBlock().asItem().getGroup())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean allowsMovement(World worldIn, BlockState state, BlockPos pos) {
		return state.allowsMovement(worldIn, pos, PathType.LAND) || state.allowsMovement(worldIn, pos, PathType.WATER) || state.allowsMovement(worldIn, pos, PathType.AIR);
	}
	
	public static void knockback(PlayerEntity player, Entity target, double knockback) {
        Vector3d look = player.getLookVec().normalize();
        target.addVelocity(look.getX()*knockback, 0, look.getZ()*knockback);
	}
	
    public static boolean isModPresent(String modid) {
		for (ModInfo modInfo : FMLLoader.getLoadingModList().getMods()) {
			if (modInfo.getModId().toString().toLowerCase().equals(modid.toLowerCase())) {
				return true;
			}
		}
		return false;
    }
    
    public static ResourceLocation getDimension(World world) {
    	
    	RegistryKey<World> dimension = world.getDimensionKey();
    	
    	ResourceLocation overworld = DimensionType.OVERWORLD_ID;
    	ResourceLocation the_nether = DimensionType.THE_NETHER_ID;
    	ResourceLocation the_end = DimensionType.THE_END_ID;
    	
    	if (dimension == World.OVERWORLD) {
    		return overworld;
    	}
    	else if (dimension == World.THE_NETHER) {
    		return the_nether;
    	}
    	else if (dimension == World.THE_END) {
    		return the_end;
    	}
    	return overworld;
    }
}
