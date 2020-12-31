package dev.alef.coloroutofspace.entities;

import java.util.Random;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.lists.EntityList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
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
   
	@SuppressWarnings("unchecked")
	public AntiPlayerEntity(FMLPlayMessages.SpawnEntity packet, World worldIn) {
		this((EntityType<? extends AntiPlayerEntity>) EntityList.color_anti_player, worldIn);
	}

   public static AttributeModifierMap.MutableAttribute func_234342_eQ_() {
	   return MonsterEntity.func_234295_eP_().func_233815_a_(Attributes.field_233819_b_, 35.0D).func_233815_a_(Attributes.field_233821_d_, (double)0.23F).func_233815_a_(Attributes.field_233823_f_, 3.0D).func_233815_a_(Attributes.field_233826_i_, 2.0D).func_233814_a_(Attributes.field_233829_l_);
   }

   public static boolean func_223334_b(EntityType<AntiPlayerEntity> p_223334_0_, IServerWorld p_223334_1_, SpawnReason reason, BlockPos p_223334_3_, Random p_223334_4_) {
	   return true;
   }
   
   public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
	   
	   if (Refs.hardcoreMode) {
		   return MonsterEntity
				   .func_234295_eP_()
				   .func_233815_a_(Attributes.field_233818_a_, 240.0D)  			// Max Health
				   .func_233815_a_(Attributes.field_233819_b_, 120.0D)  			// Follow Range
				   .func_233815_a_(Attributes.field_233821_d_, (double)0.35F)		// Movement Speed
				   .func_233815_a_(Attributes.field_233823_f_, 20.0D)				// Attack Damage
				   .func_233815_a_(Attributes.field_233824_g_, 2.0D)				// Attack KnockBack
				   .func_233815_a_(Attributes.field_233826_i_, 16.0D)				// Armor
		   		   .func_233814_a_(Attributes.field_233829_l_);						// Zombie: Spawn Reinforcements

	   }
	   else {
		   return MonsterEntity
				   .func_234295_eP_()
				   .func_233815_a_(Attributes.field_233818_a_, 120.0D)  			// Max Health
				   .func_233815_a_(Attributes.field_233819_b_, 120.0D)  			// Follow Range
				   .func_233815_a_(Attributes.field_233821_d_, (double)0.3F)		// Movement Speed
				   .func_233815_a_(Attributes.field_233823_f_, 12.0D)				// Attack Damage
				   .func_233815_a_(Attributes.field_233824_g_, 1.5D)				// Attack KnockBack
				   .func_233815_a_(Attributes.field_233826_i_, 8.0D)				// Armor
				   .func_233814_a_(Attributes.field_233829_l_);						// Zombie: Spawn Reinforcements

	   }
   }

   protected boolean shouldBurnInDay() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_HUSK_STEP;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && this.getHeldItemMainhand().isEmpty() && entityIn instanceof LivingEntity) {
         float f = this.world.getDifficultyForLocation(this.func_233580_cy_()).getAdditionalDifficulty();
         ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.HUNGER, 140 * (int)f));
      }

      return flag;
   }

   protected boolean shouldDrown() {
      return false;
   }

   protected ItemStack getSkullDrop() {
      return new ItemStack(Items.PLAYER_HEAD);
   }
   
   @Override
   public IPacket<?> createSpawnPacket() {
	   return NetworkHooks.getEntitySpawningPacket(this);
   }
}