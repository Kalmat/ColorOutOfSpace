package dev.alef.coloroutofspace.entity;

import java.util.Random;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.lists.EntityList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class AntiPlayerEntity extends ZombieEntity {
	
   public AntiPlayerEntity(EntityType<? extends AntiPlayerEntity> type, World worldIn) {
	   super(type, worldIn);
	}
   
	public AntiPlayerEntity(FMLPlayMessages.SpawnEntity packet, World worldIn) {
		this(EntityList.color_anti_player, worldIn);
	}

   public static AttributeModifierMap.MutableAttribute func_234342_eQ_() {
	   return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 120.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 120.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.3F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 12.0D).createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.5D).createMutableAttribute(Attributes.ARMOR, 8.0D).createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS);
   }

   public static boolean func_223334_b(EntityType<AntiPlayerEntity> p_223334_0_, IServerWorld p_223334_1_, SpawnReason reason, BlockPos p_223334_3_, Random p_223334_4_) {
	   return true;
   }
   
   public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
	   
	   if (Refs.difficulty == Refs.HARDCORE) {
		   return MonsterEntity
				   .func_234295_eP_()
				   .createMutableAttribute(Attributes.MAX_HEALTH, 120.0D)
				   .createMutableAttribute(Attributes.FOLLOW_RANGE, 120.0D)
				   .createMutableAttribute(Attributes.ATTACK_SPEED, 4.0D)
				   .createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.35F)
				   .createMutableAttribute(Attributes.ATTACK_DAMAGE, 20.0D)
				   .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 2.0D)
				   .createMutableAttribute(Attributes.ARMOR, 16.0D)
				   .createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS);
	   }
	   else {
		   return MonsterEntity
				   .func_234295_eP_()
				   .createMutableAttribute(Attributes.MAX_HEALTH, 80.0D)
				   .createMutableAttribute(Attributes.FOLLOW_RANGE, 120.0D)
				   .createMutableAttribute(Attributes.ATTACK_SPEED, 2.0D)
				   .createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.3F)
				   .createMutableAttribute(Attributes.ATTACK_DAMAGE, 12.0D)
				   .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.5D)
				   .createMutableAttribute(Attributes.ARMOR, 8.0D)
				   .createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS);
	   }
   }

   protected boolean shouldBurnInDay() {
      return false;
   }
   
   protected boolean shouldDrown() {
	   return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_GHAST_SCREAM;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_HUSK_STEP;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && Refs.difficulty == Refs.HARDCORE && entityIn instanceof PlayerEntity) {
         ((PlayerEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, 100));
      }
      return flag;
   }

   protected ItemStack getSkullDrop() {
      return new ItemStack(Items.PLAYER_HEAD);
   }
   
   @Override
   public IPacket<?> createSpawnPacket() {
	   return NetworkHooks.getEntitySpawningPacket(this);
   }
}