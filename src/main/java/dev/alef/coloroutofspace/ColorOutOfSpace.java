package dev.alef.coloroutofspace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.lists.BlockList;
import dev.alef.coloroutofspace.lists.EntityList;
import dev.alef.coloroutofspace.lists.ItemList;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketInfected;
import dev.alef.coloroutofspace.playerdata.IPlayerData;
import dev.alef.coloroutofspace.playerdata.PlayerData;
import dev.alef.coloroutofspace.playerdata.PlayerDataProvider;
import dev.alef.coloroutofspace.config.ConfigFile;
import dev.alef.coloroutofspace.entity.ColorEntity;
import dev.alef.coloroutofspace.item.MeteoriteSwordTier;
import dev.alef.coloroutofspace.lists.BlockItemList;
import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

//The value here should match an entry in the META-INF/mods.toml file
@Mod(Refs.MODID)
public class ColorOutOfSpace {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static long serverPrevTime = 0;
    
	public ColorOutOfSpace() {

		// Register modloading events
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::parallelDispatch);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);
		
		// Register other events we use
		MinecraftForge.EVENT_BUS.register(new PlayerCapabilityEventListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerLoggedInListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerCloneListener());
        MinecraftForge.EVENT_BUS.register(new onWorldTickListener());
        MinecraftForge.EVENT_BUS.register(new onBlockBreakListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerWakeUpListener());
        MinecraftForge.EVENT_BUS.register(new onAttackEntityListener());
        MinecraftForge.EVENT_BUS.register(new onLivingDeathListener());
        MinecraftForge.EVENT_BUS.register(new onRenderGameOverlayListener());
        
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
		
		// Register our custom blocks and items
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		BlockList.BLOCK_LIST.register(modEventBus);
		BlockItemList.BLOCKITEM_LIST.register(modEventBus);
		ItemList.ITEM_LIST.register(modEventBus);
		EntityList.ENTITY_LIST.register(modEventBus);
		
		// Register custom server --> client messages
		Networking.registerMessages();
		
        // Load config file
		ConfigFile.registerConfig();
	}
	
	private void setup(final FMLCommonSetupEvent event) {
	    // some preinit code
        PlayerData.registerPlayerCapability();
	}
 	
	private void parallelDispatch(final ParallelDispatchEvent event) {
		event.enqueueWork(() -> EntityList.putAttributes());
	}
	 
	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client
		BlockList.registerBlockRenderers();
		EntityList.registerEntityRenderers();
	}
	
	private void modConfig(final ModConfigEvent event) {
    	Refs.difficulty = ConfigFile.GENERAL.Difficulty.get() == Refs.HARDCORE ? Refs.HARDCORE : Refs.NORMAL;
	}
	
	public static class PlayerCapabilityEventListener {

	    public final static ResourceLocation PlayerDataCapability = new ResourceLocation(Refs.MODID, "player_data");
	    
	    @SubscribeEvent
	    public void attachCapability(final AttachCapabilitiesEvent<Entity> event) {
	        if (event.getObject() instanceof PlayerEntity) {
	        	event.addCapability(PlayerDataCapability, new PlayerDataProvider());
	        }
	    }
	}
	
	// SERVER
	public class onPlayerLoggedInListener {
		
		@SubscribeEvent
		public void PlayerLoggedIn(final PlayerLoggedInEvent event) {
			
    		PlayerEntity player = event.getPlayer();
    		World world = player.world;
    		
			IPlayerData playerData = PlayerData.getFromPlayer(player);

			if (playerData.getFirstJoin() == 0L) {
				playerData.setFirstJoin(world.getGameTime() + 1L);
				playerData.setFallDay(Refs.daysToFall, true);
			}
			else if (playerData.isPlayerInfected()) {
				ColorOutOfSpace.Infection.applyInfectedEffects(player, playerData.getMetDisableLevel(), false);
			}
		}
	}
	
    // SERVER
    public class onPlayerCloneListener {
        
		@SubscribeEvent
        public void PlayerClone(final Clone event) {
			
			PlayerEntity origPlayer = event.getOriginal();
			PlayerEntity player = event.getPlayer();
			World world = player.world;
			
			if (!world.isRemote) {

				IPlayerData origPlayerData = PlayerData.getFromPlayer(origPlayer);
				IPlayerData playerData = PlayerData.getFromPlayer(player);
				if (event.isWasDeath()) {
			        playerData.copyForRespawn(origPlayerData);
				} 
				if (playerData.isPlayerInfected()) {
					ColorOutOfSpace.Infection.applyInfectedEffects(player, playerData.getMetDisableLevel(), false);
				}
			}
		}
	}
    
    // SERVER
    public class onWorldTickListener {
        
		@SubscribeEvent
        public void WorldTick(final WorldTickEvent event) {
			
			World world = event.world;
			long time = event.world.getDayTime();
			
			if (time % Refs.timeIncrease == 0 && time != ColorOutOfSpace.serverPrevTime) {
				
				ColorOutOfSpace.serverPrevTime = time;

				for (PlayerEntity player : world.getPlayers()) {
					
					IPlayerData playerData = PlayerData.getFromPlayer(player);
					
					if (playerData.isPlayerInfected()) {
						ColorOutOfSpace.Infection.applyInfectedEffects(player, playerData.getMetDisableLevel(), false);
					}
					
					int daysJoined = ((int) (world.getGameTime() - playerData.getFirstJoin())) / 24000;

					if (playerData.isMetFallen() && playerData.isMetActive()) {

						if (playerData.getPrevRadius() < Refs.infectRadiusLimit) {
							playerData.increaseMetRadius(world);
						}  
						else if (!playerData.isPlayerCured()) {
							if (Refs.difficulty == Refs.HARDCORE) {
								ColorOutOfSpace.Infection.curePlayer(world, player, playerData, false);
								playerData.resetMet(world, true, false);
							}
							playerData.replanMetFall(daysJoined);
						}
					}
					else if (playerData.canMetFall(world, daysJoined)) {
						playerData.metFall(world, player);
					}
				}
			}
		}
    }
    
 	// CLIENT & SERVER
    public class onPlayerWakeUpListener {
        
		@SubscribeEvent
        public void PlayerWakeUp(final PlayerWakeUpEvent event) {
			
			PlayerEntity player = event.getPlayer();
			World world = player.world;
			
			if (!world.isRemote) {
				
				IPlayerData playerData = PlayerData.getFromPlayer(player);

				BlockPos bedPos = player.getPosition();
				playerData.setBedPos(bedPos);
				playerData.setFallPos(bedPos, true);

				int daysJoined = ((int) (world.getGameTime() - playerData.getFirstJoin())) / 24000;
				
				if (playerData.canMetFall(world, daysJoined)) {
					playerData.metFall(world, player);
				}
			}
		}
    }
    
    // CLIENT & SERVER
	public class onAttackEntityListener {
		
	    @SubscribeEvent
        public void AttackEntity(AttackEntityEvent event) {
        	
	    	Entity target = event.getTarget();
			PlayerEntity playerAttacking = event.getPlayer();
			
        	Item itemInMainHand = playerAttacking.getItemStackFromSlot(EquipmentSlotType.MAINHAND).getStack().getItem();
        	
    		if (itemInMainHand.equals(ItemList.meteorite_sword) && Refs.difficulty != Refs.HARDCORE) {
    			Util.knockback(playerAttacking, target, MeteoriteSwordTier.getKnockback());
        	}
    		
    		if (target instanceof PlayerEntity) {
    			
    			PlayerEntity playerAttacked = (PlayerEntity) target;
				IPlayerData playerData = PlayerData.getFromPlayer(playerAttacked);

				if (playerData.isPlayerInfected()) {
					playerAttacking.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100));
				}

				playerData = PlayerData.getFromPlayer(playerAttacking);
				
				if (playerData.isPlayerInfected()) {
					playerAttacked.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100));
				}
    		}
		}
	}

    // CLIENT & SERVER
    public class onLivingDeathListener {
        
		@SubscribeEvent
        public void LivingDeath(final LivingDeathEvent event) {
			
			LivingEntity entity = event.getEntityLiving();
			Entity attacker = event.getSource().getTrueSource();
			World world = entity.world;

			if (!world.isRemote && attacker != null) {

				if (attacker instanceof PlayerEntity) {
					
					if (Refs.souls.contains(entity.getType())) {

						IPlayerData playerData = PlayerData.getFromPlayer((PlayerEntity) attacker);
						
						if (playerData.isPlayerInfected() && !playerData.isPlayerCured() && !playerData.isMetDisabled()) {
							ColorOutOfSpace.Infection.checkDisableMet(world, (PlayerEntity) attacker, playerData);
						}
					}
				}
				else if (Refs.infectedDupEntities.contains(attacker.getType()) && attacker.hasCustomName() && attacker.isGlowing()) {
					Util.dupEntity(world, null, entity.getPosition(), attacker, entity, true, null);
				}
	        }
		}
    }

 	// SERVER
	public class onBlockBreakListener {
		
		@SubscribeEvent
		public void BlockBreak(final BreakEvent event) {

			IPlayerData playerData = PlayerData.getFromPlayer(event.getPlayer());
			
			if (playerData.isPlayerInfected() &&
    		   (!event.getState().equals(Refs.curedMetState) || !playerData.isMetDisabled() || !playerData.getMetPos().equals(event.getPos()))) {
    			event.getWorld().destroyBlock(event.getPos(), false);
    		}
		}
	}
	
	// CLIENT
	public class onRenderGameOverlayListener {
		
		@SubscribeEvent
		public void RenderGameOverlay(final RenderGameOverlayEvent.Text event) {
	    	ColorOutOfSpaceRender.showText(event.getMatrixStack());
		}
	}
	
	public static class Infection {
		
		public static boolean infectEntity(World worldIn, Entity entityIn) {
			
			boolean ret = false;
			
			if (entityIn instanceof PlayerEntity) {
				ret = ColorOutOfSpace.Infection.infectPlayer(worldIn, (PlayerEntity) entityIn);
			}
	    	else if (entityIn instanceof LivingEntity && !entityIn.hasCustomName() && !entityIn.isGlowing()) {
	    		ret = ColorOutOfSpace.Infection.infectLivingEntity(worldIn, entityIn);
	    	}
			return ret;
		}
		
		public static boolean infectPlayer(World worldIn, PlayerEntity player) {
			
			boolean ret = false;
			IPlayerData playerData = PlayerData.getFromPlayer(player);
	
			if (!playerData.isPlayerCured() && player.getActivePotionEffect(Effects.POISON) == null) {
				
				if (!playerData.isMetDisabled() || Refs.difficulty == Refs.HARDCORE) {
					ColorOutOfSpace.Infection.applyInfectedEffects(player, playerData.getMetDisableLevel(), true);
					ret = true;
				}

				if (!playerData.isPlayerInfected()) {
					playerData.setPlayerInfected(true);
					ColorEntity.spawnAntiPlayer((ServerWorld) worldIn, player, playerData.getMetPos().up(), false);
				}
			}
			return ret;
		}
		
		public static void applyInfectedEffects(PlayerEntity player, int cureLevel, boolean firstTime) {
			
			Networking.sendToClient(new PacketInfected(true, cureLevel, false), (ServerPlayerEntity) player);
			if (firstTime) {
				player.addPotionEffect(new EffectInstance(Effects.POISON, 200));
			}
			player.addPotionEffect(new EffectInstance(Effects.BAD_OMEN, Refs.timeIncrease));
			player.setGlowing(true);
		}
		
		public static boolean infectLivingEntity(World worldIn, Entity entityIn) {
			
			boolean ret = false;
			int i = Refs.entitiesToInfect.indexOf(entityIn.getType());
	
			if (i >= 0) {
				((LivingEntity) entityIn).setGlowing(true);
				((LivingEntity) entityIn).setHealth(2.0F);
				((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.INSTANT_DAMAGE, Refs.timeIncrease));
				Entity spawnedEntity = Util.spawnEntity(worldIn, null, entityIn.getPosition(), Refs.infectedEntities.get(i), true, null);
				spawnedEntity.setGlowing(true);
				ret = true;
			}
			else if (entityIn instanceof MonsterEntity && Refs.difficulty == Refs.HARDCORE) {
				entityIn.setGlowing(true);
				Util.applyPersistence(entityIn, null);
				// Even more hardcore: turn them all into ZOGLINGS!!!! (then keep anti-player jailed or they will fight each other)
				ret = true;
			}
			return ret;
		}
		
		public static void checkDisableMet(World worldIn, PlayerEntity player, IPlayerData playerData) {
			
			boolean justMetDisabled = playerData.checkMetDisableLevel();
			if (justMetDisabled) {
				worldIn.setBlockState(playerData.getMetPos(), Refs.curedMetState);
			}
			Networking.sendToClient(new PacketInfected(playerData.isPlayerInfected(), playerData.getMetDisableLevel(), justMetDisabled), (ServerPlayerEntity) player);
		}
	
		public static void curePlayer(World worldIn, PlayerEntity player, IPlayerData playerData, boolean usedAntidote) {
			
			player.clearActivePotions();
			player.setGlowing(false);
			
			if (usedAntidote) {
				Util.spawnMetSword(worldIn, player.getPosition(), true);
			}
			playerData.resetPlayer(usedAntidote);
			Networking.sendToClient(new PacketInfected(false, 0, usedAntidote), (ServerPlayerEntity) player);
		}
	}
}
