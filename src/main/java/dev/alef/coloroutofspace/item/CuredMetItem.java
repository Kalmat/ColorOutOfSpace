package dev.alef.coloroutofspace.item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Util;
import dev.alef.coloroutofspace.bot.MetBot;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketInfected;
import dev.alef.coloroutofspace.playerdata.IPlayerData;
import dev.alef.coloroutofspace.playerdata.PlayerData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CuredMetItem extends Item {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
   public CuredMetItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
    * the Item before the action is complete.
    */
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {

	   if (entityLiving instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityLiving;
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
         serverplayerentity.addStat(Stats.ITEM_USED.get(this));
      }

      if (entityLiving instanceof PlayerEntity) {
    	  
    	  if (!((PlayerEntity)entityLiving).abilities.isCreativeMode) {
    		  stack.shrink(1);
    	  }
    	  
          entityLiving.clearActivePotions();
          entityLiving.setGlowing(false);

	      if (!worldIn.isRemote) {
	    	  IPlayerData playerData = PlayerData.getFromPlayer((PlayerEntity)entityLiving);
		      playerData.setPlayerCured(true);
		      playerData.setPlayerInfected(false);
		      playerData.setMetActive(false);
		      Networking.sendToClient(new PacketInfected(false, 0), (ServerPlayerEntity) entityLiving);
		      
		      Util.spawnMetSword(worldIn, (PlayerEntity)entityLiving, true);
		      
		      if (playerData.getMetPos() != null) {
			      MetBot metBot = new MetBot();
		    	  metBot.uninfectArea(worldIn, (PlayerEntity)entityLiving, playerData.getMetPos(), playerData.getPrevRadius(), true);
		      }
	      }
      }
      return stack.isEmpty() ? ItemStack.EMPTY : stack;
   }

   /**
    * How long it takes to use or consume an item
    */
   public int getUseDuration(ItemStack stack) {
      return 32;
   }

   /**
    * returns the action that specifies what animation to play when the items is being used
    */
   public UseAction getUseAction(ItemStack stack) {
      return UseAction.DRINK;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      return DrinkHelper.startDrinking(worldIn, playerIn, handIn);
   }
}