package dev.alef.coloroutofspace.entity;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.Util;
import dev.alef.coloroutofspace.bot.MetBot;
import dev.alef.coloroutofspace.lists.EntityList;
import dev.alef.coloroutofspace.lists.ItemList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.piglin.PiglinAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColorEntity extends AbstractColorEntity {
   protected static final ImmutableList<SensorType<? extends Sensor<? super ColorEntity>>> field_242343_d = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);
   protected static final ImmutableList<MemoryModuleType<?>> field_242342_bo = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.OPENED_DOORS, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEAREST_ADULT_PIGLINS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.HOME);

   public ColorEntity(EntityType<? extends ColorEntity> p_i241917_1_, World p_i241917_2_) {
      super(p_i241917_1_, p_i241917_2_);
      this.experienceValue = 20;
   }

   public static AttributeModifierMap.MutableAttribute bruteAttr() {
      return MonsterEntity.func_234295_eP_()
    		  .createMutableAttribute(Attributes.MAX_HEALTH, 50.0D)
    		  .createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.35F)
    		  .createMutableAttribute(Attributes.ATTACK_DAMAGE, 7.0D);
   }

   public static AttributeModifierMap.MutableAttribute antiplayerAttr() {
	   
	   if (Refs.difficulty == Refs.HARDCORE) {
		   return MonsterEntity
				   .func_234295_eP_()
				   .createMutableAttribute(Attributes.MAX_HEALTH, 120.0D)
				   .createMutableAttribute(Attributes.FOLLOW_RANGE, 120.0D)
				   .createMutableAttribute(Attributes.ATTACK_SPEED, 4.0D)
				   .createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.4F)
				   .createMutableAttribute(Attributes.ATTACK_DAMAGE, 20.0D)
				   .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 2.0D)
				   .createMutableAttribute(Attributes.ARMOR, 16.0D);
	   }
	   else {
		   return MonsterEntity
				   .func_234295_eP_()
				   .createMutableAttribute(Attributes.MAX_HEALTH, 80.0D)
				   .createMutableAttribute(Attributes.FOLLOW_RANGE, 120.0D)
				   .createMutableAttribute(Attributes.ATTACK_SPEED, 2.0D)
				   .createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.35F)
				   .createMutableAttribute(Attributes.ATTACK_DAMAGE, 10.0D)
				   .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.5D)
				   .createMutableAttribute(Attributes.ARMOR, 8.0D);
	   }
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      ColorEntityBrain.func_242352_a(this);
      this.setEquipmentBasedOnDifficulty(difficultyIn);
      return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ItemList.meteorite_sword));
   }

   protected Brain.BrainCodec<ColorEntity> getBrainCodec() {
      return (Brain.BrainCodec<ColorEntity>) Brain.createCodec(field_242342_bo, field_242343_d);
   }

   protected Brain<?> createBrain(Dynamic<?> dynamicIn) {
      return ColorEntityBrain.func_242354_a(this, this.getBrainCodec().deserialize(dynamicIn));
   }

   @SuppressWarnings("unchecked")
   public Brain<ColorEntity> getBrain() {
      return (Brain<ColorEntity>)super.getBrain();
   }

   public boolean func_234422_eK_() {
      return false;
   }

   public boolean func_230293_i_(ItemStack p_230293_1_) {
	  Item itemInHand = p_230293_1_.getItem();
      return itemInHand.equals(ItemList.meteorite_sword) || itemInHand.equals(Items.GOLDEN_AXE) ? super.func_230293_i_(p_230293_1_) : false;
   }

   protected void updateAITasks() {
      this.world.getProfiler().startSection("piglinBruteBrain");
      this.getBrain().tick((ServerWorld)this.world, this);
      this.world.getProfiler().endSection();
      ColorEntityBrain.func_242358_b(this);
      ColorEntityBrain.func_242360_c(this);
      super.updateAITasks();
   }

   @OnlyIn(Dist.CLIENT)
   public PiglinAction func_234424_eM_() {
      return this.isAggressive() && this.func_242338_eO() ? PiglinAction.ATTACKING_WITH_MELEE_WEAPON : PiglinAction.DEFAULT;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (this.world.isRemote) {
         return false;
      } else {
         if (flag && source.getTrueSource() instanceof LivingEntity) {
            try {
				ColorEntityBrain.func_242353_a(this, (LivingEntity)source.getTrueSource());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
         }

         return flag;
      }
   }
   
   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_RAVAGER_ROAR;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITCH_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_RAVAGER_STEP;
   }
   
   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   protected void func_242345_eT() {
      this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_ANGRY, 1.0F, this.getSoundPitch());
   }

   protected void func_241848_eP() {
      this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_CONVRTED_TO_ZOMBIFIED, 1.0F, this.getSoundPitch());
   }
   
   public static ColorEntity spawnAntiPlayer(ServerWorld worldIn, PlayerEntity player, BlockPos pos, boolean jailed) {

		if (jailed) {
			MetBot metBot = new MetBot();
			metBot.infectArea(worldIn, pos, Refs.explosionRadius - 2, Refs.explosionRadius - 1, true);
		}

		Entity spawnedEntity = Util.spawnEntity(worldIn, null, pos, EntityList.color_anti_player, true, "Evil "+player.getScoreboardName());

		for (ItemStack armor : player.getEquipmentAndArmor()) {
			if (armor != null && !armor.equals(ItemStack.EMPTY)) {
				spawnedEntity.setItemStackToSlot(MobEntity.getSlotForItemStack(armor), armor);
			}
		}
		spawnedEntity.setGlowing(true);
		return (ColorEntity) spawnedEntity;
	}
}
