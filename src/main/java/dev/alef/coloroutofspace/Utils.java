package dev.alef.coloroutofspace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.lists.EntityList;
import dev.alef.coloroutofspace.lists.ItemList;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketInfected;
import dev.alef.coloroutofspace.playerdata.PlayerData;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final List<Potion> potions = new ArrayList<Potion>(ForgeRegistries.POTION_TYPES.getValues());
	public static final List<Enchantment> enchantments = new ArrayList<Enchantment>(ForgeRegistries.ENCHANTMENTS.getValues());
	public static final List<Structure<?>> structures = new ArrayList<Structure<?>>(ForgeRegistries.STRUCTURE_FEATURES.getValues());
	
	public static void infect(World worldIn, BlockPos pos, Entity entityIn) {
		
    	if (entityIn instanceof PlayerEntity) {
    		
    		PlayerEntity player = (PlayerEntity) entityIn;
    		PlayerData playerData = ColorOutOfSpace.playerDataList.get(worldIn, player);
			Utils.applyInfectedEffects(player, true);
			if (!playerData.isPlayerInfected()) {
				playerData.setPlayerInfected(true);
				Utils.spawnAntiPlayer((ServerWorld)worldIn, player, playerData.getMetPos().up());
			}
    	}
    	else if (entityIn instanceof LivingEntity) {
    		
    		int i = Refs.entitiesToInfect.indexOf(entityIn.getType());

			if (i >= 0 && ((LivingEntity) entityIn).getActivePotionEffects().size() == 0) {
				
				((LivingEntity) entityIn).setGlowing(true);
				((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.INSTANT_DAMAGE, Refs.timeIncrease));
				Entity spawnedEntity = Utils.spawnEntity(worldIn, null, new BlockPos(entityIn.getPositionVec()).up(), Refs.infectedEntities.get(i), true, null);
				spawnedEntity.setGlowing(true);
			}
			else if (Refs.difficulty == Refs.HARDCORE && entityIn instanceof MonsterEntity && !entityIn.hasCustomName()) {
				entityIn.setGlowing(true);
				Utils.applyPersistence(entityIn, null);
				// Even more hardcore: turn them all into ZOGLINGS!!!! (but think what to do with the AntiPlayer... spawn jailed right on top of the meteorite?)
			}
    	}
	}
	
	public static LivingEntity spawnAntiPlayer(ServerWorld worldIn, PlayerEntity player, BlockPos pos) {
		Entity spawnedEntity = Utils.spawnEntity(worldIn, null, pos, EntityList.color_anti_player, true, "Evil "+player.getScoreboardName());
		for (ItemStack armor : player.getEquipmentAndArmor()) {
			if (armor != null && !armor.equals(ItemStack.EMPTY)) {
				spawnedEntity.setItemStackToSlot(MobEntity.getSlotForItemStack(armor), armor);
			}
		}
		spawnedEntity.setGlowing(true);
		if (Refs.difficulty == Refs.HARDCORE) {
			((LivingEntity)spawnedEntity).addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE));
		}
		return (LivingEntity) spawnedEntity;
	}
	
	public static void applyInfectedEffects(PlayerEntity player, boolean firstTime) {
		
		Networking.sendToClient(new PacketInfected(player.getUniqueID()), (ServerPlayerEntity) player);
		if (firstTime) {
			player.addPotionEffect(new EffectInstance(Effects.POISON, 300));
			player.setGlowing(true);
		}
		player.addPotionEffect(new EffectInstance(Effects.BAD_OMEN, Refs.timeIncrease));
	}

	public static ItemStack getPotion(World worldIn, PlayerEntity player, BlockPos pos, Item potionItem) {
        Potion potion = Utils.getRandomPotion();
        PotionEntity potionentity = new PotionEntity(worldIn, pos.getX(), pos.getY(), pos.getZ());
        potionentity.setItem(PotionUtils.addPotionToItemStack(new ItemStack(potionItem), potion));
        ItemStack stack = potionentity.getItem();
        return stack;
	}
	
	public static ItemStack getMap(World worldIn, PlayerEntity player, BlockPos pos, Item mapItem) {

		int x = MathHelper.floor(player.getPosX());
		int z = MathHelper.floor(player.getPosZ());
		BlockPos structPos = null;
		String structName = "Surroundings Map";
		List<Object> retValues = new ArrayList<Object>();
		retValues = Utils.getRandomStructure(worldIn, pos, 96);
		structPos = (BlockPos) retValues.get(0);

		if (structPos != null) {
			x = structPos.getX();
			z = structPos.getZ();
			structName = (String) retValues.get(1);
		}
		ItemStack stack = FilledMapItem.setupNewMap(worldIn, x, z, (byte) 0, true, true).setDisplayName(new TranslationTextComponent(structName));
		return stack;
	}
	
	public static Entity spawnEntity(World worldIn, @Nullable PlayerEntity player, BlockPos pos, EntityType<?> spawnEntity, boolean applyPersistence, @Nullable String name) {
		
		if (spawnEntity.equals(EntityType.LIGHTNING_BOLT) && player != null) {
			pos = new BlockPos(player.getPositionVec());
		}
		Entity spawnedEntity = spawnEntity.spawn((ServerWorld) worldIn, null, null, player, pos, SpawnReason.MOB_SUMMONED, false, false);
		if (applyPersistence) {
			Utils.applyPersistence(spawnedEntity, name);
		}
		return spawnedEntity;
	}

	public static void applyPersistence(Entity entity, String nameIn) {
		
		if (entity instanceof LivingEntity) {
			
			TextComponent name;
			
			if (nameIn == null || "".equals(nameIn)) {
				Random rand = new Random();
				name = new TranslationTextComponent(Refs.tagNames.get(rand.nextInt(Refs.tagNames.size())));
			}
			else {
				name = new TranslationTextComponent(nameIn);
			}
			
			entity.setCustomName(name);
			entity.setCustomNameVisible(true);
			((MobEntity) entity).enablePersistence();
		}
	}
	
	public static void spawnFluid(World worldIn, PlayerEntity player, BlockPos pos, Fluid spawnBlock) {
		worldIn.setBlockState(pos.up(), spawnBlock.getDefaultState().getBlockState(), 11);
	}

	public static void spawnItem(World worldIn, PlayerEntity player, BlockPos pos, Item spawnItem, boolean enchantAll, boolean enchantBooks, int enchantChance) {
		
		ItemStack stack = new ItemStack(spawnItem);

		if (spawnItem.equals(Items.POTION) || spawnItem.equals(Items.SPLASH_POTION)) {
			stack  = Utils.getPotion(worldIn, player, pos, spawnItem);
		}
		else if (spawnItem.equals(Items.MAP)) {
			stack = Utils.getMap(worldIn, player, pos, spawnItem);
		}
		else if (spawnItem.equals(Items.BOOK) && (enchantBooks || enchantAll)) {
			stack = Utils.enchantItem(worldIn, player, pos, spawnItem, 1);
		}
		else if (stack.isEnchantable() && enchantAll) {
			stack = Utils.enchantItem(worldIn, player, pos, spawnItem, enchantChance);
		}
		ItemEntity spawnItemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
		spawnItemEntity.entityDropItem(stack);
	}
	
	public static ItemStack enchantItem(World worldIn, PlayerEntity player, BlockPos pos, Item enchantItem, int enchantChance) {

		Random rand = new Random();
		ItemStack stack = new ItemStack(enchantItem);
		
		if (stack != null && !stack.equals(ItemStack.EMPTY) && stack.isEnchantable() && rand.nextInt(enchantChance) == 0) {
			Enchantment ench = Utils.getRandomEnchantment(stack);
			if (ench != null) {
				stack.addEnchantment(ench, rand.nextInt(ench.getMaxLevel()) + 1);
			}
		}
		return stack;
	}

	public static void spawnMetSword(World worldIn, PlayerEntity player, boolean enchant) {
		ItemStack stack = new ItemStack(ItemList.meteorite_sword);
		if (enchant) {
			Enchantment ench = Enchantments.FIRE_ASPECT;
			stack.addEnchantment(ench, ench.getMaxLevel());
		}
		ItemEntity spawnItemEntity = new ItemEntity(worldIn, player.getPositionVec().x, player.getPositionVec().y, player.getPositionVec().z, stack);
		spawnItemEntity.entityDropItem(stack);
	}

	public static Potion getRandomPotion() {
		
		Random rand = new Random();
		int randNum = rand.nextInt(Utils.potions.size());

		return Utils.potions.get(randNum);
	}
	
	public static Enchantment getRandomEnchantment(ItemStack stack) {
		
		Random rand = new Random();
		int randNum = 0;
		Item item = stack.getItem();
		Enchantment ench = null;
		Enchantment ench1 = null;
		
		if (item.isEnchantable(stack)) {
			for (int i= 0; i < Utils.enchantments.size(); ++i) {
				randNum = rand.nextInt(Utils.enchantments.size());
				ench1 =  Utils.enchantments.get(randNum);
				if ((item.equals(Items.BOOK) && ench1.isAllowedOnBooks()) ||
						(!item.equals(Items.BOOK) && ench1.canApply(stack))) {
					ench = ench1;
					break;
				}
			}
		}
		return ench;
	}
	
	public static List<Object> getRandomStructure(World worldIn, BlockPos pos, int radius) {
		
		Random rand = new Random();
		Structure<?> struct;
		List<Object> retValues = new ArrayList<Object>();
		retValues.add(null);
		retValues.add("");
		int randNum = 0;
		
		for (int i= 0; i < Utils.structures.size(); ++i) {
			randNum = rand.nextInt(Utils.structures.size());
			struct = Utils.structures.get(randNum);
			if (struct != null) {
				retValues.set(0, ((ServerWorld) worldIn).func_241117_a_(struct, pos, radius, false));
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
				targetState = targetState.with(Utils.convertInstanceOfObject(prop, prop.getClass()), Utils.convertInstanceOfObject(stateToClone.getValues().get(prop), stateToClone.getValues().get(prop).getClass()));
			}
			catch(Exception e) {
				LOGGER.info("Property can not be applied to target state "+prop.getName());
			}
		}
		return targetState;
	}
	
	public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
	    try {
	        return clazz.cast(o);
	    } 
	    catch(ClassCastException e) {
	    	return null;
	    }
	}
	
	public static boolean hasNotSolidAround(World worldIn, BlockPos blockPos) {
		BlockPos pos;
		BlockState state;
		for (Direction dir : Direction.values()) {
			pos = blockPos.offset(dir);
			state = worldIn.getBlockState(pos);
			if (!state.isSolid() || Utils.allowsMovement(worldIn, state, pos) || !ItemGroup.BUILDING_BLOCKS.equals(state.getBlock().asItem().getGroup())) {
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
}
