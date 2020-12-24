package dev.alef.coloroutofspace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableSet;

import dev.alef.coloroutofspace.bots.MetBot;
import dev.alef.coloroutofspace.lists.BlockList;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketInfected;
import dev.alef.coloroutofspace.playerdata.PlayerData;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {
	
	private BlockState cloneState;
	
	@SuppressWarnings("deprecation")
	public static void metFall(World worldIn, PlayerEntity player, PlayerData playerData) {

		BlockPos pos = playerData.getFallPos();
		
		worldIn.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), Refs.explosionRadius, true, Explosion.Mode.DESTROY);
		for (int i = 0; i < Refs.explosionRadius; ++i) {
			pos = pos.offset(Direction.DOWN, 1);
			if (!worldIn.getBlockState(pos).isAir()) {
				break;
			}
		}
		worldIn.setBlockState(pos, BlockList.color_meteorite_block.getDefaultState());
		playerData.setMetFallen(true);
		playerData.setMetActive(true);
		playerData.setMetPos(pos);
		playerData.setFallDay(-1);

		MetBot metBot = new MetBot();
		metBot.increaseInfectedArea(worldIn, player, playerData.getMetPos(), 0, Refs.radiusIncrease);
		playerData.setPrevRadius(Refs.radiusIncrease);
	}
	
	public static void infect(World worldIn, BlockPos pos, Entity entityIn) {
		
    	if (entityIn instanceof PlayerEntity) {
    		
    		PlayerEntity player = (PlayerEntity) entityIn;
    		PlayerData playerData = ColorOutOfSpace.playerDataList.get(worldIn, player);
    		playerData.setPlayerInfected(true);
    		Utils.applyInfectedEffects(player, true);
    	}
    	else if (entityIn instanceof LivingEntity) {
    		
    		int i = Refs.entitiesToInfect.indexOf(entityIn.getType());

			if (i >= 0 && ((LivingEntity) entityIn).getActivePotionEffects().size() == 0) {
				
				((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.GLOWING, Refs.timeIncrease));
				((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.INSTANT_DAMAGE, Refs.timeIncrease));
				EntityType<?> spawnEntity = Refs.infectedEntities.get(i);
				Entity spawnedEntity = spawnEntity.spawn((ServerWorld) worldIn, new CompoundNBT(), null, null, new BlockPos(entityIn.getPositionVec()), SpawnReason.CONVERSION, false, false);
				Utils.applyPersistence(spawnedEntity);
			}
    	}
	}
	
	public static void applyInfectedEffects(PlayerEntity player, boolean firstTime) {
		
		Networking.sendToClient(new PacketInfected(player.getUniqueID()), (ServerPlayerEntity) player);
		if (firstTime) {
			player.addPotionEffect(new EffectInstance(Effects.POISON, 300));
		}
		player.addPotionEffect(new EffectInstance(Effects.BAD_OMEN, Refs.timeIncrease));
		player.addPotionEffect(new EffectInstance(Effects.GLOWING, Refs.timeIncrease));
	}

	public static void spawnPotion(World worldIn, PlayerEntity player, BlockPos pos, Item potionItem) {
        Potion potion = Utils.getRandomPotion();
        PotionEntity potionentity = new PotionEntity(worldIn, pos.getX(), pos.getY(), pos.getZ());
        potionentity.setItem(PotionUtils.addPotionToItemStack(new ItemStack(potionItem), potion));
        ItemStack stack = potionentity.getItem();
        ItemEntity spawnItemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
        spawnItemEntity.entityDropItem(stack);
	}
	
	public static void spawnMap(World worldIn, PlayerEntity player, BlockPos pos, Item mapItem) {

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
		ItemEntity spawnItemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
		spawnItemEntity.entityDropItem(stack);
	}
	
	public static void spawnEntity(World worldIn, PlayerEntity player, BlockPos pos, EntityType<?> spawnEntity, boolean applyPersistence) {
		
		if (spawnEntity.equals(EntityType.LIGHTNING_BOLT)) {
			pos = new BlockPos(player.getPositionVec());
		}
		Entity spawnedEntity = spawnEntity.spawn((ServerWorld) worldIn, new CompoundNBT(), null, player, pos, SpawnReason.MOB_SUMMONED, false, false);
		if (applyPersistence) {
			Utils.applyPersistence(spawnedEntity);
		}
	}

	public static void applyPersistence(Entity entity) {
		
		if (entity instanceof LivingEntity) {
			
			Random rand = new Random();
			TextComponent name = new TranslationTextComponent(Refs.tagNames.get(rand.nextInt(Refs.tagNames.size())));
			
			entity.setCustomName(name);
			entity.setCustomNameVisible(true);
			((MobEntity) entity).enablePersistence();
		}
	}
	
	public static void spawnFluid(World worldIn, PlayerEntity player, BlockPos pos, Fluid spawnBlock) {
		worldIn.setBlockState(pos.up(), spawnBlock.getDefaultState().getBlockState(), 11);
	}

		
	public static void spawnItem(World worldIn, PlayerEntity player, BlockPos pos, Item spawnItem, boolean enchantAll, boolean enchantBooks) {
		
		Random rand1 = new Random();
		Random rand2 = new Random();
		ItemStack stack = new ItemStack(spawnItem);

		if ((spawnItem.equals(Items.ENCHANTED_BOOK) && enchantBooks && enchantAll) || 
				(enchantAll && stack != null && !stack.equals(ItemStack.EMPTY) && stack.isEnchantable() && rand1.nextInt(Refs.enchantabilityChance) == 0)) {
			Enchantment ench = Utils.getRandomEnchantment();
			stack.addEnchantment(ench, rand2.nextInt(ench.getMaxLevel()));
		}
		
		ItemEntity spawnItemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
		spawnItemEntity.entityDropItem(stack);
	}

	public static Potion getRandomPotion() {
		
		Random rand = new Random();
		int randNum = rand.nextInt(ForgeRegistries.POTION_TYPES.getValues().size());
		int i = 0;
		
		for (Potion potion : ForgeRegistries.POTION_TYPES.getValues()) {
			if (i == randNum) {
				return potion;
			}
			++i;
		}
		return Potions.HARMING;
	}
	
	public static Enchantment getRandomEnchantment() {
		
		Random rand = new Random();
		int randNum = rand.nextInt(ForgeRegistries.ENCHANTMENTS.getValues().size());
		int i = 0;
		
		for (Enchantment ench : ForgeRegistries.ENCHANTMENTS.getValues()) {
			if (i == randNum) {
				return ench;
			}
			++i;
		}
		return null;
	}
	
	public static List<Object> getRandomStructure(World worldIn, BlockPos pos, int radius) {
		
		Random rand = new Random();
		int randNum = rand.nextInt(ForgeRegistries.STRUCTURE_FEATURES.getValues().size());
		int i = 0;
		List<Object> retValues = new ArrayList<Object>();
		BlockPos retPos = null;
		String retName = "";
		
		for (Structure<?> struct : ForgeRegistries.STRUCTURE_FEATURES.getValues()) {
			if (i == randNum) {
				retPos = ((ServerWorld) worldIn).func_241117_a_(struct, pos, radius, false);
				retValues.add(retPos);
				retName = struct.getStructureName();
				retValues.add(retName);
				return retValues;
			}
			++i;
		}
		return null;
	}

	public BlockState cloneState(BlockState stateToClone, BlockState targetState) {
		
		this.cloneState = targetState;

		if (stateToClone.getBlock().equals(targetState.getBlock())) {
		
			ImmutableSet<Property<?>> keys = stateToClone.getValues().keySet();
			keys.forEach(prop -> {
				if (prop instanceof BooleanProperty) {
					this.cloneState = this.cloneState.with((BooleanProperty) prop, (Boolean) stateToClone.getValues().get(prop));
				}
				else if (prop instanceof DirectionProperty) {
					this.cloneState  = this.cloneState.with((DirectionProperty) prop, (Direction) stateToClone.getValues().get(prop));
				}
				else if (prop instanceof IntegerProperty) {
					this.cloneState = this.cloneState.with((IntegerProperty) prop, (Integer) stateToClone.getValues().get(prop));
				}
			});
		}
		return this.cloneState;
	}
}
